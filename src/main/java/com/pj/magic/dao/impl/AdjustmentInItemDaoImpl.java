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

import com.pj.magic.dao.AdjustmentInItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;

@Repository
public class AdjustmentInItemDaoImpl extends MagicDao implements AdjustmentInItemDao {

	@Override
	public void save(AdjustmentInItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into ADJUSTMENT_IN_ITEM (ADJUSTMENT_IN_ID, PRODUCT_ID, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final AdjustmentInItem item) {
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
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		item.setId(holder.getKey().longValue());
	}
	
	private static final String UPDATE_SQL =
			"update ADJUSTMENT_IN_ITEM"
			+ " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?"
			+ " where ID = ?";
	
	private void update(AdjustmentInItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getUnit(),
				item.getQuantity(), item.getId());
	}

	private static final String FIND_ALL_BY_ADJUSTMENT_IN_SQL =
			"select ID, PRODUCT_ID, UNIT, QUANTITY from ADJUSTMENT_IN_ITEM"
			+ " where ADJUSTMENT_IN_ID = ?";
	
	@Override
	public List<AdjustmentInItem> findAllByAdjustmentIn(final AdjustmentIn adjustmentOut) {
		return getJdbcTemplate().query(FIND_ALL_BY_ADJUSTMENT_IN_SQL, new RowMapper<AdjustmentInItem>() {
			
			@Override
			public AdjustmentInItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdjustmentInItem item = new AdjustmentInItem();
				item.setId(rs.getLong("ID"));
				item.setParent(adjustmentOut);
				item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
				item.setUnit(rs.getString("UNIT"));
				item.setQuantity(rs.getInt("QUANTITY"));
				return item;
			}
		}, adjustmentOut.getId());
	}

	private static final String DELETE_SQL = "delete ADJUSTMENT_IN_ITEM where ID = ?";
	
	@Override
	public void delete(AdjustmentInItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_ADJUSTMENT_IN_SQL =
			"delete ADJUSTMENT_IN_ITEM where ADJUSTMENT_IN_ID = ?";
	
	@Override
	public void deleteAllByAdjustmentIn(AdjustmentIn adjustmentOut) {
		getJdbcTemplate().update(DELETE_ALL_BY_ADJUSTMENT_IN_SQL, adjustmentOut.getId());
	}
	
}