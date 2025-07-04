package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.model.SalesComplianceProjectSalesInvoiceItem;

public interface SalesComplianceService {

	List<SalesComplianceProject> getAllProjects();

	void save(SalesComplianceProject project);

	SalesComplianceProject getProject(Long id);

	SalesComplianceProjectSalesInvoice getSalesInvoice(Long id);

	void save(SalesComplianceProjectSalesInvoiceItem item);
	
}
