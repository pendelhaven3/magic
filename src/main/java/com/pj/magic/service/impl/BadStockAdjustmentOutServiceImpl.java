package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.BadStockAdjustmentOutDao;
import com.pj.magic.dao.BadStockAdjustmentOutItemDao;
import com.pj.magic.dao.BadStockDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.BadStockAdjustmentOutItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.BadStockAdjustmentOutSearchCriteria;
import com.pj.magic.service.BadStockAdjustmentOutService;
import com.pj.magic.service.LoginService;

@Service
public class BadStockAdjustmentOutServiceImpl implements BadStockAdjustmentOutService {

    @Autowired
    private BadStockAdjustmentOutDao badStockAdjustmentOutDao;

    @Autowired
    private BadStockAdjustmentOutItemDao badStockAdjustmentOutItemDao;

    @Autowired
    private BadStockDao badStockDao;

    @Autowired
    private SystemDao systemDao;

    @Autowired
    private LoginService loginService;

    @Override
    public List<BadStockAdjustmentOut> getAllUnpostedBadStockAdjustmentOut() {
        BadStockAdjustmentOutSearchCriteria criteria = new BadStockAdjustmentOutSearchCriteria();
        criteria.setPosted(false);

        return badStockAdjustmentOutDao.search(criteria);
    }

    @Override
    public BadStockAdjustmentOut getBadStockAdjustmentOut(Long id) {
        BadStockAdjustmentOut adjustmentOut = badStockAdjustmentOutDao.get(id);
        adjustmentOut.setItems(badStockAdjustmentOutItemDao.findAllByBadStockAdjustmentOut(adjustmentOut));
        return adjustmentOut;
    }

    @Transactional
    @Override
    public void save(BadStockAdjustmentOut adjustmentOut) {
        if (!adjustmentOut.isNew() && !adjustmentOut.isPosted()) {
            BadStockAdjustmentOut updated = badStockAdjustmentOutDao.get(adjustmentOut.getId());
            if (updated.isPosted()) {
                return;
            }
        }

        badStockAdjustmentOutDao.save(adjustmentOut);
    }

    @Transactional
    @Override
    public void save(BadStockAdjustmentOutItem item) {
        badStockAdjustmentOutItemDao.save(item);
    }

    @Transactional
    @Override
    public void delete(BadStockAdjustmentOutItem item) {
        badStockAdjustmentOutItemDao.delete(item);
    }

    @Transactional
    @Override
    public void post(BadStockAdjustmentOut adjustmentOut) {
        BadStockAdjustmentOut updated = getBadStockAdjustmentOut(adjustmentOut.getId());

        if (updated.isPosted()) {
            throw new AlreadyPostedException();
        }

        if (!updated.hasItems()) {
            throw new NoItemException();
        }

        for (BadStockAdjustmentOutItem item : updated.getItems()) {
            BadStock badStock = getOrCreateBadStock(item.getProduct());
            if (!badStock.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
                throw new NotEnoughStocksException(item);
            }
            badStock.addUnitQuantity(item.getUnit(), -item.getQuantity());
            badStockDao.save(badStock);
        }

        updated.setPosted(true);
        updated.setPostDate(systemDao.getCurrentDateTime());
        updated.setPostedBy(loginService.getLoggedInUser());
        badStockAdjustmentOutDao.save(updated);
    }

    private BadStock getOrCreateBadStock(Product product) {
        BadStock badStock = badStockDao.get(product.getId());
        if (badStock == null) {
            badStock = new BadStock(product);
        }
        return badStock;
    }

    @Override
    public List<BadStockAdjustmentOut> search(BadStockAdjustmentOutSearchCriteria criteria) {
        return badStockAdjustmentOutDao.search(criteria);
    }

}
