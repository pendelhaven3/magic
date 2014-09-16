package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AdjustmentOut;

public interface AdjustmentOutDao {

	AdjustmentOut get(long id);

	void save(AdjustmentOut adjustmentOut);

	void delete(AdjustmentOut adjustmentOut);

	List<AdjustmentOut> search(AdjustmentOut criteria);
	
}
