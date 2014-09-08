package com.pj.magic.dao.impl;

import java.sql.Connection;
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

import com.pj.magic.dao.PurchaseOrderDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.Supplier;

@Repository
public class PurchaseOrderDaoImpl extends MagicDao implements PurchaseOrderDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PURCHASE_ORDER_NO, SUPPLIER_ID, POST_IND, ORDER_IND,"
			+ " a.PAYMENT_TERM_ID, REMARKS, REFERENCE_NO, b.NAME as SUPPLIER_NAME"
			+ " from PURCHASE_ORDER a, SUPPLIER b"
			+ " where a.SUPPLIER_ID = b.ID";
	
	private PurchaseOrderRowMapper purchaseOrderRowMapper = new PurchaseOrderRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public PurchaseOrder get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, purchaseOrderRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(PurchaseOrder purchaseOrder) {
		if (purchaseOrder.getId() == null) {
			insert(purchaseOrder);
		} else {
			update(purchaseOrder);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into PURCHASE_ORDER (SUPPLIER_ID) values (?)";
	
	private void insert(final PurchaseOrder purchaseOrder) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, purchaseOrder.getSupplier().getId());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		PurchaseOrder updated = get(holder.getKey().longValue());
		purchaseOrder.setId(updated.getId());
		purchaseOrder.setPurchaseOrderNumber(updated.getPurchaseOrderNumber());
	}
	
	private static final String UPDATE_SQL =
			"update PURCHASE_ORDER set SUPPLIER_ID = ?, POST_IND = ?, ORDER_IND = ?,"
			+ " PAYMENT_TERM_ID = ?, REMARKS = ?, REFERENCE_NO = ?"
			+ " where ID = ?";
	
	private void update(PurchaseOrder purchaseOrder) {
		getJdbcTemplate().update(UPDATE_SQL, 
				purchaseOrder.getSupplier().getId(),
				purchaseOrder.isPosted() ? "Y" : "N",
				purchaseOrder.isOrdered() ? "Y" : "N",
				purchaseOrder.getPaymentTerm() != null ? purchaseOrder.getPaymentTerm().getId() : null,
				purchaseOrder.getRemarks(),
				purchaseOrder.getReferenceNumber(),
				purchaseOrder.getId());
	}
	
	private class PurchaseOrderRowMapper implements RowMapper<PurchaseOrder> {

		@Override
		public PurchaseOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchaseOrder purchaseOrder = new PurchaseOrder();
			purchaseOrder.setId(rs.getLong("ID"));
			purchaseOrder.setPurchaseOrderNumber(rs.getLong("PURCHASE_ORDER_NO"));
			purchaseOrder.setSupplier(new Supplier(rs.getLong("SUPPLIER_ID"), rs.getString("SUPPLIER_NAME")));
			purchaseOrder.setPosted("Y".equals(rs.getString("POST_IND")));
			purchaseOrder.setOrdered("Y".equals(rs.getString("ORDER_IND")));
			if (rs.getLong("PAYMENT_TERM_ID") != 0) {
				purchaseOrder.setPaymentTerm(new PaymentTerm(rs.getLong("PAYMENT_TERM_ID")));
			}
			purchaseOrder.setRemarks(rs.getString("REMARKS"));
			purchaseOrder.setReferenceNumber(rs.getString("REFERENCE_NO"));
			return purchaseOrder;
		}
	}

	private static final String DELETE_SQL = "delete PURCHASE_ORDER where ID = ?";
	
	@Override
	public void delete(PurchaseOrder purchaseOrder) {
		getJdbcTemplate().update(DELETE_SQL, purchaseOrder.getId());
	}

	@Override
	public List<PurchaseOrder> search(PurchaseOrder criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" and POST_IND = ?");
		sql.append(" order by a.ID desc"); // TODO: change to be more flexible when the need arises
		
		return getJdbcTemplate().query(sql.toString(), purchaseOrderRowMapper,
				criteria.isPosted() ? "Y" : "N");
	}

}