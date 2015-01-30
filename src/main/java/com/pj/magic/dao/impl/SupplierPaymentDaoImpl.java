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

import com.pj.magic.dao.SupplierPaymentDao;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.SupplierPaymentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class SupplierPaymentDaoImpl extends MagicDao implements SupplierPaymentDao {

	private static final String SUPPLIER_PAYMENT_NUMBER_SEQUENCE = "SUPPLIER_PAYMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, SUPPLIER_PAYMENT_NO, SUPPLIER_ID, ENCODER,"
			+ " POST_IND, POST_DT, POST_BY,"
			+ " b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME, "
			+ " c.USERNAME as ENCODER_USERNAME,"
			+ " d.USERNAME as POST_BY_USERNAME"
			+ " from SUPPLIER_PAYMENT a"
			+ " join SUPPLIER b"
			+ "   on b.ID = a.SUPPLIER_ID"
			+ " join USER c"
			+ "   on c.ID = a.ENCODER"
			+ " left join USER d"
			+ "   on d.ID = a.POST_BY";
	
	private SupplierPaymentRowMapper supplierPaymentRowMapper = new SupplierPaymentRowMapper();
	
	@Override
	public void save(SupplierPayment supplierPayment) {
		if (supplierPayment.getId() == null) {
			insert(supplierPayment);
		} else {
			update(supplierPayment);
		}
	}

	private static final String INSERT_SQL =
			"insert into SUPPLIER_PAYMENT"
			+ " (SUPPLIER_PAYMENT_NO, SUPPLIER_ID, CREATE_DT, ENCODER) values (?, ?, curdate(), ?)";
	
	private void insert(final SupplierPayment supplierPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextSupplierPaymentNumber());
				ps.setLong(2, supplierPayment.getSupplier().getId());
				ps.setLong(3, supplierPayment.getEncoder().getId());
				return ps;
			}
		}, holder);
		
		SupplierPayment updated = get(holder.getKey().longValue());
		supplierPayment.setId(updated.getId());
		supplierPayment.setSupplierPaymentNumber(updated.getSupplierPaymentNumber());
	}

	private long getNextSupplierPaymentNumber() {
		return getNextSequenceValue(SUPPLIER_PAYMENT_NUMBER_SEQUENCE);
	}
	
	private static final String UPDATE_SQL =
			"update SUPPLIER_PAYMENT set SUPPLIER_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ? where ID = ?";
	
	private void update(SupplierPayment payment) {
		getJdbcTemplate().update(UPDATE_SQL,
				payment.getSupplier().getId(),
				payment.isPosted() ? "Y" : "N",
				payment.isPosted() ? payment.getPostDate() : null,
				payment.isPosted() ? payment.getPostedBy().getId() : null,
				payment.isCancelled() ? "Y" : "N",
				payment.isCancelled() ? payment.getCancelDate() : null,
				payment.isCancelled() ? payment.getCancelledBy().getId() : null,
				payment.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL
			+ " where a.ID = ?";
	
	@Override
	public SupplierPayment get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, supplierPaymentRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<SupplierPayment> search(SupplierPaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getCancelled() != null) {
			sql.append(" and a.CANCEL_IND = ?");
			params.add(criteria.getCancelled() ? "Y" : "N");
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
			sql.append(" and a.POST_DT >= ? and a.POST_DT <= DATE_ADD(?, INTERVAL 1 DAY)");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
		}
		
		sql.append(" order by a.SUPPLIER_PAYMENT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), supplierPaymentRowMapper, params.toArray());
	}

	private class SupplierPaymentRowMapper implements RowMapper<SupplierPayment> {

		@Override
		public SupplierPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			SupplierPayment supplierPayment = new SupplierPayment();
			supplierPayment.setId(rs.getLong("ID"));
			supplierPayment.setSupplierPaymentNumber(rs.getLong("SUPPLIER_PAYMENT_NO"));
			supplierPayment.setEncoder(new User(rs.getLong("ENCODER"), rs.getString("ENCODER_USERNAME")));
			
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("SUPPLIER_ID"));
			supplier.setCode(rs.getString("SUPPLIER_CODE"));
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			supplierPayment.setSupplier(supplier);
			
			supplierPayment.setPosted("Y".equals(rs.getString("POST_IND")));
			if (supplierPayment.isPosted()) {
				supplierPayment.setPostDate(rs.getDate("POST_DT"));
				supplierPayment.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			return supplierPayment;
		}
		
	}
	
}