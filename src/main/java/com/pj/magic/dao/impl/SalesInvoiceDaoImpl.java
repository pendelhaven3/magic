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

import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;

@Repository
public class SalesInvoiceDaoImpl extends MagicDao implements SalesInvoiceDao {
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_INVOICE_NO, CREATE_DT, RELATED_SALES_REQUISITION_NO, MODE, REMARKS, CUSTOMER_ID,"
			+ " POST_DT, POST_IND, CANCEL_DT, CANCEL_IND,"
			+ " PRICING_SCHEME_ID, d.NAME as PRICING_SCHEME_NAME,"
			+ " CREATED_BY, b.USERNAME as CREATED_BY_USERNAME,"
			+ " PAYMENT_TERM_ID, e.NAME as PAYMENT_TERM_NAME,"
			+ " POST_BY, f.USERNAME as POST_BY_USERNAME,"
			+ " CANCEL_BY, g.USERNAME as CANCEL_BY_USERNAME"
			+ " from SALES_INVOICE a"
			+ " join USER b"
			+ "   on b.ID = a.CREATED_BY"
			+ " join PRICING_SCHEME d"
			+ "   on d.ID = a.PRICING_SCHEME_ID"
			+ " join PAYMENT_TERM e"
			+ "   on e.ID = a.PAYMENT_TERM_ID"
			+ " left join USER f"
			+ "   on f.ID = a.POST_BY"
			+ " left join USER g"
			+ "   on g.ID = a.CANCEL_BY"
			+ " where 1 = 1";
	
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
			+ " (CUSTOMER_ID, CREATE_DT, CREATED_BY, RELATED_SALES_REQUISITION_NO,"
			+ "  PRICING_SCHEME_ID, MODE, REMARKS, PAYMENT_TERM_ID)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final SalesInvoice salesInvoice) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, salesInvoice.getCustomer().getId());
				ps.setDate(2, new Date(salesInvoice.getCreateDate().getTime()));
				ps.setLong(3, salesInvoice.getCreatedBy().getId());
				ps.setLong(4, salesInvoice.getRelatedSalesRequisitionNumber());
				ps.setLong(5, salesInvoice.getPricingScheme().getId());
				ps.setString(6, salesInvoice.getMode());
				ps.setString(7, salesInvoice.getRemarks());
				ps.setLong(8, salesInvoice.getPaymentTerm().getId());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		SalesInvoice updated = get(holder.getKey().longValue());
		salesInvoice.setId(updated.getId());
		salesInvoice.setSalesInvoiceNumber(updated.getSalesInvoiceNumber());
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
			salesInvoice.setCreatedBy(
					new User(rs.getLong("CREATED_BY"), rs.getString("CREATED_BY_USERNAME")));
			salesInvoice.setRelatedSalesRequisitionNumber(rs.getLong("RELATED_SALES_REQUISITION_NO"));
			salesInvoice.setCustomer(new Customer(rs.getLong("CUSTOMER_ID")));
			salesInvoice.setPricingScheme(
					new PricingScheme(rs.getLong("PRICING_SCHEME_ID"), rs.getString("PRICING_SCHEME_NAME")));
			salesInvoice.setMode(rs.getString("MODE"));
			salesInvoice.setRemarks(rs.getString("REMARKS"));
			salesInvoice.setPaymentTerm(
					new PaymentTerm(rs.getLong("PAYMENT_TERM_ID"), rs.getString("PAYMENT_TERM_NAME")));
			salesInvoice.setPosted("Y".equals(rs.getString("POST_IND")));
			salesInvoice.setPostDate(rs.getDate("POST_DT"));
			if (rs.getLong("POST_BY") != 0) {
				salesInvoice.setPostedBy(
						new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			salesInvoice.setCancelled("Y".equals(rs.getString("CANCEL_IND")));
			salesInvoice.setCancelDate(rs.getDate("CANCEL_DT"));
			if (rs.getLong("CANCEL_BY") != 0) {
				salesInvoice.setCancelledBy(
						new User(rs.getLong("CANCEL_BY"), rs.getString("CANCEL_BY_USERNAME")));
			}
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
			+ " set POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ?"
			+ " where ID = ?";
	
	private void update(SalesInvoice salesInvoice) {
		getJdbcTemplate().update(UPDATE_SQL,
				salesInvoice.isPosted() ? "Y" : "N",
				salesInvoice.isPosted() ? new Date(salesInvoice.getPostDate().getTime()) : null,
				salesInvoice.isPosted() ? salesInvoice.getPostedBy().getId() : null,
				salesInvoice.isCancelled() ? "Y" : "N",
				salesInvoice.isCancelled() ? new Date(salesInvoice.getCancelDate().getTime()) : null,
				salesInvoice.isCancelled() ? salesInvoice.getCancelledBy().getId() : null,
				salesInvoice.getId());
	}
	
}
