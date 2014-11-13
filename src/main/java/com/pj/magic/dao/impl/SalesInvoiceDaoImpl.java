package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
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

import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;

@Repository
public class SalesInvoiceDaoImpl extends MagicDao implements SalesInvoiceDao {
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_INVOICE_NO, CREATE_DT, RELATED_SALES_REQUISITION_NO, MODE, REMARKS, CUSTOMER_ID,"
			+ " POST_DT, MARK_IND, MARK_DT, CANCEL_DT, CANCEL_IND, TRANSACTION_DT, VAT_AMOUNT,"
			+ " PRICING_SCHEME_ID, d.NAME as PRICING_SCHEME_NAME,"
			+ " ENCODER, b.USERNAME as ENCODER_USERNAME,"
			+ " PAYMENT_TERM_ID, e.NAME as PAYMENT_TERM_NAME,"
			+ " POST_BY, f.USERNAME as POST_BY_USERNAME,"
			+ " MARK_BY, g.USERNAME as MARK_BY_USERNAME,"
			+ " CANCEL_BY, h.USERNAME as CANCEL_BY_USERNAME"
			+ " from SALES_INVOICE a"
			+ " join USER b"
			+ "   on b.ID = a.ENCODER"
			+ " join PRICING_SCHEME d"
			+ "   on d.ID = a.PRICING_SCHEME_ID"
			+ " join PAYMENT_TERM e"
			+ "   on e.ID = a.PAYMENT_TERM_ID"
			+ " left join USER f"
			+ "   on f.ID = a.POST_BY"
			+ " left join USER g"
			+ "   on g.ID = a.MARK_BY"
			+ " left join USER h"
			+ "   on h.ID = a.CANCEL_BY"
			+ " where 1 = 1";
	
	private static final String SALES_INVOICE_NUMBER_SEQUENCE = "SALES_INVOICE_NO_SEQ";
	
	private SalesInvoiceRowMapper salesInvoiceRowMapper = new SalesInvoiceRowMapper();
	
	@Override
	public void save(SalesInvoice salesInvoice) {
		if (salesInvoice.getId() == null) {
			insert(salesInvoice);
		} else {
			update(salesInvoice);
		}
	}

	private static final String INSERT_SQL =
			"insert into SALES_INVOICE "
			+ " (SALES_INVOICE_NO, CUSTOMER_ID, CREATE_DT, TRANSACTION_DT, ENCODER, RELATED_SALES_REQUISITION_NO,"
			+ "  PRICING_SCHEME_ID, MODE, REMARKS, PAYMENT_TERM_ID, POST_DT, POST_BY, VAT_AMOUNT)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final SalesInvoice salesInvoice) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextSalesInvoiceNumber());
				ps.setLong(2, salesInvoice.getCustomer().getId());
				ps.setDate(3, new Date(salesInvoice.getCreateDate().getTime()));
				ps.setDate(4, new Date(salesInvoice.getTransactionDate().getTime()));
				ps.setLong(5, salesInvoice.getEncoder().getId());
				ps.setLong(6, salesInvoice.getRelatedSalesRequisitionNumber());
				ps.setLong(7, salesInvoice.getPricingScheme().getId());
				ps.setString(8, salesInvoice.getMode());
				ps.setString(9, salesInvoice.getRemarks());
				ps.setLong(10, salesInvoice.getPaymentTerm().getId());
				ps.setDate(11, new Date(salesInvoice.getPostDate().getTime()));
				ps.setLong(12, salesInvoice.getPostedBy().getId());
				ps.setBigDecimal(13, salesInvoice.getVatAmount());
				return ps;
			}
		}, holder);
		
		SalesInvoice updated = get(holder.getKey().longValue());
		salesInvoice.setId(updated.getId());
		salesInvoice.setSalesInvoiceNumber(updated.getSalesInvoiceNumber());
	}

	private long getNextSalesInvoiceNumber() {
		return getNextSequenceValue(SALES_INVOICE_NUMBER_SEQUENCE);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public SalesInvoice get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, salesInvoiceRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class SalesInvoiceRowMapper implements RowMapper<SalesInvoice> {

		@Override
		public SalesInvoice mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesInvoice salesInvoice = new SalesInvoice();
			salesInvoice.setId(rs.getLong("ID"));
			salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			salesInvoice.setCreateDate(rs.getDate("CREATE_DT"));
			salesInvoice.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			salesInvoice.setEncoder(new User(rs.getLong("ENCODER"), rs.getString("ENCODER_USERNAME")));
			salesInvoice.setRelatedSalesRequisitionNumber(rs.getLong("RELATED_SALES_REQUISITION_NO"));
			salesInvoice.setCustomer(new Customer(rs.getLong("CUSTOMER_ID")));
			salesInvoice.setPricingScheme(
					new PricingScheme(rs.getLong("PRICING_SCHEME_ID"), rs.getString("PRICING_SCHEME_NAME")));
			salesInvoice.setMode(rs.getString("MODE"));
			salesInvoice.setRemarks(rs.getString("REMARKS"));
			salesInvoice.setPaymentTerm(
					new PaymentTerm(rs.getLong("PAYMENT_TERM_ID"), rs.getString("PAYMENT_TERM_NAME")));
			salesInvoice.setPostDate(rs.getDate("POST_DT"));
			salesInvoice.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			salesInvoice.setMarked("Y".equals(rs.getString("MARK_IND")));
			salesInvoice.setMarkDate(rs.getDate("MARK_DT"));
			if (rs.getLong("MARK_BY") != 0) {
				salesInvoice.setMarkedBy(
						new User(rs.getLong("MARK_BY"), rs.getString("MARK_BY_USERNAME")));
			}
			salesInvoice.setCancelled("Y".equals(rs.getString("CANCEL_IND")));
			salesInvoice.setCancelDate(rs.getDate("CANCEL_DT"));
			if (rs.getLong("CANCEL_BY") != 0) {
				salesInvoice.setCancelledBy(
						new User(rs.getLong("CANCEL_BY"), rs.getString("CANCEL_BY_USERNAME")));
			}
			salesInvoice.setVatAmount(rs.getBigDecimal("VAT_AMOUNT"));
			return salesInvoice;
		}
		
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.ID desc";
	
	@Override
	public List<SalesInvoice> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, salesInvoiceRowMapper);
	}

	private static final String UPDATE_SQL =
			"update SALES_INVOICE"
			+ " set MARK_IND = ?, MARK_DT = ?, MARK_BY = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ?"
			+ " where ID = ?";
	
	private void update(SalesInvoice salesInvoice) {
		getJdbcTemplate().update(UPDATE_SQL,
				salesInvoice.isMarked() ? "Y" : "N",
				salesInvoice.isMarked() ? new Date(salesInvoice.getMarkDate().getTime()) : null,
				salesInvoice.isMarked() ? salesInvoice.getMarkedBy().getId() : null,
				salesInvoice.isCancelled() ? "Y" : "N",
				salesInvoice.isCancelled() ? new Date(salesInvoice.getCancelDate().getTime()) : null,
				salesInvoice.isCancelled() ? salesInvoice.getCancelledBy().getId() : null,
				salesInvoice.getId());
	}

	@Override
	public List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria) {
		StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.isMarked() != null) {
			sb.append(" and MARK_IND = ?");
			params.add(criteria.isMarked() ? "Y" : "N");
		}
		
		if (criteria.isCancelled() != null) {
			sb.append(" and CANCEL_IND = ?");
			params.add(criteria.isCancelled() ? "Y" : "N");
		}
		
		if (criteria.getSalesInvoiceNumber() != null) {
			sb.append(" and SALES_INVOICE_NO = ?");
			params.add(criteria.getSalesInvoiceNumber());
		}
		
		if (criteria.getCustomer() != null) {
			sb.append(" and CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		sb.append(" order by SALES_INVOICE_NO desc");
		return getJdbcTemplate().query(sb.toString(), salesInvoiceRowMapper, params.toArray());
	}

	private static final String FIND_ALL_UNPAID_BY_CUSTOMER_SQL = BASE_SELECT_SQL
			+ " and a.CUSTOMER_ID = ?"
			+ " and not exists("
			+ "   select 1"
			+ "   from PAYMENT_ITEM pi"
			+ "   where pi.SALES_INVOICE_ID = a.ID"
			+ " )"
			+ " order by SALES_INVOICE_NO";
	
	@Override
	public List<SalesInvoice> findAllUnpaidByCustomer(Customer customer) {
		return getJdbcTemplate().query(FIND_ALL_UNPAID_BY_CUSTOMER_SQL, salesInvoiceRowMapper, customer.getId());
	}
	
}
