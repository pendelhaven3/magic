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

import com.pj.magic.dao.PaymentDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.User;

@Repository
public class PaymentDaoImpl extends MagicDao implements PaymentDao {

	private static final String BASE_SELECT_SQL =
			"   select a.ID, CUSTOMER_ID, PAYMENT_DT, AMOUNT_RECEIVED,"
			+ " PAYMENT_TERMINAL_ID, c.NAME as PAYMENT_TERMINAL_NAME,"
			+ " RECEIVED_BY, b.USERNAME as RECEIVED_BY_USERNAME"
			+ " from PAYMENT a"
			+ " join USER b"
			+ "   on b.ID = a.RECEIVED_BY"
			+ " join PAYMENT_TERMINAL c"
			+ "   on c.ID = a.PAYMENT_TERMINAL_ID";
	
	private PaymentRowMapper paymentRowMapper = new PaymentRowMapper();
	
	@Override
	public void save(Payment payment) {
		insert(payment);
	}

	private static final String INSERT_SQL =
			"insert into PAYMENT (CUSTOMER_ID, PAYMENT_DT, AMOUNT_RECEIVED, RECEIVED_BY, PAYMENT_TERMINAL_ID)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final Payment payment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, payment.getCustomer().getId());
				ps.setDate(2, new Date(payment.getPaymentDate().getTime()));
				ps.setBigDecimal(3, payment.getAmountReceived());
				ps.setLong(4, payment.getReceivedBy().getId());
				ps.setLong(5, payment.getPaymentTerminal().getId());
				return ps;
			}
		}, holder);
		
		payment.setId(holder.getKey().longValue());
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

	private class PaymentRowMapper implements RowMapper<Payment> {

		@Override
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Payment payment = new Payment();
			payment.setId(rs.getLong("ID"));
			payment.setCustomer(new Customer(rs.getLong("CUSTOMER_ID")));
			payment.setPaymentDate(rs.getDate("PAYMENT_DT"));
			payment.setAmountReceived(rs.getBigDecimal("AMOUNT_RECEIVED"));
			payment.setReceivedBy(
					new User(rs.getLong("RECEIVED_BY"), rs.getString("RECEIVED_BY_USERNAME")));
			payment.setPaymentTerminal(
					new PaymentTerminal(rs.getLong("PAYMENT_TERMINAL_ID"), rs.getString("PAYMENT_TERMINAL_NAME")));
			return payment;
		}
		
	}

	private static final String FIND_ALL_BY_PAYMENT_DATE = BASE_SELECT_SQL
			+ " where PAYMENT_DT = ?"
			+ " order by a.ID";
	
	@Override
	public List<Payment> findAllByPaymentDate(java.util.Date paymentDate) {
		return getJdbcTemplate().query(FIND_ALL_BY_PAYMENT_DATE, paymentRowMapper, paymentDate);
	}

}
