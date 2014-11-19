package com.pj.magic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AccountsReceivableSummaryDao;
import com.pj.magic.dao.AccountsReceivableSummaryItemDao;
import com.pj.magic.gui.tables.models.AccountsReceivableSummary;
import com.pj.magic.model.AccountsReceivableSummaryItem;
import com.pj.magic.service.AccountsReceivableService;

@Service
public class AccountsReceivableServiceImpl implements AccountsReceivableService {

	@Autowired private AccountsReceivableSummaryDao accountsReceivableSummaryDao;
	@Autowired private AccountsReceivableSummaryItemDao accountsReceivableSummaryItemDao;
	
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
		summary.setItems(accountsReceivableSummaryItemDao.findAllByAccountsReceivableSummary(summary));
		return summary;
	}

}
