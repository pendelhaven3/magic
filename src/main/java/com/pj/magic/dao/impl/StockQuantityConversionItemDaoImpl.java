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

import com.pj.magic.dao.StockQuantityConversionItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;

@Repository
public class StockQuantityConversionItemDaoImpl extends MagicDao implements StockQuantityConversionItemDao {

	@Override
	public void save(StockQuantityConversionItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into STOCK_QTY_CONVERSION_ITEM"
			+ " (STOCK_QTY_CONVERSION_ID, PRODUCT_ID, FROM_UNIT, QUANTITY, TO_UNIT)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final StockQuantityConversionItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setLong(2, item.getProduct().getId());
				ps.setString(3, item.getFromUnit());
				ps.setInt(4, item.getQuantity());
				ps.setString(5, item.getToUnit());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		item.setId(holder.getKey().longValue());
	}
	
	private static final String UPDATE_SQL =
			"update STOCK_QTY_CONVERSION_ITEM"
			+ " set PRODUCT_ID = ?, FROM_UNIT = ?, QUANTITY = ?, TO_UNIT = ?"
			+ " where ID = ?";
	
	private void update(StockQuantityConversionItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getFromUnit(),
				item.getQuantity(), item.getToUnit(), item.getId());
	}

	private static final String FIND_ALL_BY_STOCK_QUANTITY_CONVERSION_SQL =
			"select ID, PRODUCT_ID, FROM_UNIT, QUANTITY, TO_UNIT from STOCK_QTY_CONVERSION_ITEM"
			+ " where STOCK_QTY_CONVERSION_ID = ?";
	
	@Override
	public List<StockQuantityConversionItem> findAllByStockQuantityConversion(
			final StockQuantityConversion stockQuantityConversion) {
		return getJdbcTemplate().query(FIND_ALL_BY_STOCK_QUANTITY_CONVERSION_SQL, new RowMapper<StockQuantityConversionItem>() {
			
			@Override
			public StockQuantityConversionItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				StockQuantityConversionItem item = new StockQuantityConversionItem();
				item.setId(rs.getLong("ID"));
				item.setParent(stockQuantityConversion);
				item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
				item.setFromUnit(rs.getString("FROM_UNIT"));
				item.setQuantity(rs.getInt("QUANTITY"));
				item.setToUnit(rs.getString("TO_UNIT"));
				return item;
			}
		}, stockQuantityConversion.getId());
	}

	private static final String DELETE_SQL = "delete STOCK_QTY_CONVERSION_ITEM where ID = ?";
	
	@Override
	public void delete(StockQuantityConversionItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_STOCK_QUANTITY_CONVERSION_SQL =
			"delete STOCK_QTY_CONVERSION_ITEM where STOCK_QTY_CONVERSION_ID = ?";
	
	@Override
	public void deleteAllByStockQuantityConversion(StockQuantityConversion stockQuantityConversion) {
		getJdbcTemplate().update(DELETE_ALL_BY_STOCK_QUANTITY_CONVERSION_SQL, stockQuantityConversion.getId());
	}
	
}
