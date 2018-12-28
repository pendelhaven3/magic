package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.BadStockAdjustmentInItem;

public interface BadStockAdjustmentInService {

    List<BadStockAdjustmentIn> getAllUnpostedBadStockAdjustmentIn();

    BadStockAdjustmentIn getBadStockAdjustmentIn(Long id);

    void save(BadStockAdjustmentIn adjustmentIn);

    void save(BadStockAdjustmentInItem item);

    void delete(BadStockAdjustmentInItem item);

    void post(BadStockAdjustmentIn adjustmentIn);
    
}
