package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AccountsReceivableSummaryDao;
import com.pj.magic.gui.tables.models.AccountsReceivableSummary;
import com.pj.magic.model.Customer;

@Repository
public class AccountsReceivableSummaryDaoImpl extends MagicDao implements AccountsReceivableSummaryDao {

	private static final String ACCOUNTS_RECEIVABLE_SUMMARY_NUMBER_SEQUENCE = "ACCT_RECEIVABLE_SUMMARY_NO_SEQ";
	
	private static final String BASE_SELECT_SQL = 
			"select a.ID, ACCT_RECEIVABLE_SUMMARY_NO, a.CUSTOMER_ID"
			+ " from ACCT_RECEIVABLE_SUMMARY a";
	
	private AccountsReceivableSummaryRowMapper rowMapper = new AccountsReceivableSummaryRowMapper();
	
	@Override
	public void save(AccountsReceivableSummary summary) {
		if (summary.getId() == null) {
			insert(summary);
		} else {
			update(summary);
		}
	}

	private void update(AccountsReceivableSummary summary) {
	}

	private static final String INSERT_SQL =
			"insert into ACCT_RECEIVABLE_SUMMARY (ACCT_RECEIVABLE_SUMMARY_NO, CUSTOMER_ID) values (?, ?)";
	
	private void insert(final AccountsReceivableSummary summary) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextAccountsReceivableSummaryNumber());
				ps.setLong(2, summary.getCustomer().getId());
				return ps;
			}
		}, holder);
		
		AccountsReceivableSummary updated = get(holder.getKey().longValue());
		summary.setId(updated.getId());
		summary.setAccountReceivableSummaryNumber(updated.getAccountReceivableSummaryNumber());
	}

	private long getNextAccountsReceivableSummaryNumber() {
		return getNextSequenceValue(ACCOUNTS_RECEIVABLE_SUMMARY_NUMBER_SEQUENCE);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public AccountsReceivableSummary get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private class AccountsReceivableSummaryRowMapper implements RowMapper<AccountsReceivableSummary> {

		@Override
		public AccountsReceivableSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
			AccountsReceivableSummary summary = new AccountsReceivableSummary();
			summary.setId(rs.getLong("ID"));
			summary.setAccountReceivableSummaryNumber(rs.getLong("ACCT_RECEIVABLE_SUMMARY_NO"));
			summary.setCustomer(new Customer(rs.getLong("CUSTOMER_ID")));
			return summary;
		}
		
	}
	
}
