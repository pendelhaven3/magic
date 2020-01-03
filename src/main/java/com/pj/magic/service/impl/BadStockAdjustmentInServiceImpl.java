package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.BadStockAdjustmentInDao;
import com.pj.magic.dao.BadStockAdjustmentInItemDao;
import com.pj.magic.dao.BadStockDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.BadStockAdjustmentInItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.BadStockAdjustmentInSearchCriteria;
import com.pj.magic.service.BadStockAdjustmentInService;
import com.pj.magic.service.LoginService;

@Service
public class BadStockAdjustmentInServiceImpl implements BadStockAdjustmentInService {

    @Autowired
    private BadStockAdjustmentInDao badStockAdjustmentInDao;
    
    @Autowired
    private BadStockAdjustmentInItemDao badStockAdjustmentInItemDao;
    
    @Autowired
    private BadStockDao badStockDao;
    
    @Autowired
    private SystemDao systemDao;
    
    @Autowired
    private LoginService loginService;
    
    @Override
    public List<BadStockAdjustmentIn> getAllUnpostedBadStockAdjustmentIn() {
        BadStockAdjustmentInSearchCriteria criteria = new BadStockAdjustmentInSearchCriteria();
        criteria.setPosted(false);
        
        return badStockAdjustmentInDao.search(criteria);
    }

    @Override
    public BadStockAdjustmentIn getBadStockAdjustmentIn(Long id) {
        BadStockAdjustmentIn adjustmentIn = badStockAdjustmentInDao.get(id);
        adjustmentIn.setItems(badStockAdjustmentInItemDao.findAllByBadStockAdjustmentIn(adjustmentIn));
        return adjustmentIn;
    }

    @Transactional
    @Override
    public void save(BadStockAdjustmentIn adjustmentIn) {
        if (!adjustmentIn.isNew() && !adjustmentIn.isPosted()) {
            BadStockAdjustmentIn updated = badStockAdjustmentInDao.get(adjustmentIn.getId());
            if (updated.isPosted()) {
                return;
            }
        }
        
        badStockAdjustmentInDao.save(adjustmentIn);
    }

    @Transactional
    @Override
    public void save(BadStockAdjustmentInItem item) {
        badStockAdjustmentInItemDao.save(item);
    }

    @Transactional
    @Override
    public void delete(BadStockAdjustmentInItem item) {
        badStockAdjustmentInItemDao.delete(item);
    }

    @Transactional
    @Override
    public void post(BadStockAdjustmentIn adjustmentIn) {
        BadStockAdjustmentIn updated = getBadStockAdjustmentIn(adjustmentIn.getId());
        
        if (updated.isPosted()) {
            throw new AlreadyPostedException();
        }
        
        if (!updated.hasItems()) {
            throw new NoItemException(); 
        }
        
        for (BadStockAdjustmentInItem item : updated.getItems()) {
            BadStock badStock = getOrCreateBadStock(item.getProduct());
            badStock.addUnitQuantity(item.getUnit(), item.getQuantity());
            badStockDao.save(badStock);
        }
        
        updated.setPosted(true);
        updated.setPostDate(systemDao.getCurrentDateTime());
        updated.setPostedBy(loginService.getLoggedInUser());
        badStockAdjustmentInDao.save(updated);
    }

    private BadStock getOrCreateBadStock(Product product) {
        BadStock badStock = badStockDao.get(product.getId());
        if (badStock == null) {
            badStock = new BadStock(product);
        }
        return badStock;
    }

    @Override
    public List<BadStockAdjustmentIn> search(BadStockAdjustmentInSearchCriteria criteria) {
        return badStockAdjustmentInDao.search(criteria);
    }
    
}
