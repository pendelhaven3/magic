package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.search.BadStockAdjustmentOutSearchCriteria;

public interface BadStockAdjustmentOutDao {

    List<BadStockAdjustmentOut> search(BadStockAdjustmentOutSearchCriteria criteria);

    BadStockAdjustmentOut get(Long id);

    void save(BadStockAdjustmentOut adjustmentOut);
    
}
