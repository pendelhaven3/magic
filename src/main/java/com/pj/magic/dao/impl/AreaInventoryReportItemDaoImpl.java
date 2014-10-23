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

import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;

@Repository
public class AreaInventoryReportItemDaoImpl extends MagicDao implements AreaInventoryReportItemDao {

	@Override
	public void save(AreaInventoryReportItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into AREA_INV_REPORT_ITEM (AREA_INV_REPORT_ID, PRODUCT_ID, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final AreaInventoryReportItem item) {
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
			"update AREA_INV_REPORT_ITEM"
			+ " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?"
			+ " where ID = ?";
	
	private void update(AreaInventoryReportItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getUnit(),
				item.getQuantity(), item.getId());
	}

	private static final String FIND_ALL_BY_AREA_INVENTORY_REPORT_SQL =
			"select ID, PRODUCT_ID, UNIT, QUANTITY from AREA_INV_REPORT_ITEM"
			+ " where AREA_INV_REPORT_ID = ?";
	
	@Override
	public List<AreaInventoryReportItem> findAllByAreaInventoryReport(final AreaInventoryReport areaInventoryReport) {
		return getJdbcTemplate().query(FIND_ALL_BY_AREA_INVENTORY_REPORT_SQL, new RowMapper<AreaInventoryReportItem>() {
			
			@Override
			public AreaInventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				AreaInventoryReportItem item = new AreaInventoryReportItem();
				item.setId(rs.getLong("ID"));
				item.setParent(areaInventoryReport);
				item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
				item.setUnit(rs.getString("UNIT"));
				item.setQuantity(rs.getInt("QUANTITY"));
				return item;
			}
		}, areaInventoryReport.getId());
	}

	private static final String DELETE_SQL = "delete from AREA_INV_REPORT_ITEM where ID = ?";
	
	@Override
	public void delete(AreaInventoryReportItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_AREA_INVENTORY_REPORT_SQL =
			"delete from AREA_INV_REPORT_ITEM where AREA_INV_REPORT_ID = ?";
	
	@Override
	public void deleteAllByAreaInventoryReport(AreaInventoryReport areaInventoryReport) {
		getJdbcTemplate().update(DELETE_ALL_BY_AREA_INVENTORY_REPORT_SQL, areaInventoryReport.getId());
	}
	
}
