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

import com.pj.magic.dao.PurchasePaymentBankTransferDao;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;

@Repository
public class PurchasePaymentBankTransferDaoImpl extends MagicDao implements PurchasePaymentBankTransferDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, SUPPLIER_PAYMENT_ID, BANK, AMOUNT, REFERENCE_NO, TRANSFER_DT"
			+ " from SUPP_PAYMENT_BANK_TRANSFER a";
	
	private PurchasePaymentBankTransferRowMapper bankTransferRowMapper = 
			new PurchasePaymentBankTransferRowMapper();
	
	@Override
	public void save(PurchasePaymentBankTransfer bankTransfer) {
		if (bankTransfer.getId() == null) {
			insert(bankTransfer);
		} else {
			update(bankTransfer);
		}
	}

	private static final String INSERT_SQL = 
			"insert into SUPP_PAYMENT_BANK_TRANSFER"
			+ " (SUPPLIER_PAYMENT_ID, BANK, AMOUNT, REFERENCE_NO, TRANSFER_DT) values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentBankTransfer bankTransfer) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, bankTransfer.getParent().getId());
				ps.setString(2, bankTransfer.getBank());
				ps.setBigDecimal(3, bankTransfer.getAmount());
				ps.setString(4, bankTransfer.getReferenceNumber());
				ps.setDate(5, new java.sql.Date(bankTransfer.getTransferDate().getTime()));
				return ps;
			}
		}, holder);
		
		bankTransfer.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update SUPP_PAYMENT_BANK_TRANSFER"
			+ " set BANK = ?, AMOUNT = ?, REFERENCE_NO = ?, TRANSFER_DT = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentBankTransfer bankTransfer) {
		getJdbcTemplate().update(UPDATE_SQL, 
				bankTransfer.getBank(),
				bankTransfer.getAmount(),
				bankTransfer.getReferenceNumber(),
				bankTransfer.getTransferDate(),
				bankTransfer.getId());
	}

	private static final String FIND_ALL_BY_SUPPLIER_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.SUPPLIER_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentBankTransfer> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_PAYMENT_SQL, bankTransferRowMapper, 
				purchasePayment.getId());
	}

	private class PurchasePaymentBankTransferRowMapper implements RowMapper<PurchasePaymentBankTransfer> {

		@Override
		public PurchasePaymentBankTransfer mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer();
			bankTransfer.setId(rs.getLong("ID"));
			bankTransfer.setParent(new PurchasePayment(rs.getLong("SUPPLIER_PAYMENT_ID")));
			bankTransfer.setBank(rs.getString("BANK"));
			bankTransfer.setReferenceNumber(rs.getString("REFERENCE_NO"));
			bankTransfer.setAmount(rs.getBigDecimal("AMOUNT"));
			bankTransfer.setTransferDate(rs.getDate("TRANSFER_DT"));
			return bankTransfer;
		}
		
	}
	
	private static final String DELETE_ALL_BY_PURCHASE_PAYMENT_SQL =
			"delete from SUPP_PAYMENT_BANK_TRANSFER where SUPPLIER_PAYMENT_ID = ?";

	@Override
	public void deleteAllByPurchasePayment(PurchasePayment purchasePayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PURCHASE_PAYMENT_SQL, purchasePayment.getId());
	}
	
	private static final String DELETE_SQL = "delete from SUPP_PAYMENT_BANK_TRANSFER where ID = ?";
	
	@Override
	public void delete(PurchasePaymentBankTransfer bankTransfer) {
		getJdbcTemplate().update(DELETE_SQL, bankTransfer.getId());
	}

}