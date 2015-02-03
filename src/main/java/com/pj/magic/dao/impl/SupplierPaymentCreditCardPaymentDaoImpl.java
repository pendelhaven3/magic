package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
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

import com.pj.magic.dao.SupplierPaymentCreditCardPaymentDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCreditCardPayment;

@Repository
public class SupplierPaymentCreditCardPaymentDaoImpl extends MagicDao implements SupplierPaymentCreditCardPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, SUPPLIER_PAYMENT_ID, AMOUNT, CREDIT_CARD_ID, TRANSACTION_DT, APPROVAL_CODE,"
			+ " b.USER as CREDIT_CARD_USER, b.BANK as CREDIT_CARD_BANK"
			+ " from SUPP_PAYMENT_CREDITCARD_PYMNT a"
			+ " join CREDIT_CARD b"
			+ "   on b.ID = a.CREDIT_CARD_ID";
	
	private SupplierPaymentCreditCardPaymentRowMapper creditCardPaymentRowMapper = 
			new SupplierPaymentCreditCardPaymentRowMapper();
	
	@Override
	public void save(SupplierPaymentCreditCardPayment creditCardPayment) {
		if (creditCardPayment.getId() == null) {
			insert(creditCardPayment);
		} else {
			update(creditCardPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into SUPP_PAYMENT_CREDITCARD_PYMNT"
			+ " (SUPPLIER_PAYMENT_ID, AMOUNT, CREDIT_CARD_ID, TRANSACTION_DT, APPROVAL_CODE) values (?, ?, ?, ?, ?)";
	
	private void insert(final SupplierPaymentCreditCardPayment creditCardPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, creditCardPayment.getParent().getId());
				ps.setBigDecimal(2, creditCardPayment.getAmount());
				ps.setLong(3, creditCardPayment.getCreditCard().getId());
				ps.setDate(4, new Date(creditCardPayment.getTransactionDate().getTime()));
				ps.setString(5, creditCardPayment.getApprovalCode());
				return ps;
			}
		}, holder);
		
		creditCardPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update SUPP_PAYMENT_CREDITCARD_PYMNT"
			+ " set AMOUNT = ?, CREDIT_CARD_ID = ?, TRANSACTION_DT = ?, APPROVAL_CODE = ?"
			+ " where ID = ?";
	
	private void update(SupplierPaymentCreditCardPayment cashPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				cashPayment.getAmount(),
				cashPayment.getCreditCard().getId(),
				cashPayment.getTransactionDate(),
				cashPayment.getApprovalCode(),
				cashPayment.getId());
	}

	private static final String FIND_ALL_BY_SUPPLIER_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.SUPPLIER_PAYMENT_ID = ?";
	
	@Override
	public List<SupplierPaymentCreditCardPayment> findAllBySupplierPayment(SupplierPayment supplierPayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_PAYMENT_SQL, creditCardPaymentRowMapper, 
				supplierPayment.getId());
	}

	private class SupplierPaymentCreditCardPaymentRowMapper implements RowMapper<SupplierPaymentCreditCardPayment> {

		@Override
		public SupplierPaymentCreditCardPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			SupplierPaymentCreditCardPayment creditCardPayment = new SupplierPaymentCreditCardPayment();
			creditCardPayment.setId(rs.getLong("ID"));
			creditCardPayment.setParent(new SupplierPayment(rs.getLong("SUPPLIER_PAYMENT_ID")));
			creditCardPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			
			CreditCard creditCard = new CreditCard();
			creditCard.setId(rs.getLong("CREDIT_CARD_ID"));
			creditCard.setUser(rs.getString("CREDIT_CARD_USER"));
			creditCard.setBank(rs.getString("CREDIT_CARD_BANK"));
			creditCardPayment.setCreditCard(creditCard);
			
			creditCardPayment.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			creditCardPayment.setApprovalCode(rs.getString("APPROVAL_CODE"));
			return creditCardPayment;
		}
		
	}
	
	private static final String DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL =
			"delete from SUPP_PAYMENT_CREDITCARD_PYMNT where SUPPLIER_PAYMENT_ID = ?";

	@Override
	public void deleteAllBySupplierPayment(SupplierPayment supplierPayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL, supplierPayment.getId());
	}

	private static final String DELETE_SQL = "delete from SUPP_PAYMENT_CREDITCARD_PYMNT where ID = ?";
	
	@Override
	public void delete(SupplierPaymentCreditCardPayment cashPayment) {
		getJdbcTemplate().update(DELETE_SQL, cashPayment.getId());
	}

}