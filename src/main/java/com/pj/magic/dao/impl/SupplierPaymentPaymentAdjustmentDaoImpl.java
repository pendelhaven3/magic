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

import com.pj.magic.dao.SupplierPaymentPaymentAdjustmentDao;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentPaymentAdjustment;

@Repository
public class SupplierPaymentPaymentAdjustmentDaoImpl extends MagicDao 
		implements SupplierPaymentPaymentAdjustmentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, SUPPLIER_PAYMENT_ID, ADJUSTMENT_TYPE_ID, REFERENCE_NO, a.AMOUNT,"
			+ " b.CODE as ADJUSTMENT_TYPE_CODE"
			+ " from SUPP_PAYMENT_PAYMNT_ADJ a"
			+ " join PURCHASE_PAYMENT_ADJ_TYPE b"
			+ "   on b.ID = a.PURCHASE_PAYMENT_ADJ_TYPE_ID";
	
	private SupplierPaymentAdjustmentRowMapper adjustmentRowMapper = new SupplierPaymentAdjustmentRowMapper();
	
	@Override
	public void save(SupplierPaymentPaymentAdjustment adjustment) {
		if (adjustment.getId() == null) {
			insert(adjustment);
		} else {
			update(adjustment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into SUPP_PAYMENT_PAYMNT_ADJ"
			+ " (SUPPLIER_PAYMENT_ID, PURCHASE_PAYMENT_ADJ_TYPE_ID, REFERENCE_NO, AMOUNT) values (?, ?, ?, ?)";
	
	private void insert(final SupplierPaymentPaymentAdjustment adjustment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, adjustment.getParent().getId());
				ps.setLong(2, adjustment.getAdjustmentType().getId());
				ps.setString(3, adjustment.getReferenceNumber());
				ps.setBigDecimal(4, adjustment.getAmount());
				return ps;
			}
		}, holder);
		
		adjustment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update SUPP_PAYMENT_PAYMNT_ADJ"
			+ " set ADJUSTMENT_TYPE_ID = ?, REFERENCE_NO = ?, AMOUNT = ?"
			+ " where ID = ?";
	
	private void update(SupplierPaymentPaymentAdjustment adjustment) {
		getJdbcTemplate().update(UPDATE_SQL,
				adjustment.getAdjustmentType().getId(),
				adjustment.getReferenceNumber(),
				adjustment.getAmount(), 
				adjustment.getId());
	}

	private static final String FIND_ALL_BY_SUPPLIER_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.SUPPLIER_PAYMENT_ID = ?";
	
	@Override
	public List<SupplierPaymentPaymentAdjustment> findAllBySupplierPayment(SupplierPayment supplierPayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_PAYMENT_SQL, adjustmentRowMapper, supplierPayment.getId());
	}

	private class SupplierPaymentAdjustmentRowMapper implements RowMapper<SupplierPaymentPaymentAdjustment> {

		@Override
		public SupplierPaymentPaymentAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
			SupplierPaymentPaymentAdjustment adjustment = new SupplierPaymentPaymentAdjustment();
			adjustment.setId(rs.getLong("ID"));
			adjustment.setParent(new SupplierPayment(rs.getLong("SUPPLIER_PAYMENT_ID")));
			adjustment.setAdjustmentType(new PurchasePaymentAdjustmentType(
					rs.getLong("ADJUSTMENT_TYPE_ID"), rs.getString("ADJUSTMENT_TYPE_CODE")));
			adjustment.setReferenceNumber(rs.getString("REFERENCE_NO"));
			adjustment.setAmount(rs.getBigDecimal("AMOUNT"));
			return adjustment;
		}
		
	}
	
	private static final String DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL =
			"delete from SUPP_PAYMENT_PAYMNT_ADJ where SUPPLIER_PAYMENT_ID = ?";

	@Override
	public void deleteAllBySupplierPayment(SupplierPayment supplierPayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL, supplierPayment.getId());
	}

	private static final String DELETE_SQL = "delete from SUPP_PAYMENT_PAYMNT_ADJ where ID = ?";
	
	@Override
	public void delete(SupplierPaymentPaymentAdjustment paymentAdjustment) {
		getJdbcTemplate().update(DELETE_SQL, paymentAdjustment.getId());
	}
	
}