package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesRequisition;

public interface SalesRequisitionDao {

	List<SalesRequisition> getAll();
	
	SalesRequisition get(long id);

	void save(SalesRequisition salesRequisition);

	void delete(SalesRequisition salesRequisition);
	
}
