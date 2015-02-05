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

import com.pj.magic.dao.PurchasePaymentCheckPaymentDao;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;

@Repository
public class PurchasePaymentCheckPaymentDaoImpl extends MagicDao implements PurchasePaymentCheckPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, SUPPLIER_PAYMENT_ID, BANK, CHECK_DT, CHECK_NO, AMOUNT,"
			+ " b.SUPPLIER_ID, c.NAME as SUPPLIER_NAME"
			+ " from SUPP_PAYMENT_CHECK_PYMNT a"
			+ " join SUPPLIER_PAYMENT b"
			+ "   on b.ID = a.SUPPLIER_PAYMENT_ID"
			+ " join SUPPLIER c"
			+ "   on c.ID = b.SUPPLIER_ID";
	
	private PurchasePaymentCheckPaymentRowMapper checkPaymentRowMapper = 
			new PurchasePaymentCheckPaymentRowMapper();
	
	@Override
	public void save(PurchasePaymentCheckPayment checkPayment) {
		if (checkPayment.getId() == null) {
			insert(checkPayment);
		} else {
			update(checkPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into SUPP_PAYMENT_CHECK_PYMNT"
			+ " (SUPPLIER_PAYMENT_ID, BANK, CHECK_DT, CHECK_NO, AMOUNT) values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentCheckPayment checkPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, checkPayment.getParent().getId());
				ps.setString(2, checkPayment.getBank());
				ps.setDate(3, new Date(checkPayment.getCheckDate().getTime()));
				ps.setString(4, checkPayment.getCheckNumber());
				ps.setBigDecimal(5, checkPayment.getAmount());
				return ps;
			}
		}, holder);
		
		checkPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update SUPP_PAYMENT_CHECK_PYMNT"
			+ " set BANK = ?, CHECK_DT = ?, CHECK_NO = ?, AMOUNT = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentCheckPayment checkPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				checkPayment.getBank(), 
				checkPayment.getCheckDate(),
				checkPayment.getCheckNumber(),
				checkPayment.getAmount(),
				checkPayment.getId());
	}

	private static final String FIND_ALL_BY_SUPPLIER_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.SUPPLIER_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentCheckPayment> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_PAYMENT_SQL, checkPaymentRowMapper, 
				purchasePayment.getId());
	}

	private class PurchasePaymentCheckPaymentRowMapper implements RowMapper<PurchasePaymentCheckPayment> {

		@Override
		public PurchasePaymentCheckPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentCheckPayment check = new PurchasePaymentCheckPayment();
			check.setId(rs.getLong("ID"));
			check.setParent(new PurchasePayment(rs.getLong("SUPPLIER_PAYMENT_ID")));
			check.setBank(rs.getString("BANK"));
			check.setCheckDate(rs.getDate("CHECK_DT"));
			check.setCheckNumber(rs.getString("CHECK_NO"));
			check.setAmount(rs.getBigDecimal("AMOUNT"));
			
			check.getParent().setSupplier(
					new Supplier(rs.getLong("SUPPLIER_ID"), rs.getString("SUPPLIER_NAME")));
			
			return check;
		}
		
	}
	
	private static final String DELETE_ALL_BY_PURCHASE_PAYMENT_SQL =
			"delete from SUPP_PAYMENT_CHECK_PYMNT where PAYMENT_ID = ?";

	@Override
	public void deleteAllByPurchasePayment(PurchasePayment purchasePayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PURCHASE_PAYMENT_SQL, purchasePayment.getId());
	}

	private static final String DELETE_SQL = "delete from SUPP_PAYMENT_CHECK_PYMNT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentCheckPayment checkPayment) {
		getJdbcTemplate().update(DELETE_SQL, checkPayment.getId());
	}

}