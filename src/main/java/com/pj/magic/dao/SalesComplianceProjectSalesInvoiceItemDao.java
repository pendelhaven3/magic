package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.model.SalesComplianceProjectSalesInvoiceItem;

public interface SalesComplianceProjectSalesInvoiceItemDao {

	List<SalesComplianceProjectSalesInvoiceItem> findAllBySalesInvoice(SalesComplianceProjectSalesInvoice salesInvoice);

	void save(SalesComplianceProjectSalesInvoiceItem item);

	void removeAllBySalesInvoice(SalesComplianceProjectSalesInvoice salesInvoice);

}
