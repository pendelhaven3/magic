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

import com.pj.magic.dao.SupplierPaymentAdjustmentDao;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.SupplierPaymentAdjustment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.SupplierPaymentAdjustmentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class SupplierPaymentAdjustmentDaoImpl extends MagicDao implements SupplierPaymentAdjustmentDao {

	private static final String SUPPLIER_PAYMENT_ADJUSTMENT_NUMBER_SEQUENCE = "SUPP_PAYMENT_ADJUSTMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, SUPP_PAYMENT_ADJUSTMENT_NO, SUPPLIER_ID, PURCHASE_PAYMENT_ADJ_TYPE_ID, AMOUNT,"
			+ " POST_IND, POST_DT, POST_BY, a.REMARKS,"
			+ " b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME,"
			+ " d.CODE as ADJUSTMENT_TYPE_CODE"
			+ " from SUPP_PAYMENT_ADJUSTMENT a"
			+ " join SUPPLIER b"
			+ "   on b.ID = a.SUPPLIER_ID"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY"
			+ " join PURCHASE_PAYMENT_ADJ_TYPE d"
			+ "   on d.ID = a.PURCHASE_PAYMENT_ADJ_TYPE_ID";

	private SupplierPaymentAdjustmentRowMapper paymentAdjustmentRowMapper = 
			new SupplierPaymentAdjustmentRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public SupplierPaymentAdjustment get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, paymentAdjustmentRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(SupplierPaymentAdjustment adjustment) {
		if (adjustment.getId() == null) {
			insert(adjustment);
		} else {
			update(adjustment);
		}
	}

	private static final String UPDATE_SQL = 
			"update SUPP_PAYMENT_ADJUSTMENT set SUPPLIER_ID = ?, PURCHASE_PAYMENT_ADJ_TYPE_ID = ?, AMOUNT = ?,"
			+ " POST_IND = ?, POST_DT = ?, POST_BY = ?, REMARKS = ? where ID = ?";
	
	private void update(SupplierPaymentAdjustment paymentAdjustment) {
		getJdbcTemplate().update(UPDATE_SQL,
				paymentAdjustment.getSupplier().getId(),
				paymentAdjustment.getAdjustmentType().getId(),
				paymentAdjustment.getAmount(),
				paymentAdjustment.isPosted() ? "Y" : "N",
				paymentAdjustment.getPostDate(),
				paymentAdjustment.isPosted() ? paymentAdjustment.getPostedBy().getId() : null,
				paymentAdjustment.getRemarks(),
				paymentAdjustment.getId());
	}

	private static final String INSERT_SQL =
			"insert into SUPP_PAYMENT_ADJUSTMENT"
			+ " (SUPP_PAYMENT_ADJUSTMENT_NO, SUPPLIER_ID, PURCHASE_PAYMENT_ADJ_TYPE_ID, AMOUNT, REMARKS)"
			+ " values"
			+ " (?, ?, ?, ?, ?)";
	
	private void insert(final SupplierPaymentAdjustment paymentAdjustment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPaymentAdjustmentNumber());
				ps.setLong(2, paymentAdjustment.getSupplier().getId());
				ps.setLong(3, paymentAdjustment.getAdjustmentType().getId());
				ps.setBigDecimal(4, paymentAdjustment.getAmount());
				ps.setString(5, paymentAdjustment.getRemarks());
				return ps;
			}
		}, holder);
		
		SupplierPaymentAdjustment updated = get(holder.getKey().longValue());
		paymentAdjustment.setId(updated.getId());
		paymentAdjustment.setSupplierPaymentAdjustmentNumber(updated.getSupplierPaymentAdjustmentNumber());
	}
	
	private Long getNextPaymentAdjustmentNumber() {
		return getNextSequenceValue(SUPPLIER_PAYMENT_ADJUSTMENT_NUMBER_SEQUENCE);
	}

	private class SupplierPaymentAdjustmentRowMapper implements RowMapper<SupplierPaymentAdjustment> {

		@Override
		public SupplierPaymentAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
			SupplierPaymentAdjustment paymentAdjustment = new SupplierPaymentAdjustment();
			paymentAdjustment.setId(rs.getLong("ID"));
			paymentAdjustment.setSupplierPaymentAdjustmentNumber(rs.getLong("SUPP_PAYMENT_ADJUSTMENT_NO"));
			paymentAdjustment.setAmount(rs.getBigDecimal("AMOUNT"));
			
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("SUPPLIER_ID"));
			supplier.setCode(rs.getString("SUPPLIER_CODE"));
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			paymentAdjustment.setSupplier(supplier);

			PurchasePaymentAdjustmentType adjustmentType = new PurchasePaymentAdjustmentType();
			adjustmentType.setId(rs.getLong("PURCHASE_PAYMENT_ADJ_TYPE_ID"));
			adjustmentType.setCode(rs.getString("ADJUSTMENT_TYPE_CODE"));
			paymentAdjustment.setAdjustmentType(adjustmentType);
			
			paymentAdjustment.setPosted("Y".equals(rs.getString("POST_IND")));
			if (paymentAdjustment.isPosted()) {
				paymentAdjustment.setPostDate(rs.getDate("POST_DT"));
				paymentAdjustment.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			paymentAdjustment.setRemarks(rs.getString("REMARKS"));
			
			return paymentAdjustment;
		}
		
	}

	private static final String FIND_BY_SUPPLIER_PAYMENT_ADJUSTMENT_NUMBER_SQL = BASE_SELECT_SQL
			+ " where a.SUPP_PAYMENT_ADJUSTMENT_NO = ?";
	
	@Override
	public SupplierPaymentAdjustment findBySupplierPaymentAdjustmentNumber(long supplierPaymentAdjustmentNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_SUPPLIER_PAYMENT_ADJUSTMENT_NUMBER_SQL, 
					paymentAdjustmentRowMapper, supplierPaymentAdjustmentNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<SupplierPaymentAdjustment> search(SupplierPaymentAdjustmentSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getPaymentAdjustmentNumber() != null) {
			sql.append(" and a.SUPP_PAYMENT_ADJUSTMENT_NO = ?");
			params.add(criteria.getPaymentAdjustmentNumber());
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and a.SUPPLIER_ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getAdjustmentType() != null) {
			sql.append(" and a.PURCHASE_PAYMENT_ADJ_TYPE_ID = ?");
			params.add(criteria.getAdjustmentType().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
		}
		
		if (criteria.getPostDateFrom() != null) {
			sql.append(" and a.POST_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateFrom()));
		}
		
		if (criteria.getPostDateTo() != null) {
			sql.append(" and a.POST_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateTo()));
		}
		
		sql.append(" order by SUPP_PAYMENT_ADJUSTMENT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), paymentAdjustmentRowMapper, params.toArray());
	}

}