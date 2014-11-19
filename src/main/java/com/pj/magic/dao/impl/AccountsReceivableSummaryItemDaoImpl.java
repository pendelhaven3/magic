package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AccountsReceivableSummaryItemDao;
import com.pj.magic.model.AccountsReceivableSummary;
import com.pj.magic.model.AccountsReceivableSummaryItem;
import com.pj.magic.model.SalesInvoice;

@Repository
public class AccountsReceivableSummaryItemDaoImpl extends MagicDao implements AccountsReceivableSummaryItemDao {

	@Override
	public void save(AccountsReceivableSummaryItem item) {
		insert(item);
	}

	private static final String INSERT_SQL =
			"insert into ACCT_RECEIVABLE_SUMMARY_ITEM"
			+ " (ACCT_RECEIVABLE_SUMMARY_ID, SALES_INVOICE_ID) values (?, ?)";
	
	private void insert(AccountsReceivableSummaryItem item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getSalesInvoice().getId());
	}

	private static final String FIND_ALL_BY_ACCOUNTS_RECEIVABLE_SUMMARY_SQL =
			"   select a.ID, SALES_INVOICE_ID, b.SALES_INVOICE_NO"
			+ " from ACCT_RECEIVABLE_SUMMARY_ITEM a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID"
			+ " where ACCT_RECEIVABLE_SUMMARY_ID = ?";
	
	@Override
	public List<AccountsReceivableSummaryItem> findAllByAccountsReceivableSummary(
			final AccountsReceivableSummary summary) {
		return getJdbcTemplate().query(FIND_ALL_BY_ACCOUNTS_RECEIVABLE_SUMMARY_SQL, new RowMapper<AccountsReceivableSummaryItem>() {

			@Override
			public AccountsReceivableSummaryItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountsReceivableSummaryItem item = new AccountsReceivableSummaryItem();
				item.setId(rs.getLong("ID"));
				item.setParent(summary);
				
				SalesInvoice salesInvoice = new SalesInvoice();
				salesInvoice.setId(rs.getLong("SALES_INVOICE_ID"));
				salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
				item.setSalesInvoice(salesInvoice);
				
				return item;
			}
			
		}, summary.getId());
	}
	
}
