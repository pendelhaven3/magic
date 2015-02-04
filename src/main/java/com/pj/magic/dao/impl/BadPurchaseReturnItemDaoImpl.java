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

import com.pj.magic.dao.BadPurchaseReturnItemDao;
import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.BadPurchaseReturnItem;
import com.pj.magic.model.Product;

@Repository
public class BadPurchaseReturnItemDaoImpl extends MagicDao implements BadPurchaseReturnItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_PURCHASE_RETURN_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_COST,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from BAD_PURCHASE_RETURN_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private BadPurchaseReturnItemRowMapper badStockReturnItemRowMapper = new BadPurchaseReturnItemRowMapper();
	
	@Override
	public void save(BadPurchaseReturnItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into BAD_PURCHASE_RETURN_ITEM"
			+ " (BAD_PURCHASE_RETURN_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_COST)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final BadPurchaseReturnItem item) {
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
				ps.setBigDecimal(5, item.getUnitCost());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update BAD_PURCHASE_RETURN_ITEM set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, UNIT_COST = ?"
			+ " where ID = ?";
	
	private void update(BadPurchaseReturnItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getUnitCost(),
				item.getId());
	}

	private static final String FIND_ALL_BY_BAD_PURCHASE_RETURN_SQL = BASE_SELECT_SQL
			+ " where a.BAD_PURCHASE_RETURN_ID = ?";
	
	@Override
	public List<BadPurchaseReturnItem> findAllByBadPurchaseReturn(BadPurchaseReturn badStockReturn) {
		return getJdbcTemplate().query(FIND_ALL_BY_BAD_PURCHASE_RETURN_SQL, badStockReturnItemRowMapper, 
				badStockReturn.getId());
	}

	private static final String DELETE_SQL = "delete from BAD_PURCHASE_RETURN_ITEM where ID = ?";
	
	@Override
	public void delete(BadPurchaseReturnItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private class BadPurchaseReturnItemRowMapper implements RowMapper<BadPurchaseReturnItem> {

		@Override
		public BadPurchaseReturnItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			BadPurchaseReturnItem item = new BadPurchaseReturnItem();
			item.setId(rs.getLong("ID"));
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			item.setProduct(product);
			
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setUnitCost(rs.getBigDecimal("UNIT_COST"));
			
			return item;
		}
		
	}
	
}