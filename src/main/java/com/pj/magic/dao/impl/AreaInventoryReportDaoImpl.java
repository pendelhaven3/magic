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

import com.pj.magic.dao.AreaInventoryReportDao;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.InventoryCheck;

@Repository
public class AreaInventoryReportDaoImpl extends MagicDao implements AreaInventoryReportDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, INVENTORY_CHECK_ID, REPORT_NO, AREA, CHECKER, DOUBLE_CHECKER,"
			+ " b.INVENTORY_DT"
			+ " from AREA_INV_REPORT a"
			+ " join INVENTORY_CHECK b"
			+ "   on b.ID = a.INVENTORY_CHECK_ID";
	
	private AreaInventoryReportRowMapper areaInventoryReportRowMapper = new AreaInventoryReportRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public AreaInventoryReport get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, areaInventoryReportRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(AreaInventoryReport areaInventoryReport) {
		if (areaInventoryReport.getId() == null) {
			insert(areaInventoryReport);
		} else {
			update(areaInventoryReport);
		}
	}

	private static final String INSERT_SQL =
			"insert into AREA_INV_REPORT"
			+ " (INVENTORY_CHECK_ID, REPORT_NO, AREA, CHECKER, DOUBLE_CHECKER)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final AreaInventoryReport areaInventoryReport) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, areaInventoryReport.getParent().getId());
				ps.setInt(2, areaInventoryReport.getReportNumber());
				ps.setString(3, areaInventoryReport.getArea());
				ps.setString(4, areaInventoryReport.getChecker());
				ps.setString(5, areaInventoryReport.getDoubleChecker());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		areaInventoryReport.setId(holder.getKey().longValue());
	}

	private void update(AreaInventoryReport areaInventoryReport) {
		// TODO Auto-generated method stub
		
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by ID desc";
	
	@Override
	public List<AreaInventoryReport> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, areaInventoryReportRowMapper);
	}

	private class AreaInventoryReportRowMapper implements RowMapper<AreaInventoryReport> {

		@Override
		public AreaInventoryReport mapRow(ResultSet rs, int rowNum) throws SQLException {
			AreaInventoryReport areaInventoryReport = new AreaInventoryReport();
			areaInventoryReport.setId(rs.getLong("ID"));
			areaInventoryReport.setParent(new InventoryCheck(rs.getLong("INVENTORY_CHECK_ID")));
			areaInventoryReport.getParent().setInventoryDate(rs.getDate("INVENTORY_DT"));
			areaInventoryReport.setReportNumber(rs.getInt("REPORT_NO"));
			areaInventoryReport.setArea(rs.getString("AREA"));
			areaInventoryReport.setChecker(rs.getString("CHECKER"));
			areaInventoryReport.setDoubleChecker(rs.getString("DOUBLE_CHECKER"));
			return areaInventoryReport;
		}
		
	}
	
}
