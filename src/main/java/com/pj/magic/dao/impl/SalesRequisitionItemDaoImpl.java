package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;

@Repository
public class SalesRequisitionItemDaoImpl extends MagicDao implements SalesRequisitionItemDao {

	private static final String BASE_SELECT_SQL =
			"select ID, SALES_REQUISITION_ID, PRODUCT_ID, UNIT, QUANTITY from SALES_REQUISITION_ITEM";

	private SalesRequisitionItemRowMapper salesRequisitionItemRowMapper =
			new SalesRequisitionItemRowMapper();
	
	@Override
	public void save(SalesRequisitionItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into SALES_REQUISITION_ITEM (SALES_REQUISITION_ID, PRODUCT_ID, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final SalesRequisitionItem item) {
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
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}
	
	private static final String UPDATE_SQL =
			"update SALES_REQUISITION_ITEM"
			+ " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?"
			+ " where ID = ?";
	
	private void update(SalesRequisitionItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getUnit(),
				item.getQuantity(), item.getId());
	}

	private static final String FIND_ALL_BY_SALES_REQUISITION_SQL = BASE_SELECT_SQL
			+ " where SALES_REQUISITION_ID = ?";
	
	@Override
	public List<SalesRequisitionItem> findAllBySalesRequisition(SalesRequisition salesRequisition) {
		List<SalesRequisitionItem> items = getJdbcTemplate().query(FIND_ALL_BY_SALES_REQUISITION_SQL, 
				salesRequisitionItemRowMapper, salesRequisition.getId());
		for (SalesRequisitionItem item : items) {
			item.setParent(salesRequisition);
		}
		return items;
	}

	private static final String DELETE_SQL = "delete from SALES_REQUISITION_ITEM where ID = ?";
	
	@Override
	public void delete(SalesRequisitionItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_SALES_REQUISITION_SQL =
			"delete from SALES_REQUISITION_ITEM where SALES_REQUISITION_ID = ?";
	
	@Override
	public void deleteAllBySalesRequisition(SalesRequisition salesRequisition) {
		getJdbcTemplate().update(DELETE_ALL_BY_SALES_REQUISITION_SQL, salesRequisition.getId());
	}

	private static final String FIND_FIRST_BY_PRODUCT_SQL = BASE_SELECT_SQL
			+ " where PRODUCT_ID = ? limit 1";
	
	@Override
	public SalesRequisitionItem findFirstByProduct(Product product) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_BY_PRODUCT_SQL, salesRequisitionItemRowMapper,
					product.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class SalesRequisitionItemRowMapper implements RowMapper<SalesRequisitionItem> {

		@Override
		public SalesRequisitionItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesRequisitionItem item = new SalesRequisitionItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new SalesRequisition(rs.getLong("SALES_REQUISITION_ID")));
			item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			return item;
		}
		
	}
	
}
