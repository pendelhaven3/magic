package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ReceivingReceiptDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.util.ReceivingReceiptSearchCriteria;

@Repository
public class ReceivingReceiptDaoImpl extends MagicDao implements ReceivingReceiptDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, RECEIVING_RECEIPT_NO, SUPPLIER_ID, POST_IND, "
			+ " a.PAYMENT_TERM_ID, c.NAME as PAYMENT_TERM_NAME, a.REMARKS, REFERENCE_NO, RECEIVED_DT, "
			+ " RELATED_PURCHASE_ORDER_NO, RECEIVED_BY, b.NAME as SUPPLIER_NAME"
			+ " from RECEIVING_RECEIPT a, SUPPLIER b, PAYMENT_TERM c"
			+ " where a.SUPPLIER_ID = b.ID"
			+ " and a.PAYMENT_TERM_ID = c.ID";

	private static final String RECEIVING_RECEIPT_NUMBER_SEQUENCE = "RECEIVING_RECEIPT_NO_SEQ";
	
	private ReceivingReceiptRowMapper receivingReceiptRowMapper = new ReceivingReceiptRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public ReceivingReceipt get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, receivingReceiptRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(ReceivingReceipt receivingReceipt) {
		if (receivingReceipt.getId() == null) {
			insert(receivingReceipt);
		} else {
			update(receivingReceipt);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into RECEIVING_RECEIPT"
			+ " (RECEIVING_RECEIPT_NO, SUPPLIER_ID, PAYMENT_TERM_ID, REFERENCE_NO, REMARKS, RECEIVED_DT, "
			+ "  RELATED_PURCHASE_ORDER_NO, RECEIVED_BY)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final ReceivingReceipt receivingReceipt) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextReceivingReceiptNumber());
				ps.setLong(2, receivingReceipt.getSupplier().getId());
				ps.setLong(3, receivingReceipt.getPaymentTerm().getId());
				ps.setString(4, receivingReceipt.getReferenceNumber());
				ps.setString(5, receivingReceipt.getRemarks());
				ps.setDate(6, new Date(receivingReceipt.getReceivedDate().getTime()));
				ps.setLong(7, receivingReceipt.getRelatedPurchaseOrderNumber());
				ps.setLong(8, receivingReceipt.getReceivedBy().getId());
				return ps;
			}
		}, holder);
		
		ReceivingReceipt updated = get(holder.getKey().longValue());
		receivingReceipt.setId(updated.getId());
		receivingReceipt.setReceivingReceiptNumber(updated.getReceivingReceiptNumber());
	}
	
	private long getNextReceivingReceiptNumber() {
		return getNextSequenceValue(RECEIVING_RECEIPT_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL =
			"update RECEIVING_RECEIPT set SUPPLIER_ID = ?, POST_IND = ?, "
			+ " PAYMENT_TERM_ID = ?, REMARKS = ?, REFERENCE_NO = ?, RECEIVED_DT = ?"
			+ " where ID = ?";
	
	private void update(ReceivingReceipt receivingReceipt) {
		getJdbcTemplate().update(UPDATE_SQL, 
				receivingReceipt.getSupplier().getId(),
				receivingReceipt.isPosted() ? "Y" : "N",
				receivingReceipt.getPaymentTerm().getId(),
				receivingReceipt.getRemarks(),
				receivingReceipt.getReferenceNumber(),
				receivingReceipt.getReceivedDate(),
				receivingReceipt.getId());
	}
	
	private class ReceivingReceiptRowMapper implements RowMapper<ReceivingReceipt> {

		@Override
		public ReceivingReceipt mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReceivingReceipt receivingReceipt = new ReceivingReceipt();
			receivingReceipt.setId(rs.getLong("ID"));
			receivingReceipt.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
			receivingReceipt.setSupplier(new Supplier(rs.getLong("SUPPLIER_ID"), rs.getString("SUPPLIER_NAME")));
			receivingReceipt.setPosted("Y".equals(rs.getString("POST_IND")));
			receivingReceipt.setPaymentTerm(
					new PaymentTerm(rs.getLong("PAYMENT_TERM_ID"), rs.getString("PAYMENT_TERM_NAME")));
			receivingReceipt.setRemarks(rs.getString("REMARKS"));
			receivingReceipt.setReferenceNumber(rs.getString("REFERENCE_NO"));
			receivingReceipt.setReceivedDate(rs.getDate("RECEIVED_DT"));
			receivingReceipt.setRelatedPurchaseOrderNumber(rs.getLong("RELATED_PURCHASE_ORDER_NO"));
			receivingReceipt.setReceivedBy(new User(rs.getLong("RECEIVED_BY")));
			return receivingReceipt;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.ID desc";
	
	@Override
	public List<ReceivingReceipt> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, receivingReceiptRowMapper);
	}

	private static final String GET_PRODUCT_CANVASS_ITEMS_SQL =
			"select b.RECEIVED_DT, b.RECEIVING_RECEIPT_NO, c.NAME as SUPPLIER_NAME, a.QUANTITY, a.COST,"
			+ " b.REFERENCE_NO, a.CURRENT_COST, a.DISCOUNT_1, a.DISCOUNT_2, a.DISCOUNT_3, a.FLAT_RATE_DISCOUNT,"
			+ " a.UNIT"
			+ " from RECEIVING_RECEIPT_ITEM a"
			+ " join RECEIVING_RECEIPT b"
			+ "   on b.ID = a.RECEIVING_RECEIPT_ID"
			+ " join SUPPLIER c"
			+ "   on c.ID = b.SUPPLIER_ID"
			+ " where a.PRODUCT_ID = ?"
			+ " and b.POST_IND = 'Y'"
			+ " order by RECEIVED_DT desc, RECEIVING_RECEIPT_NO desc";
	
	@Override
	public List<ProductCanvassItem> getProductCanvassItems(Product product) {
		return getJdbcTemplate().query(GET_PRODUCT_CANVASS_ITEMS_SQL, new RowMapper<ProductCanvassItem>() {

			@Override
			public ProductCanvassItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReceivingReceiptItem item = new ReceivingReceiptItem();
				item.setQuantity(rs.getInt("QUANTITY"));
				item.setCost(rs.getBigDecimal("COST"));
				item.setDiscount1(rs.getBigDecimal("DISCOUNT_1"));
				item.setDiscount2(rs.getBigDecimal("DISCOUNT_2"));
				item.setDiscount3(rs.getBigDecimal("DISCOUNT_3"));
				item.setFlatRateDiscount(rs.getBigDecimal("FLAT_RATE_DISCOUNT"));
				
				ProductCanvassItem canvassItem = new ProductCanvassItem();
				canvassItem.setReceivedDate(rs.getDate("RECEIVED_DT"));
				canvassItem.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
				canvassItem.setSupplier(new Supplier(rs.getString("SUPPLIER_NAME")));
				canvassItem.setUnit(rs.getString("UNIT"));
				canvassItem.setFinalCost(item.getFinalCost());
				canvassItem.setCurrentCost(rs.getBigDecimal("CURRENT_COST"));
				canvassItem.setReferenceNumber(rs.getString("REFERENCE_NO"));
				return canvassItem;
			}
			
		}, product.getId());
	}

	@Override
	public List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getReceivingReceiptNumber() != null) {
			sql.append(" and a.RECEIVING_RECEIPT_NO = ?");
			params.add(criteria.getReceivingReceiptNumber());
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and b.ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		sql.append(" order by a.ID desc");
		
		return getJdbcTemplate().query(sql.toString(), receivingReceiptRowMapper, params.toArray());
	}

}
