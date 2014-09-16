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

import com.pj.magic.dao.ReceivingReceiptItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;

@Repository
public class ReceivingReceiptItemDaoImpl extends MagicDao implements ReceivingReceiptItemDao {

	@Override
	public void save(ReceivingReceiptItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into RECEIVING_RECEIPT_ITEM"
			+ " (RECEIVING_RECEIPT_ID, PRODUCT_ID, UNIT, QUANTITY, COST)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final ReceivingReceiptItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setLong(2, item.getProduct().getId());
				ps.setString(3, item.getUnit());
				ps.setInt(4, item.getQuantity());
				ps.setBigDecimal(5, item.getCost());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		item.setId(holder.getKey().longValue());
	}
	
	private static final String UPDATE_SQL =
			"update RECEIVING_RECEIPT_ITEM"
			+ " set DISCOUNT_1 = ?, DISCOUNT_2 = ?, DISCOUNT_3 = ?, FLAT_RATE_DISCOUNT = ?"
			+ " where ID = ?";
	
	private void update(ReceivingReceiptItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getDiscount1(), item.getDiscount2(), 
				item.getDiscount3(), item.getFlatRateDiscount(), item.getId());
	}

	private static final String FIND_ALL_BY_RECEIVING_RECEIPT_SQL =
			"select ID, PRODUCT_ID, UNIT, QUANTITY, COST,"
			+ " DISCOUNT_1, DISCOUNT_2, DISCOUNT_3, FLAT_RATE_DISCOUNT"
			+ " from RECEIVING_RECEIPT_ITEM"
			+ " where RECEIVING_RECEIPT_ID = ?";
	
	@Override
	public List<ReceivingReceiptItem> findAllByReceivingReceipt(
			final ReceivingReceipt receivingReceipt) {
		return getJdbcTemplate().query(FIND_ALL_BY_RECEIVING_RECEIPT_SQL, new RowMapper<ReceivingReceiptItem>() {
			
			@Override
			public ReceivingReceiptItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReceivingReceiptItem item = new ReceivingReceiptItem();
				item.setId(rs.getLong("ID"));
				item.setParent(receivingReceipt);
				item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
				item.setUnit(rs.getString("UNIT"));
				item.setQuantity(rs.getInt("QUANTITY"));
				item.setCost(rs.getBigDecimal("COST").setScale(2));
				item.setDiscount1(rs.getBigDecimal("DISCOUNT_1").setScale(2));
				item.setDiscount2(rs.getBigDecimal("DISCOUNT_2").setScale(2));
				item.setDiscount3(rs.getBigDecimal("DISCOUNT_3").setScale(2));
				item.setFlatRateDiscount(rs.getBigDecimal("FLAT_RATE_DISCOUNT").setScale(2));
				return item;
			}
		}, receivingReceipt.getId());
	}

}
