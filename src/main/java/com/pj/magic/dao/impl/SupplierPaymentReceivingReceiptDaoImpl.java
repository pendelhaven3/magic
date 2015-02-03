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

import com.pj.magic.dao.SupplierPaymentReceivingReceiptDao;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentReceivingReceipt;

@Repository
public class SupplierPaymentReceivingReceiptDaoImpl extends MagicDao implements SupplierPaymentReceivingReceiptDao {

	private static final String BASE_SELECT_SQL =
			"   select a.ID, SUPPLIER_PAYMENT_ID, RECEIVING_RECEIPT_ID,"
			+ " b.RECEIVING_RECEIPT_NO, b.RECEIVED_DT, b.VAT_INCLUSIVE, b.VAT_RATE"
			+ " from SUPP_PAYMENT_RECV_RCPT a"
			+ " join RECEIVING_RECEIPT b"
			+ "   on b.ID = a.RECEIVING_RECEIPT_ID";
	
	private SupplierPaymentReceivingReceiptRowMapper rowMapper = new SupplierPaymentReceivingReceiptRowMapper();
	
	private static final String INSERT_SQL =
			"insert into SUPP_PAYMENT_RECV_RCPT"
			+ " (SUPPLIER_PAYMENT_ID, RECEIVING_RECEIPT_ID) values (?, ?)";
	
	@Override
	public void insert(final SupplierPaymentReceivingReceipt paymentReceivingReceipt) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, paymentReceivingReceipt.getParent().getId());
				ps.setLong(2, paymentReceivingReceipt.getReceivingReceipt().getId());
				return ps;
			}
		}, holder);
		
		paymentReceivingReceipt.setId(holder.getKey().longValue());
	}

	private static final String FIND_ALL_BY_SUPPLIER_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where SUPPLIER_PAYMENT_ID = ?"
			+ " order by b.RECEIVING_RECEIPT_NO";
	
	@Override
	public List<SupplierPaymentReceivingReceipt> findAllBySupplierPayment(SupplierPayment payment) {
		List<SupplierPaymentReceivingReceipt> receivingReceipts = 
				getJdbcTemplate().query(FIND_ALL_BY_SUPPLIER_PAYMENT_SQL, rowMapper, payment.getId());
		for (SupplierPaymentReceivingReceipt receivingReceipt : receivingReceipts) {
			receivingReceipt.setParent(payment);
		}
		return receivingReceipts;
	}

	private static final String DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL = 
			"delete from SUPP_PAYMENT_RECV_RCPT where SUPPLIER_PAYMENT_ID = ?";
	
	@Override
	public void deleteAllBySupplierPayment(SupplierPayment supplierPayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_SUPPLIER_PAYMENT_SQL, supplierPayment.getId());
	}

	private static final String DELETE_SQL = "delete from SUPP_PAYMENT_RECV_RCPT where ID = ?";
	
	@Override
	public void delete(SupplierPaymentReceivingReceipt paymentReceivingReceipt) {
		getJdbcTemplate().update(DELETE_SQL, paymentReceivingReceipt.getId());
	}

	private class SupplierPaymentReceivingReceiptRowMapper implements RowMapper<SupplierPaymentReceivingReceipt> {

		@Override
		public SupplierPaymentReceivingReceipt mapRow(ResultSet rs, int rowNum) throws SQLException {
			SupplierPaymentReceivingReceipt paymentReceivingReceipt = new SupplierPaymentReceivingReceipt();
			paymentReceivingReceipt.setId(rs.getLong("ID"));
			
			SupplierPayment supplierPayment = new SupplierPayment();
			supplierPayment.setId(rs.getLong("SUPPLIER_PAYMENT_ID"));
			paymentReceivingReceipt.setParent(supplierPayment);
			
			ReceivingReceipt receivingReceipt = new ReceivingReceipt();
			receivingReceipt.setId(rs.getLong("RECEIVING_RECEIPT_ID"));
			receivingReceipt.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
			receivingReceipt.setReceivedDate(rs.getDate("RECEIVED_DT"));
			receivingReceipt.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
			receivingReceipt.setVatRate(rs.getBigDecimal("VAT_RATE"));
			paymentReceivingReceipt.setReceivingReceipt(receivingReceipt);
			
			return paymentReceivingReceipt;
		}
		
	}

}