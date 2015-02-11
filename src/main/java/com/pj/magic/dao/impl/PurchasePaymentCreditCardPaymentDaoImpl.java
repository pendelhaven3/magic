package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PurchasePaymentCreditCardPaymentDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchasePaymentCreditCardPaymentDaoImpl extends MagicDao implements PurchasePaymentCreditCardPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, SUPPLIER_PAYMENT_ID, AMOUNT, CREDIT_CARD_ID, TRANSACTION_DT, APPROVAL_CODE,"
			+ " b.SUPPLIER_PAYMENT_NO,"
			+ " c.USER as CREDIT_CARD_USER, c.BANK as CREDIT_CARD_BANK,"
			+ " d.NAME as SUPPLIER_NAME"
			+ " from SUPP_PAYMENT_CREDITCARD_PYMNT a"
			+ " join SUPPLIER_PAYMENT b"
			+ "   on b.ID = a.SUPPLIER_PAYMENT_ID"
			+ " join CREDIT_CARD C"
			+ "   on c.ID = a.CREDIT_CARD_ID"
			+ " join SUPPLIER d"
			+ "   on d.ID = b.SUPPLIER_ID";
	
	private PurchasePaymentCreditCardPaymentRowMapper creditCardPaymentRowMapper = 
			new PurchasePaymentCreditCardPaymentRowMapper();
	
	@Override
	public void save(PurchasePaymentCreditCardPayment creditCardPayment) {
		if (creditCardPayment.getId() == null) {
			insert(creditCardPayment);
		} else {
			update(creditCardPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into SUPP_PAYMENT_CREDITCARD_PYMNT"
			+ " (SUPPLIER_PAYMENT_ID, AMOUNT, CREDIT_CARD_ID, TRANSACTION_DT, APPROVAL_CODE) values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentCreditCardPayment creditCardPayment) {
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
	
	private void update(PurchasePaymentCreditCardPayment cashPayment) {
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
	public List<PurchasePaymentCreditCardPayment> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_PAYMENT_SQL, creditCardPaymentRowMapper, 
				purchasePayment.getId());
	}

	private class PurchasePaymentCreditCardPaymentRowMapper implements RowMapper<PurchasePaymentCreditCardPayment> {

		@Override
		public PurchasePaymentCreditCardPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment();
			creditCardPayment.setId(rs.getLong("ID"));
			
			PurchasePayment purchasePayment = new PurchasePayment();
			purchasePayment.setId(rs.getLong("SUPPLIER_PAYMENT_ID"));
			purchasePayment.setPurchasePaymentNumber(rs.getLong("SUPPLIER_PAYMENT_NO"));
			
			Supplier supplier = new Supplier();
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			purchasePayment.setSupplier(supplier);
			creditCardPayment.setParent(purchasePayment);
			
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
	
	private static final String DELETE_ALL_BY_PURCHASE_PAYMENT_SQL =
			"delete from SUPP_PAYMENT_CREDITCARD_PYMNT where SUPPLIER_PAYMENT_ID = ?";

	@Override
	public void deleteAllByPurchasePayment(PurchasePayment purchasePayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PURCHASE_PAYMENT_SQL, purchasePayment.getId());
	}

	private static final String DELETE_SQL = "delete from SUPP_PAYMENT_CREDITCARD_PYMNT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentCreditCardPayment cashPayment) {
		getJdbcTemplate().update(DELETE_SQL, cashPayment.getId());
	}

	@Override
	public List<PurchasePaymentCreditCardPayment> search(
			PurchasePaymentCreditCardPaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getSupplier() != null) {
			sql.append(" and c.ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and b.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getFromDate() != null) {
			sql.append(" and a.TRANSACTION_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and a.TRANSACTION_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		sql.append(" order by a.ID desc");
		
		return getJdbcTemplate().query(sql.toString(), creditCardPaymentRowMapper, params.toArray());
	}

}