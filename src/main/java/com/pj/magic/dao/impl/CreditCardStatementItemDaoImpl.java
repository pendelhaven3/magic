package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CreditCardStatementItemDao;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;

@Repository
public class CreditCardStatementItemDaoImpl extends MagicDao implements CreditCardStatementItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, CREDIT_CARD_STATEMENT_ID, PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID,"
			+ " b.AMOUNT, b.TRANSACTION_DT"
			+ " from ADJUSTMENT_IN_ITEM a"
			+ " join PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT b"
			+ "   on b.ID = a.PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID";
	
	private CreditCardStatementItemRowMapper statementItemRowMapper =
			new CreditCardStatementItemRowMapper();
	
	private static final String FIND_ALL_BY_ADJUSTMENT_IN_SQL = BASE_SELECT_SQL 
			+ " where ADJUSTMENT_IN_ID = ?";
	
	@Override
	public List<CreditCardStatementItem> findAllByCreditCardStatement(CreditCardStatement statement) {
		List<CreditCardStatementItem> items = getJdbcTemplate().query(FIND_ALL_BY_ADJUSTMENT_IN_SQL, 
				statementItemRowMapper, statement.getId());
		for (CreditCardStatementItem item : items) {
			item.setParent(statement);
		}
		return items;
	}

	private class CreditCardStatementItemRowMapper implements RowMapper<CreditCardStatementItem> {

		@Override
		public CreditCardStatementItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			CreditCardStatementItem item = new CreditCardStatementItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new CreditCardStatement(rs.getLong("CREDIT_CARD_STATEMENT_ID")));
			item.setCreditCardPayment(mapCreditCardPayments(rs));
			return item;
		}

		private PurchasePaymentCreditCardPayment mapCreditCardPayments(ResultSet rs) throws SQLException {
			PurchasePaymentCreditCardPayment creditCardPayment =
					new PurchasePaymentCreditCardPayment();
			creditCardPayment.setId(rs.getLong("PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID"));
			creditCardPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			creditCardPayment.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			return creditCardPayment;
		}
		
	}

}
