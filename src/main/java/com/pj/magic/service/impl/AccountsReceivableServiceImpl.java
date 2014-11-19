package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AccountsReceivableSummaryDao;
import com.pj.magic.dao.AccountsReceivableSummaryItemDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.AccountsReceivableSummary;
import com.pj.magic.model.AccountsReceivableSummaryItem;
import com.pj.magic.service.AccountsReceivableService;

@Service
public class AccountsReceivableServiceImpl implements AccountsReceivableService {

	@Autowired private AccountsReceivableSummaryDao accountsReceivableSummaryDao;
	@Autowired private AccountsReceivableSummaryItemDao accountsReceivableSummaryItemDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	
	@Transactional
	@Override
	public void save(AccountsReceivableSummary summary) {
		accountsReceivableSummaryDao.save(summary);
		for (AccountsReceivableSummaryItem item : summary.getItems()) {
			accountsReceivableSummaryItemDao.save(item);
		}
	}

	@Override
	public AccountsReceivableSummary getAccountsReceivableSummary(long id) {
		AccountsReceivableSummary summary = accountsReceivableSummaryDao.get(id);
		loadSummaryDetails(summary);
		return summary;
	}

	private void loadSummaryDetails(AccountsReceivableSummary summary) {
		summary.setItems(accountsReceivableSummaryItemDao.findAllByAccountsReceivableSummary(summary));
		for (AccountsReceivableSummaryItem item : summary.getItems()) {
			item.getSalesInvoice().setItems(salesInvoiceItemDao.findAllBySalesInvoice(item.getSalesInvoice()));
		}
	}
	
	@Override
	public List<AccountsReceivableSummary> getAllAccountsReceivableSummaries() {
		List<AccountsReceivableSummary> summaries = accountsReceivableSummaryDao.getAll();
		for (AccountsReceivableSummary summary : summaries) {
			loadSummaryDetails(summary);
		}
		return summaries;
	}

}