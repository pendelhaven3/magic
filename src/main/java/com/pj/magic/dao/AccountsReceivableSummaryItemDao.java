package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AccountsReceivableSummary;
import com.pj.magic.model.AccountsReceivableSummaryItem;

public interface AccountsReceivableSummaryItemDao {

	void save(AccountsReceivableSummaryItem item);

	List<AccountsReceivableSummaryItem> findAllByAccountsReceivableSummary(AccountsReceivableSummary summary);
	
}
