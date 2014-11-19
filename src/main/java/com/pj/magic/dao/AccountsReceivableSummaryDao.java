package com.pj.magic.dao;

import com.pj.magic.gui.tables.models.AccountsReceivableSummary;

public interface AccountsReceivableSummaryDao {

	void save(AccountsReceivableSummary summary);
	
	AccountsReceivableSummary get(long id);
	
}
