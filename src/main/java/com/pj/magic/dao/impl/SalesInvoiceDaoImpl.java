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
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;

@Repository
public class SalesInvoiceDaoImpl extends MagicDao implements SalesInvoiceDao {
	
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
			"insert into SALES_INVOICE (CUSTOMER_NAME, POST_DT, POSTED_BY, SALES_INVOICE_ID) values (?, ?, ?, ?)";
	
	private void insert(final SalesInvoice salesInvoice) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, salesInvoice.getCustomerName());
				ps.setDate(2, new Date(salesInvoice.getPostDate().getTime()));
				ps.setString(3, salesInvoice.getPostedBy());
				ps.setLong(4, salesInvoice.getOrigin().getId());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		SalesInvoice updated = get(holder.getKey().longValue());
		salesInvoice.setId(updated.getId());
		salesInvoice.setSalesInvoiceNumber(updated.getSalesInvoiceNumber());
	}

	private void update(SalesInvoice salesInvoice) {
		// TODO: Put implementation here
	}
	
	private static final String GET_SQL =
			"select ID, SALES_INVOICE_NO, CUSTOMER_NAME, POST_DT, POSTED_BY, SALES_INVOICE_ID"
			+ " from SALES_INVOICE where ID = ?";
	
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
			salesInvoice.setCustomerName(rs.getString("CUSTOMER_NAME"));
			salesInvoice.setPostDate(rs.getDate("POST_DT"));
			salesInvoice.setPostedBy(rs.getString("POSTED_BY"));
			salesInvoice.setOrigin(new SalesRequisition(rs.getLong("ID")));
			return salesInvoice;
		}
		
	}

	private static final String GET_ALL_SQL = 
			"select ID, SALES_INVOICE_NO, CUSTOMER_NAME, POST_DT, POSTED_BY from SALES_INVOICE";
	
	@Override
	public List<SalesInvoice> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, salesInvoiceRowMapper);
	}
	
}
