package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AccountsReceivableSummary;

public interface AccountsReceivableService {

	void save(AccountsReceivableSummary summary);

	AccountsReceivableSummary getAccountsReceivableSummary(long id);

	List<AccountsReceivableSummary> getAllAccountsReceivableSummaries();
	
}
