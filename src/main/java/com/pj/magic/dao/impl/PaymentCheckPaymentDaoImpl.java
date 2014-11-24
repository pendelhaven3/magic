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

import com.pj.magic.dao.PaymentCheckPaymentDao;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCheckPayment;

@Repository
public class PaymentCheckPaymentDaoImpl extends MagicDao implements PaymentCheckPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PAYMENT_ID, BANK, CHECK_NO, AMOUNT"
			+ " from PAYMENT_CHECK_PAYMENT a";
	
	private PaymentCheckPaymentRowMapper checkRowMapper = new PaymentCheckPaymentRowMapper();
	
	@Override
	public void save(PaymentCheckPayment check) {
		if (check.getId() == null) {
			insert(check);
		} else {
			update(check);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PAYMENT_CHECK_PAYMENT (PAYMENT_ID, BANK, CHECK_NO, AMOUNT) values (?, ?, ?, ?)";
	
	private void insert(final PaymentCheckPayment check) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, check.getParent().getId());
				ps.setString(2, check.getBank());
				ps.setString(3, check.getCheckNumber());
				ps.setBigDecimal(4, check.getAmount());
				return ps;
			}
		}, holder);
		
		check.setId(holder.getKey().longValue());
	}

	private void update(PaymentCheckPayment check) {
		// TODO Auto-generated method stub
		
	}

	private static final String FIND_ALL_BY_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PAYMENT_ID = ?";
	
	@Override
	public List<PaymentCheckPayment> findAllByPayment(Payment payment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PAYMENT_SQL, checkRowMapper, payment.getId());
	}

	private class PaymentCheckPaymentRowMapper implements RowMapper<PaymentCheckPayment> {

		@Override
		public PaymentCheckPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentCheckPayment check = new PaymentCheckPayment();
			check.setId(rs.getLong("ID"));
			check.setParent(new Payment(rs.getLong("PAYMENT_ID")));
			check.setBank(rs.getString("BANK"));
			check.setCheckNumber(rs.getString("CHECK_NO"));
			check.setAmount(rs.getBigDecimal("AMOUNT"));
			return check;
		}
		
	}
	
}