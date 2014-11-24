package com.pj.magic.dao.impl;

import java.sql.Connection;
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

import com.pj.magic.dao.PaymentDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.search.PaymentSearchCriteria;

@Repository
public class PaymentDaoImpl extends MagicDao implements PaymentDao {

	private static final String PAYMENT_NUMBER_SEQUENCE = "PAYMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, PAYMENT_NO, CUSTOMER_ID, POST_IND,"
			+ " b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME"
			+ " from PAYMENT a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_ID";
	
	private PaymentRowMapper paymentRowMapper = new PaymentRowMapper();
	
	@Override
	public void save(Payment payment) {
		if (payment.getId() == null) {
			insert(payment);
		} else {
			update(payment);
		}
	}

	private void update(Payment payment) {
		// TODO Auto-generated method stub
		
	}

	private static final String INSERT_SQL = "insert into PAYMENT (PAYMENT_NO, CUSTOMER_ID) values (?, ?)";
	
	private void insert(final Payment payment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPaymentNumber());
				ps.setLong(2, payment.getCustomer().getId());
				return ps;
			}
		}, holder);
		
		Payment updated = get(holder.getKey().longValue());
		payment.setId(updated.getId());
		payment.setPaymentNumber(updated.getPaymentNumber());
	}

	private long getNextPaymentNumber() {
		return getNextSequenceValue(PAYMENT_NUMBER_SEQUENCE);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public Payment get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, paymentRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Payment> findAllByPaymentDate(java.util.Date truncate) {
		// TODO Auto-generated method stub
		return null;
	}

	private class PaymentRowMapper implements RowMapper<Payment> {

		@Override
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Payment payment = new Payment();
			payment.setId(rs.getLong("ID"));
			payment.setPaymentNumber(rs.getLong("PAYMENT_NO"));
			payment.setPosted("Y".equals(rs.getString("POST_IND")));
			
			Customer customer = new Customer();
			customer.setId(rs.getLong("CUSTOMER_ID"));
			customer.setCode(rs.getString("CUSTOMER_CODE"));
			customer.setName(rs.getString("CUSTOMER_NAME"));
			payment.setCustomer(customer);
			
			return payment;
		}
		
	}

	@Override
	public List<Payment> search(PaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		sql.append(" order by a.PAYMENT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), paymentRowMapper, params.toArray());
	}

	private static final String DELETE_SQL = "delete from PAYMENT where ID = ?";
	
	@Override
	public void delete(Payment payment) {
		getJdbcTemplate().update(DELETE_SQL, payment.getId());
	}
	
}
