package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.search.BadStockAdjustmentInSearchCriteria;

public interface BadStockAdjustmentInDao {

    List<BadStockAdjustmentIn> search(BadStockAdjustmentInSearchCriteria criteria);

    BadStockAdjustmentIn get(Long id);

    void save(BadStockAdjustmentIn adjustmentIn);
    
}
