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

import com.pj.magic.dao.SupplierPaymentCashPaymentDao;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCashPayment;
import com.pj.magic.model.User;

@Repository
public class SupplierPaymentCashPaymentDaoImpl extends MagicDao implements SupplierPaymentCashPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, SUPPLIER_PAYMENT_ID, a.AMOUNT, PAID_DT, PAID_BY,"
			+ " b.USERNAME as PAID_BY_USERNAME"
			+ " from SUPP_PAYMENT_CASH_PYMNT a"
			+ " join USER b"
			+ "   on b.ID = a.PAID_BY"
			+ " join SUPPLIER_PAYMENT c"
			+ "   on c.ID = a.SUPPLIER_PAYMENT_ID";
	
	private SupplierPaymentCashPaymentRowMapper cashPaymentRowMapper = 
			new SupplierPaymentCashPaymentRowMapper();
	
	@Override
	public void save(SupplierPaymentCashPayment cashPayment) {
		if (cashPayment.getId() == null) {
			insert(cashPayment);
		} else {
			update(cashPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into SUPP_PAYMENT_CASH_PYMNT"
			+ " (SUPPLIER_PAYMENT_ID, AMOUNT, PAID_DT, PAID_BY) values (?, ?, ?, ?)";
	
	private void insert(final SupplierPaymentCashPayment cashPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, cashPayment.getParent().getId());
				ps.setBigDecimal(2, cashPayment.getAmount());
				ps.setDate(3, new Date(cashPayment.getPaidDate().getTime()));
				ps.setLong(4, cashPayment.getPaidBy().getId());
				return ps;
			}
		}, holder);
		
		cashPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update SUPP_PAYMENT_CASH_PYMNT"
			+ " set AMOUNT = ?, PAID_DT = ?, PAID_BY = ?"
			+ " where ID = ?";
	
	private void update(SupplierPaymentCashPayment cashPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				cashPayment.getAmount(), 
				cashPayment.getPaidDate(),
				cashPayment.getPaidBy().getId(),
				cashPayment.getId());
	}

	private static final String FIND_ALL_BY_SUPPLIER_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.SUPPLIER_PAYMENT_ID = ?";
	
	@Override
	public List<SupplierPaymentCashPayment> findAllBySupplierPayment(SupplierPayment supplierPayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_PAYMENT_SQL, cashPaymentRowMapper, 
				supplierPayment.getId());
	}

	private class SupplierPaymentCashPaymentRowMapper implements RowMapper<SupplierPaymentCashPayment> {

		@Override
		public SupplierPaymentCashPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			SupplierPaymentCashPayment cashPayment = new SupplierPaymentCashPayment();
			cashPayment.setId(rs.getLong("ID"));
			cashPayment.setParent(new SupplierPayment(rs.getLong("SUPPLIER_PAYMENT_ID")));
			cashPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			cashPayment.setPaidDate(rs.getDate("PAID_DT"));
			cashPayment.setPaidBy(
					new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
			return cashPayment;
		}
		
	}
	
	private static final String DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL =
			"delete from SUPP_PAYMENT_CASH_PYMNT where SUPPLIER_PAYMENT_ID = ?";

	@Override
	public void deleteAllBySupplierPayment(SupplierPayment supplierPayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL, supplierPayment.getId());
	}

	private static final String DELETE_SQL = "delete from SUPP_PAYMENT_CASH_PYMNT where ID = ?";
	
	@Override
	public void delete(SupplierPaymentCashPayment cashPayment) {
		getJdbcTemplate().update(DELETE_SQL, cashPayment.getId());
	}

}