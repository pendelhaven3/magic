package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.BadStockAdjustmentInItem;

public interface BadStockAdjustmentInItemDao {

    void save(BadStockAdjustmentInItem item);

    void delete(BadStockAdjustmentInItem item);

    List<BadStockAdjustmentInItem> findAllByBadStockAdjustmentIn(BadStockAdjustmentIn adjustmentIn);
    
}
