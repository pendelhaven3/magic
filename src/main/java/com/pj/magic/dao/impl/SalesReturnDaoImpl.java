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

import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;

@Repository
public class SalesReturnDaoImpl extends MagicDao implements SalesReturnDao {

	private static final String SALES_RETURN_NUMBER_SEQUENCE = "SALES_RETURN_NO_SEQ";
	
	private static final String BASE_SELECT_SQL = 
			"select a.ID, SALES_RETURN_NO, SALES_INVOICE_ID, b.SALES_INVOICE_NO,"
			+ " b.CUSTOMER_ID, c.NAME as CUSTOMER_NAME"
			+ " from SALES_RETURN a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID"
			+ " join CUSTOMER c"
			+ "   on c.ID = b.CUSTOMER_ID";
	
	private SalesReturnRowMapper salesReturnRowMapper = new SalesReturnRowMapper();
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by SALES_RETURN_NO desc";
	
	@Override
	public List<SalesReturn> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, salesReturnRowMapper);
	}

	private class SalesReturnRowMapper implements RowMapper<SalesReturn> {

		@Override
		public SalesReturn mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesReturn salesReturn = new SalesReturn();
			salesReturn.setId(rs.getLong("ID"));
			salesReturn.setSalesReturnNumber(rs.getLong("SALES_RETURN_NO"));
			
			SalesInvoice salesInvoice = new SalesInvoice();
			salesInvoice.setId(rs.getLong("SALES_INVOICE_ID"));
			salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			salesInvoice.setCustomer(new Customer(rs.getLong("CUSTOMER_ID"), rs.getString("CUSTOMER_NAME")));
			salesReturn.setSalesInvoice(salesInvoice);
			
			return salesReturn;
		}
		
	}

	@Override
	public void save(SalesReturn salesReturn) {
		if (salesReturn.getId() == null) {
			insert(salesReturn);
		} else {
			update(salesReturn);
		}
	}

	private void update(SalesReturn salesReturn) {
		
	}
	
	private static final String INSERT_SQL = 
			"insert into SALES_RETURN (SALES_RETURN_NO, SALES_INVOICE_ID) values (?, ?)";

	private void insert(final SalesReturn salesReturn) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextSalesReturnNumber());
				ps.setLong(2, salesReturn.getSalesInvoice().getId());
				return ps;
			}
		}, holder);
		
		SalesReturn updated = get(holder.getKey().longValue());
		salesReturn.setId(updated.getId());
		salesReturn.setSalesReturnNumber(updated.getSalesReturnNumber());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public SalesReturn get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, salesReturnRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private long getNextSalesReturnNumber() {
		return getNextSequenceValue(SALES_RETURN_NUMBER_SEQUENCE);
	}
	
}