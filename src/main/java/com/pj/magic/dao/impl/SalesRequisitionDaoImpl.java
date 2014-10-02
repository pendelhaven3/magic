package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.User;

@Repository
public class SalesRequisitionDaoImpl extends MagicDao implements SalesRequisitionDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_REQUISITION_NO, CUSTOMER_ID, CREATE_DT, ENCODER_ID, POST_IND,"
			+ " PRICING_SCHEME_ID, MODE, REMARKS,"
			+ " PAYMENT_TERM_ID, b.NAME as PAYMENT_TERM_NAME"
			+ " from SALES_REQUISITION a"
			+ " join PAYMENT_TERM b"
			+ "   on b.ID = a.PAYMENT_TERM_ID";
	
	private SalesRequisitionRowMapper salesRequisitionRowMapper = new SalesRequisitionRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
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
			"insert into SALES_REQUISITION"
			+ " (CUSTOMER_ID, CREATE_DT, ENCODER_ID, PRICING_SCHEME_ID, MODE, REMARKS, PAYMENT_TERM_ID)"
			+ " values (?, ?, ?, ?, ?, ?, ?)";
	
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
				ps.setLong(3, salesRequisition.getEncoder().getId());
				ps.setLong(4, salesRequisition.getPricingScheme().getId());
				ps.setString(5, salesRequisition.getMode());
				ps.setString(6, salesRequisition.getRemarks());
				ps.setLong(7, salesRequisition.getPaymentTerm().getId());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		SalesRequisition updated = get(holder.getKey().longValue());
		salesRequisition.setId(updated.getId());
		salesRequisition.setSalesRequisitionNumber(updated.getSalesRequisitionNumber());
	}
	
	private static final String UPDATE_SQL =
			"update SALES_REQUISITION"
			+ " set CUSTOMER_ID = ?, POST_IND = ?, PRICING_SCHEME_ID = ?, MODE = ?, REMARKS = ?,"
			+ " PAYMENT_TERM_ID = ?"
			+ " where ID = ?";
	
	private void update(SalesRequisition salesRequisition) {
		getJdbcTemplate().update(UPDATE_SQL, salesRequisition.getCustomer().getId(), 
				salesRequisition.isPosted() ? "Y" : "N",
				salesRequisition.getPricingScheme().getId(),
				salesRequisition.getMode(),
				salesRequisition.getRemarks(),
				salesRequisition.getPaymentTerm().getId(),
				salesRequisition.getId());
	}
	
	private class SalesRequisitionRowMapper implements RowMapper<SalesRequisition> {

		@Override
		public SalesRequisition mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesRequisition salesRequisition = new SalesRequisition();
			salesRequisition.setId(rs.getLong("ID"));
			salesRequisition.setSalesRequisitionNumber(rs.getLong("SALES_REQUISITION_NO"));
			salesRequisition.setCreateDate(rs.getDate("CREATE_DT"));
			salesRequisition.setEncoder(new User(rs.getLong("ENCODER_ID")));
			salesRequisition.setPosted("Y".equals(rs.getString("POST_IND")));
			salesRequisition.setPricingScheme(new PricingScheme(rs.getLong("PRICING_SCHEME_ID")));
			salesRequisition.setMode(rs.getString("MODE"));
			salesRequisition.setRemarks(rs.getString("REMARKS"));
			salesRequisition.setCustomer(new Customer(rs.getLong("CUSTOMER_ID")));
			salesRequisition.setPaymentTerm(
					new PaymentTerm(rs.getLong("PAYMENT_TERM_ID"), rs.getString("PAYMENT_TERM_NAME")));
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
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where POST_IND = ?");
		sql.append(" order by a.ID desc"); // TODO: change to be more flexible when the need arises
		
		return getJdbcTemplate().query(sql.toString(), salesRequisitionRowMapper,
				criteria.isPosted() ? "Y" : "N");
	}

}
