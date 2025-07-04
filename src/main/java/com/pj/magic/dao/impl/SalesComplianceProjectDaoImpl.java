package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesComplianceProjectDao;
import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.util.DbUtil;

@Repository
public class SalesComplianceProjectDaoImpl extends MagicDao implements SalesComplianceProjectDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_COMPLIANCE_PROJECT_NO, NAME, START_DT, END_DT, TARGET_AMOUNT"
			+ " from SALES_COMPLIANCE_PROJECT a";
	
	private static final String SALES_COMPLIANCE_PROJECT_NUMBER_SEQUENCE = "SALES_COMPLIANCE_PROJECT_NO_SEQ";
	
	private RowMapper<SalesComplianceProject> rowMapper = (rs, rownum) -> {
		SalesComplianceProject project = new SalesComplianceProject();
		project.setId(rs.getLong("ID"));
		project.setSalesComplianceProjectNumber(rs.getLong("SALES_COMPLIANCE_PROJECT_NO"));
		project.setName(rs.getString("NAME"));
		project.setStartDate(rs.getDate("START_DT"));
		project.setEndDate(rs.getDate("END_DT"));
		project.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
		return project;
	};

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by ID desc";
	
	@Override
	public List<SalesComplianceProject> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public SalesComplianceProject get(Long id) {
		return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
	}

	@Override
	public void save(SalesComplianceProject project) {
		if (project.getId() == null) {
			insert(project);
		} else {
			update(project);
		}
	}

	private static final String INSERT_SQL =
			"insert into SALES_COMPLIANCE_PROJECT"
			+ " (SALES_COMPLIANCE_PROJECT_NO, NAME, START_DT, END_DT, TARGET_AMOUNT)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final SalesComplianceProject project) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextSalesComplianceProjectNumber());
				ps.setString(2, project.getName());
				ps.setDate(3, DbUtil.toSqlDate(project.getStartDate()));
				ps.setDate(4, DbUtil.toSqlDate(project.getEndDate()));
				ps.setBigDecimal(5, project.getTargetAmount());
				return ps;
			}
		}, holder);
		
		SalesComplianceProject updated = get(holder.getKey().longValue());
		project.setId(updated.getId());
		project.setSalesComplianceProjectNumber(updated.getSalesComplianceProjectNumber());
	}
	
	private long getNextSalesComplianceProjectNumber() {
		return getNextSequenceValue(SALES_COMPLIANCE_PROJECT_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL =
			"update SALES_COMPLIANCE_PROJECT"
			+ " set NAME = ?, START_DT = ?, END_DT = ?, TARGET_AMOUNT = ?"
			+ " where ID = ?";
	
	private void update(SalesComplianceProject project) {
		getJdbcTemplate().update(UPDATE_SQL,
				project.getName(),
				project.getStartDate(),
				project.getEndDate(),
				project.getTargetAmount(),
				project.getId());
	}
	
}