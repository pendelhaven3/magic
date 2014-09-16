package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AdjustmentIn;

public interface AdjustmentInDao {

	AdjustmentIn get(long id);

	void save(AdjustmentIn adjustmentOut);

	void delete(AdjustmentIn adjustmentOut);

	List<AdjustmentIn> search(AdjustmentIn criteria);
	
}
