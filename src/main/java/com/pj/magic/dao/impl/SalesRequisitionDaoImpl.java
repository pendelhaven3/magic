package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesRequisition;

@Repository
public class SalesRequisitionDaoImpl extends MagicDao implements SalesRequisitionDao {

	private static final Logger logger = LoggerFactory.getLogger(SalesRequisitionDaoImpl.class);
	
	private static final String SIMPLE_SELECT_SQL =
			"select ID, SALES_REQUISITION_NO, CUSTOMER_ID, CREATE_DT, ENCODER, POST_IND"
			+ " from SALES_REQUISITION";
	
	@Autowired private DataSource dataSource;
	
	private SalesRequisitionRowMapper salesRequisitionRowMapper = new SalesRequisitionRowMapper();
	
	private static final String GET_SQL = SIMPLE_SELECT_SQL + " where ID = ?";
	
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
			"insert into SALES_REQUISITION (CUSTOMER_ID, CREATE_DT, ENCODER)"
			+ " values (?, ?, ?)";
	
	private void insert(final SalesRequisition salesRequisition) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				if (salesRequisition.getCustomer() != null) {
					ps.setLong(1, salesRequisition.getCustomer().getId());
				} else {
					ps.setNull(1, Types.INTEGER);
				}
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
			+ " set CUSTOMER_ID = ?, POST_IND = ?"
			+ " where ID = ?";
	
	private void update(SalesRequisition salesRequisition) {
		getJdbcTemplate().update(UPDATE_SQL, salesRequisition.getCustomer().getId(), 
				salesRequisition.isPosted() ? "Y" : "N", salesRequisition.getId());
	}
	
	private class SalesRequisitionRowMapper implements RowMapper<SalesRequisition> {

		@Override
		public SalesRequisition mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesRequisition salesRequisition = new SalesRequisition();
			salesRequisition.setId(rs.getLong("ID"));
			salesRequisition.setSalesRequisitionNumber(rs.getLong("SALES_REQUISITION_NO"));
			salesRequisition.setCreateDate(rs.getDate("CREATE_DT"));
			salesRequisition.setEncoder(rs.getString("ENCODER"));
			salesRequisition.setPosted("Y".equals(rs.getString("POST_IND")));

			if (rs.getLong("CUSTOMER_ID") != 0) {
				Customer customer = new Customer();
				customer.setId(rs.getLong("CUSTOMER_ID"));
				salesRequisition.setCustomer(customer);
			}
			
			return salesRequisition;
		}
	}

	private static final String DELETE_SQL = "delete SALES_REQUISITION where ID = ?";
	
	@Override
	public void delete(SalesRequisition salesRequisition) {
		getJdbcTemplate().update(DELETE_SQL, salesRequisition.getId());
	}

	@Override
	public List<SalesRequisition> search(SalesRequisition criteria) {
		StringBuilder sql = new StringBuilder(SIMPLE_SELECT_SQL);
		sql.append(" where POST_IND = ?");
		sql.append(" order by ID desc"); // TODO: change to be more flexible when the need arises
		
		return getJdbcTemplate().query(sql.toString(), salesRequisitionRowMapper,
				criteria.isPosted() ? "Y" : "N");
	}

}
