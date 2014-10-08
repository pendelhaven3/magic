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

import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;

@Repository
public class InventoryCheckDaoImpl extends MagicDao implements InventoryCheckDao {

	private static final String BASE_SELECT_SQL =
			"select ID, INVENTORY_DT, POST_IND from INVENTORY_CHECK";
	
	private InventoryCheckRowMapper inventoryCheckRowMapper = new InventoryCheckRowMapper();
	
	@Override
	public void save(InventoryCheck inventoryCheck) {
		if (inventoryCheck.getId() == null) {
			insert(inventoryCheck);
		} else {
			update(inventoryCheck);
		}
	}

	private static final String INSERT_SQL = "insert into INVENTORY_CHECK (INVENTORY_DT) values (?)";
	
	private void insert(final InventoryCheck inventoryCheck) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setDate(1, new Date(inventoryCheck.getInventoryDate().getTime()));
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		inventoryCheck.setId(holder.getKey().longValue());
	}

	private void update(InventoryCheck inventoryCheck) {
		
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where ID = ?";
	
	@Override
	public InventoryCheck get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, inventoryCheckRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by INVENTORY_DT desc";
	
	@Override
	public List<InventoryCheck> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, inventoryCheckRowMapper);
	}

	private class InventoryCheckRowMapper implements RowMapper<InventoryCheck> {

		@Override
		public InventoryCheck mapRow(ResultSet rs, int rowNum) throws SQLException {
			InventoryCheck inventoryCheck = new InventoryCheck();
			inventoryCheck.setId(rs.getLong("ID"));
			inventoryCheck.setInventoryDate(rs.getDate("INVENTORY_DT"));
			inventoryCheck.setPosted("Y".equals(rs.getString("POST_IND")));
			return inventoryCheck;
		}
		
	}

	@Override
	public List<InventoryCheck> search(InventoryCheck criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
		sb.append(" where POST_IND = ?");
		params.add(criteria.isPosted() ? "Y" : "N");
		
		return getJdbcTemplate().query(sb.toString(), inventoryCheckRowMapper, params.toArray());
	}

	private static final String GET_SUMMARY_ITEMS_SQL =
			"   select b.ID, b.CODE, b.DESCRIPTION, a.UNIT,"
			+ " b.AVAIL_QTY_CSE, b.AVAIL_QTY_TIE, b.AVAIL_QTY_CTN, b.AVAIL_QTY_DOZ, b.AVAIL_QTY_PCS,"
			+ " a.COUNTED_QTY"
			+ " from ("
			+ "   select airt.PRODUCT_ID, airt.UNIT, sum(airt.QUANTITY) as COUNTED_QTY"
			+ "   from AREA_INV_REPORT air"
			+ "   join AREA_INV_REPORT_ITEM airt"
			+ "     on airt.AREA_INV_REPORT_ID = air.ID"
			+ "   where air.INVENTORY_CHECK_ID = ?"
			+ "   group by airt.PRODUCT_ID, airt.UNIT"
			+ " ) a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	@Override
	public List<InventoryCheckSummaryItem> getSummaryItems(InventoryCheck inventoryCheck) {
		return getJdbcTemplate().query(GET_SUMMARY_ITEMS_SQL, new RowMapper<InventoryCheckSummaryItem>() {

			@Override
			public InventoryCheckSummaryItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				Product product = new Product();
				product.setId(rs.getLong("ID"));
				product.setCode(rs.getString("CODE"));
				product.setDescription(rs.getString("DESCRIPTION"));
				product.addUnitQuantity(Unit.CASE, rs.getInt("AVAIL_QTY_CSE"));
				product.addUnitQuantity(Unit.TIE, rs.getInt("AVAIL_QTY_TIE"));
				product.addUnitQuantity(Unit.CARTON, rs.getInt("AVAIL_QTY_CTN"));
				product.addUnitQuantity(Unit.DOZEN, rs.getInt("AVAIL_QTY_DOZ"));
				product.addUnitQuantity(Unit.PIECES, rs.getInt("AVAIL_QTY_PCS"));

				InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
				item.setProduct(product);
				item.setUnit(rs.getString("UNIT"));
				item.setQuantity(rs.getInt("COUNTED_QTY"));
				return item;
			}
			
		}, inventoryCheck.getId());
	}
	
}
