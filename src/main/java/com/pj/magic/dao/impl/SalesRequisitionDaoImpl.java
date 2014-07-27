package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
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

import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.model.SalesRequisition;

@Repository
public class SalesRequisitionDaoImpl extends MagicDao implements SalesRequisitionDao {

	private SalesRequisitionRowMapper salesRequisitionRowMapper = new SalesRequisitionRowMapper();
	
	private static final String GET_ALL_SQL =
			"select ID, SALES_REQUISITION_NO, CUSTOMER_NAME, CREATE_DT, ENCODER"
			+ " from SALES_REQUISITION";
	
	@Override
	public List<SalesRequisition> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, salesRequisitionRowMapper);
	}

	private static final String GET_SQL =
			"select ID, SALES_REQUISITION_NO, CUSTOMER_NAME, CREATE_DT, ENCODER"
			+ " from SALES_REQUISITION"
			+ " where ID = ?";
	
	@Override
	public SalesRequisition get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, salesRequisitionRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(SalesRequisition salesRequisition) {
		if (salesRequisition.getId() == null) {
			insert(salesRequisition);
		} else {
			update(salesRequisition);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into SALES_REQUISITION (CUSTOMER_NAME, CREATE_DT, ENCODER)"
			+ " values (?, ?, ?)";
	
	private void insert(final SalesRequisition salesRequisition) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, salesRequisition.getCustomerName());
				ps.setDate(2, new Date(salesRequisition.getCreateDate().getTime()));
				ps.setString(3, salesRequisition.getEncoder());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		SalesRequisition updated = get(holder.getKey().longValue());
		salesRequisition.setId(updated.getId());
		salesRequisition.setSalesRequisitionNumber(updated.getSalesRequisitionNumber());
	}
	
	private static final String UPDATE_SQL =
			"update SALES_REQUISITION"
			+ " set CUSTOMER_NAME = ?"
			+ " where ID = ?";
	
	private void update(SalesRequisition salesRequisition) {
		getJdbcTemplate().update(UPDATE_SQL, salesRequisition.getCustomerName(), salesRequisition.getId());
	}
	
	private class SalesRequisitionRowMapper implements RowMapper<SalesRequisition> {

		@Override
		public SalesRequisition mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesRequisition salesRequisition = new SalesRequisition();
			salesRequisition.setId(rs.getLong("ID"));
			salesRequisition.setSalesRequisitionNumber(rs.getLong("SALES_REQUISITION_NO"));
			salesRequisition.setCustomerName(rs.getString("CUSTOMER_NAME"));
			salesRequisition.setCreateDate(rs.getDate("CREATE_DT"));
			salesRequisition.setEncoder(rs.getString("ENCODER"));
			return salesRequisition;
		}
	}

	private static final String DELETE_SQL = "delete SALES_REQUISITION where ID = ?";
	
	@Override
	public void delete(SalesRequisition salesRequisition) {
		getJdbcTemplate().update(DELETE_SQL, salesRequisition.getId());
	}

}
