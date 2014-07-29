package com.pj.magic.service;

import java.util.List;

import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;

public interface SalesRequisitionService {

	List<SalesRequisition> getAllSalesRequisitions();
	
	void save(SalesRequisition salesRequisition);
	
	SalesRequisition getSalesRequisition(long id);

	void save(SalesRequisitionItem item);

	void delete(SalesRequisitionItem item);

	void delete(SalesRequisition currentlySelectedSalesRequisition);

	void post(SalesRequisition salesRequisition) throws NotEnoughStocksException;
	
	List<SalesRequisition> getAllNonPostedSalesRequisitions();
	
}
