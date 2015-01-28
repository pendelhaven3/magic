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
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCreditCardPayment;
import com.pj.magic.model.User;

@Repository
public class SupplierPaymentCreditCardPaymentDaoImpl extends MagicDao implements SupplierPaymentCreditCardPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, SUPPLIER_PAYMENT_ID, a.AMOUNT, BANK, PAID_DT, PAID_BY,"
			+ " b.USERNAME as PAID_BY_USERNAME"
			+ " from SUPP_PAYMENT_CREDITCARD_PYMNT a"
			+ " join USER b"
			+ "   on b.ID = a.PAID_BY"
			+ " join SUPPLIER_PAYMENT c"
			+ "   on c.ID = a.SUPPLIER_PAYMENT_ID";
	
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
			+ " (SUPPLIER_PAYMENT_ID, AMOUNT, BANK, PAID_DT, PAID_BY) values (?, ?, ?, ?, ?)";
	
	private void insert(final SupplierPaymentCreditCardPayment creditCardPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, creditCardPayment.getParent().getId());
				ps.setBigDecimal(2, creditCardPayment.getAmount());
				ps.setString(3, creditCardPayment.getBank());
				ps.setDate(4, new Date(creditCardPayment.getPaidDate().getTime()));
				ps.setLong(5, creditCardPayment.getPaidBy().getId());
				return ps;
			}
		}, holder);
		
		creditCardPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update SUPP_PAYMENT_CREDITCARD_PYMNT"
			+ " set AMOUNT = ?, BANK = ?, PAID_DT = ?, PAID_BY = ?"
			+ " where ID = ?";
	
	private void update(SupplierPaymentCreditCardPayment cashPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				cashPayment.getAmount(),
				cashPayment.getBank(),
				cashPayment.getPaidDate(),
				cashPayment.getPaidBy().getId(),
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
			creditCardPayment.setBank(rs.getString("BANK"));
			creditCardPayment.setPaidDate(rs.getDate("PAID_DT"));
			creditCardPayment.setPaidBy(
					new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
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