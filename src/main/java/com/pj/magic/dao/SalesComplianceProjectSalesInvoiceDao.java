package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;

public interface SalesComplianceProjectSalesInvoiceDao {

	List<SalesComplianceProjectSalesInvoice> findAllBySalesComplianceProject(SalesComplianceProject project);

	void save(SalesComplianceProjectSalesInvoice projectSalesInvoice);

	SalesComplianceProjectSalesInvoice get(Long id);

}