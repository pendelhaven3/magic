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
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PaymentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PaymentDaoImpl extends MagicDao implements PaymentDao {

	private static final String PAYMENT_NUMBER_SEQUENCE = "PAYMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, PAYMENT_NO, CUSTOMER_ID, POST_IND, POST_DT, POST_BY, CREATE_DT,"
			+ " b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME,"
			+ " a.PAYMENT_TERMINAL_ID, d.NAME as PAYMENT_TERMINAL_NAME"
			+ " from PAYMENT a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_ID"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY"
			+ " left join PAYMENT_TERMINAL d"
			+ "   on d.ID = a.PAYMENT_TERMINAL_ID";
	
	private PaymentRowMapper paymentRowMapper = new PaymentRowMapper();
	
	@Override
	public void save(Payment payment) {
		if (payment.getId() == null) {
			insert(payment);
		} else {
			update(payment);
		}
	}

	private static final String UPDATE_SQL =
			"update PAYMENT set CUSTOMER_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " PAYMENT_TERMINAL_ID = ? where ID = ?";
	
	private void update(Payment payment) {
		getJdbcTemplate().update(UPDATE_SQL,
				payment.getCustomer().getId(),
				payment.isPosted() ? "Y" : "N",
				payment.getPostDate(),
				payment.isPosted() ? payment.getPostedBy().getId() : null,
				payment.getPaymentTerminal() != null ? payment.getPaymentTerminal().getId() : null,
				payment.getId());
	}

	private static final String INSERT_SQL = 
			"insert into PAYMENT (PAYMENT_NO, CUSTOMER_ID, CREATE_DT) values (?, ?, curdate())";
	
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
			
			if (rs.getLong("POST_BY") != 0) {
				payment.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			payment.setPostDate(rs.getDate("POST_DT"));
			payment.setCreateDate(rs.getDate("CREATE_DT"));
			
			if (rs.getLong("PAYMENT_TERMINAL_ID") != 0) {
				PaymentTerminal terminal = new PaymentTerminal();
				terminal.setId(rs.getLong("PAYMENT_TERMINAL_ID"));
				terminal.setName(rs.getString("PAYMENT_TERMINAL_NAME"));
				payment.setPaymentTerminal(terminal);
			}
			
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
		
		if (criteria.getCustomer() != null) {
			sql.append(" and a.CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getPaymentNumber() != null) {
			sql.append(" and a.PAYMENT_NO = ?");
			params.add(criteria.getPaymentNumber());
		}
		
		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
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
