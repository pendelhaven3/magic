package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;

public interface AdjustmentInService {

	void save(AdjustmentIn adjustmentIn);
	
	AdjustmentIn getAdjustmentIn(long id);

	void save(AdjustmentInItem item);

	void delete(AdjustmentInItem item);

	void delete(AdjustmentIn adjustmentIn);

	void post (AdjustmentIn adjustmentIn);
	
	List<AdjustmentIn> getAllNonPostedAdjustmentIns();
	
}
