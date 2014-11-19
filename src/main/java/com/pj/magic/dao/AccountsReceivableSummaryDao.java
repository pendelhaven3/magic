package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AccountsReceivableSummary;

public interface AccountsReceivableSummaryDao {

	void save(AccountsReceivableSummary summary);
	
	AccountsReceivableSummary get(long id);

	List<AccountsReceivableSummary> getAll();
	
}
