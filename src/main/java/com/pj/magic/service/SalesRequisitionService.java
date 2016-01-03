package com.pj.magic.service;

import java.util.List;

import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;

public interface SalesRequisitionService {

	void save(SalesRequisition salesRequisition);
	
	SalesRequisition getSalesRequisition(long id);

	void save(SalesRequisitionItem item);

	void delete(SalesRequisitionItem item);

	void delete(SalesRequisition currentlySelectedSalesRequisition);

	SalesInvoice post (SalesRequisition salesRequisition) throws SalesRequisitionPostException;
	
	List<SalesRequisition> getAllNonPostedSalesRequisitions();

	SalesRequisition separatePerCaseItems(SalesRequisition salesRequisition);
	
}
