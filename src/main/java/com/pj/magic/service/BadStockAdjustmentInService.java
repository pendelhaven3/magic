package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.BadStockAdjustmentInItem;
import com.pj.magic.model.search.BadStockAdjustmentInSearchCriteria;

public interface BadStockAdjustmentInService {

    List<BadStockAdjustmentIn> getAllUnpostedBadStockAdjustmentIn();

    BadStockAdjustmentIn getBadStockAdjustmentIn(Long id);

    void save(BadStockAdjustmentIn adjustmentIn);

    void save(BadStockAdjustmentInItem item);

    void delete(BadStockAdjustmentInItem item);

    void post(BadStockAdjustmentIn adjustmentIn);

    List<BadStockAdjustmentIn> search(BadStockAdjustmentInSearchCriteria criteria);
    
}
