package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SalesComplianceProjectDao;
import com.pj.magic.dao.SalesComplianceProjectSalesInvoiceDao;
import com.pj.magic.dao.SalesComplianceProjectSalesInvoiceItemDao;
import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.model.SalesComplianceProjectSalesInvoiceItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.SalesComplianceService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SystemService;

@Service
public class SalesComplianceServiceImpl implements SalesComplianceService {

	@Autowired private SalesComplianceProjectDao salesComplianceProjectDao;
	@Autowired private SalesComplianceProjectSalesInvoiceDao salesComplianceProjectSalesInvoiceDao;
	@Autowired private SalesComplianceProjectSalesInvoiceItemDao salesComplianceProjectSalesInvoiceItemDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SystemService systemService;
	
	@Override
	public List<SalesComplianceProject> getAllProjects() {
		return salesComplianceProjectDao.getAll();
	}

	@Override
	@Transactional
	public void save(SalesComplianceProject project) {
		boolean isNew = (project.getId() == null);
		
		salesComplianceProjectDao.save(project);
		
		if (isNew) {
			createProjectSalesInvoices(project);
		}
	}

	private void createProjectSalesInvoices(SalesComplianceProject project) {
		for (SalesInvoice salesInvoice : findAllSalesInvoicesWithPrintInvoiceNumberByTransactionDate(project.getStartDate(), project.getEndDate())) {
			SalesComplianceProjectSalesInvoice projectSalesInvoice = new SalesComplianceProjectSalesInvoice();
			projectSalesInvoice.setSalesComplianceProject(project);
			projectSalesInvoice.setSalesInvoice(salesInvoice);
			salesComplianceProjectSalesInvoiceDao.save(projectSalesInvoice);
			
			for (SalesInvoiceItem item : salesInvoice.getItems()) {
				SalesComplianceProjectSalesInvoiceItem projectSalesInvoiceItem = new SalesComplianceProjectSalesInvoiceItem(item);
				projectSalesInvoiceItem.setParent(projectSalesInvoice);
				salesComplianceProjectSalesInvoiceItemDao.save(projectSalesInvoiceItem);
			}
		}
	}

	private List<SalesInvoice> findAllSalesInvoicesWithPrintInvoiceNumberByTransactionDate(Date startDate, Date endDate) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setHasPrintInvoiceNumber(true);
		criteria.setTransactionDateFrom(startDate);
		criteria.setTransactionDateTo(endDate);
		return salesInvoiceService.search(criteria);
	}

	@Override
	public SalesComplianceProject getProject(Long id) {
		SalesComplianceProject project = salesComplianceProjectDao.get(id);
		project.setSalesInvoices(salesComplianceProjectSalesInvoiceDao.findAllBySalesComplianceProject(project));
		
		for (SalesComplianceProjectSalesInvoice salesInvoice : project.getSalesInvoices()) {
			salesInvoice.setItems(salesComplianceProjectSalesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		
		return project;
	}

	@Override
	public SalesComplianceProjectSalesInvoice getSalesInvoice(Long id) {
		SalesComplianceProjectSalesInvoice salesInvoice = salesComplianceProjectSalesInvoiceDao.get(id);
		salesInvoice.setItems(salesComplianceProjectSalesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
        salesInvoice.setVatAmount(salesInvoice.getTotalNetAmount().divide(new BigDecimal("1.12"), RoundingMode.HALF_UP).multiply(systemService.getVatRate())
                .setScale(2, RoundingMode.HALF_UP));
		return salesInvoice;
	}

	@Override
	@Transactional
	public void save(SalesComplianceProjectSalesInvoiceItem item) {
		salesComplianceProjectSalesInvoiceItemDao.save(item);
	}

	@Override
	@Transactional
	public void remove(SalesComplianceProjectSalesInvoice salesInvoice) {
		salesComplianceProjectSalesInvoiceItemDao.removeAllBySalesInvoice(salesInvoice);
		salesComplianceProjectSalesInvoiceDao.remove(salesInvoice);
	}

	@Override
	@Transactional
	public void recreate(SalesComplianceProject salesComplianceProject) {
		for (SalesComplianceProjectSalesInvoice projectSalesInvoice : salesComplianceProjectSalesInvoiceDao.findAllBySalesComplianceProject(salesComplianceProject)) {
			remove(projectSalesInvoice);
		}
		createProjectSalesInvoices(salesComplianceProject);
	}

}
