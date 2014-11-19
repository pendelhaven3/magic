package com.pj.magic.service;

import com.pj.magic.gui.tables.models.AccountsReceivableSummary;

public interface AccountsReceivableService {

	void save(AccountsReceivableSummary summary);

	AccountsReceivableSummary getAccountsReceivableSummary(long id);
	
}
