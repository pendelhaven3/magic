package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.NoMoreStockAdjustmentItem;

public interface NoMoreStockAdjustmentItemDao {

	List<NoMoreStockAdjustmentItem> findAllByNoMoreStockAdjustment(NoMoreStockAdjustment noMoreStockAdjustment);

	void save(NoMoreStockAdjustmentItem item);

	void delete(NoMoreStockAdjustmentItem item);

	void deleteAllByNoMoreStockAdjustment(NoMoreStockAdjustment noMoreStockAdjustment);
	
}