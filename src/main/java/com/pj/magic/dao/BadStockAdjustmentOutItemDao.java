package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.BadStockAdjustmentOutItem;

public interface BadStockAdjustmentOutItemDao {

    void save(BadStockAdjustmentOutItem item);

    void delete(BadStockAdjustmentOutItem item);

    List<BadStockAdjustmentOutItem> findAllByBadStockAdjustmentOut(BadStockAdjustmentOut adjustmentOut);
    
}
