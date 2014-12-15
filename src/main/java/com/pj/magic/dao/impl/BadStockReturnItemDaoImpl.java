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

import com.pj.magic.dao.BadStockReturnItemDao;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.Product;

@Repository
public class BadStockReturnItemDaoImpl extends MagicDao implements BadStockReturnItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_STOCK_RETURN_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from BAD_STOCK_RETURN_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private BadStockReturnItemRowMapper badStockReturnItemRowMapper = new BadStockReturnItemRowMapper();
	
	@Override
	public void save(BadStockReturnItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into BAD_STOCK_RETURN_ITEM (BAD_STOCK_RETURN_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final BadStockReturnItem item) {
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
				ps.setBigDecimal(5, item.getUnitPrice());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update BAD_STOCK_RETURN_ITEM set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, UNIT_PRICE = ?"
			+ " where ID = ?";
	
	private void update(BadStockReturnItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getUnitPrice(),
				item.getId());
	}

	private static final String FIND_ALL_BY_BAD_STOCK_RETURN_SQL = BASE_SELECT_SQL
			+ " where a.BAD_STOCK_RETURN_ID = ?";
	
	@Override
	public List<BadStockReturnItem> findAllByBadStockReturn(BadStockReturn badStockReturn) {
		return getJdbcTemplate().query(FIND_ALL_BY_BAD_STOCK_RETURN_SQL, badStockReturnItemRowMapper, 
				badStockReturn.getId());
	}

	private static final String DELETE_SQL = "delete from BAD_STOCK_RETURN_ITEM where ID = ?";
	
	@Override
	public void delete(BadStockReturnItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private class BadStockReturnItemRowMapper implements RowMapper<BadStockReturnItem> {

		@Override
		public BadStockReturnItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			BadStockReturnItem item = new BadStockReturnItem();
			item.setId(rs.getLong("ID"));
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			item.setProduct(product);
			
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			
			return item;
		}
		
	}
	
}