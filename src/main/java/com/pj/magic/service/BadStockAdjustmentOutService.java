package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.BadStockAdjustmentOutItem;
import com.pj.magic.model.search.BadStockAdjustmentOutSearchCriteria;

public interface BadStockAdjustmentOutService {

    List<BadStockAdjustmentOut> getAllUnpostedBadStockAdjustmentOut();

    BadStockAdjustmentOut getBadStockAdjustmentOut(Long id);

    void save(BadStockAdjustmentOut AdjustmentOut);

    void save(BadStockAdjustmentOutItem item);

    void delete(BadStockAdjustmentOutItem item);

    void post(BadStockAdjustmentOut AdjustmentOut);

    List<BadStockAdjustmentOut> search(BadStockAdjustmentOutSearchCriteria criteria);
    
}
