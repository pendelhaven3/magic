package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CreditCardStatementItemDao;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.Supplier;

@Repository
public class CreditCardStatementItemDaoImpl extends MagicDao implements CreditCardStatementItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, CREDIT_CARD_STATEMENT_ID, PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID,"
			+ " b.AMOUNT, b.TRANSACTION_DT,"
			+ " c.PURCHASE_PAYMENT_NO,"
			+ " d.NAME as SUPPLIER_NAME"
			+ " from CREDIT_CARD_STATEMENT_ITEM a"
			+ " join PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT b"
			+ "   on b.ID = a.PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID"
			+ " join PURCHASE_PAYMENT c"
			+ "   on c.ID = b.PURCHASE_PAYMENT_ID"
			+ " join SUPPLIER d"
			+ "   on d.ID = c.SUPPLIER_ID";
	
	private CreditCardStatementItemRowMapper statementItemRowMapper =
			new CreditCardStatementItemRowMapper();
	
	private static final String FIND_ALL_BY_CREDIT_CARD_STATEMENT_SQL = BASE_SELECT_SQL 
			+ " where CREDIT_CARD_STATEMENT_ID = ?";
	
	@Override
	public List<CreditCardStatementItem> findAllByCreditCardStatement(CreditCardStatement statement) {
		List<CreditCardStatementItem> items = getJdbcTemplate().query(FIND_ALL_BY_CREDIT_CARD_STATEMENT_SQL, 
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
			creditCardPayment.setParent(mapPurchasePayment(rs));
			creditCardPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			creditCardPayment.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			return creditCardPayment;
		}

		private PurchasePayment mapPurchasePayment(ResultSet rs) throws SQLException {
			PurchasePayment purchasePayment = new PurchasePayment();
			purchasePayment.setPurchasePaymentNumber(rs.getLong("PURCHASE_PAYMENT_NO"));
			purchasePayment.setSupplier(new Supplier());
			purchasePayment.getSupplier().setName(rs.getString("SUPPLIER_NAME"));
			return purchasePayment;
		}
		
	}

	private static final String INSERT_SQL =
			"insert into CREDIT_CARD_STATEMENT_ITEM"
			+ " (CREDIT_CARD_STATEMENT_ID, PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID)"
			+ " values (?, ?)";
	
	@Override
	public void save(final CreditCardStatementItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setLong(2, item.getCreditCardPayment().getId());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

}
