package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadStockAdjustmentInDao;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.search.BadStockAdjustmentInSearchCriteria;
import com.pj.magic.service.BadStockAdjustmentInService;

@Service
public class BadStockAdjustmentInServiceImpl implements BadStockAdjustmentInService {

    @Autowired
    private BadStockAdjustmentInDao badStockAdjustmentInDao;
    
    @Override
    public List<BadStockAdjustmentIn> getAllUnpostedBadStockAdjustmentIn() {
        BadStockAdjustmentInSearchCriteria criteria = new BadStockAdjustmentInSearchCriteria();
        criteria.setPosted(false);
        
        return badStockAdjustmentInDao.search(criteria);
    }

}
