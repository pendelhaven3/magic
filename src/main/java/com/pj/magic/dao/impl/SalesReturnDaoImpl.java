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

import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.User;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class SalesReturnDaoImpl extends MagicDao implements SalesReturnDao {

	private static final String SALES_RETURN_NUMBER_SEQUENCE = "SALES_RETURN_NO_SEQ";
	
	private static final String BASE_SELECT_SQL = 
			"select a.ID, SALES_RETURN_NO, SALES_INVOICE_ID, b.SALES_INVOICE_NO, a.POST_IND, a.POST_DT, a.POST_BY,"
			+ " PAID_IND, PAID_BY, PAID_DT,"
			+ " b.CUSTOMER_ID, c.NAME as CUSTOMER_NAME,"
			+ " d.USERNAME as POST_BY_USERNAME,"
			+ " e.USERNAME as PAID_BY_USERNAME,"
			+ " a.PAYMENT_TERMINAL_ID, f.NAME as PAYMENT_TERMINAL_NAME"
			+ " from SALES_RETURN a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID"
			+ " join CUSTOMER c"
			+ "   on c.ID = b.CUSTOMER_ID"
			+ " left join USER d"
			+ "   on d.ID = a.POST_BY"
			+ " left join USER e"
			+ "   on e.ID = a.PAID_BY"
			+ " left join PAYMENT_TERMINAL f"
			+ "   on f.ID = a.PAYMENT_TERMINAL_ID";
	
	private SalesReturnRowMapper salesReturnRowMapper = new SalesReturnRowMapper();
	
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
			
			salesReturn.setPosted("Y".equals(rs.getString("POST_IND")));
			if (salesReturn.isPosted()) {
				salesReturn.setPostDate(rs.getTimestamp("POST_DT"));
				salesReturn.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			salesReturn.setPaid("Y".equals(rs.getString("PAID_IND")));
			if (salesReturn.isPaid()) {
				salesReturn.setPaidDate(rs.getTimestamp("PAID_DT"));
				salesReturn.setPaidBy(new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
				salesReturn.setPaymentTerminal(new PaymentTerminal(
						rs.getLong("PAYMENT_TERMINAL_ID"), rs.getString("PAYMENT_TERMINAL_NAME")));
			}
			
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

	private static final String UPDATE_SQL =
			"update SALES_RETURN set SALES_INVOICE_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " PAID_IND = ?, PAID_DT = ?, PAID_BY = ?, PAYMENT_TERMINAL_ID = ? where ID = ?";
	
	private void update(SalesReturn salesReturn) {
		getJdbcTemplate().update(UPDATE_SQL,
				salesReturn.getSalesInvoice().getId(),
				salesReturn.isPosted() ? "Y" : "N",
				salesReturn.isPosted() ? salesReturn.getPostDate() : null,
				salesReturn.isPosted() ? salesReturn.getPostedBy().getId() : null,
				salesReturn.isPaid() ? "Y" : "N",
				salesReturn.isPaid() ? salesReturn.getPaidDate() : null,
				salesReturn.isPaid() ? salesReturn.getPaidBy().getId() : null,
				salesReturn.isPaid() ? salesReturn.getPaymentTerminal().getId() : null,
				salesReturn.getId());
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

	private static final String PAYMENT_WHERE_CLAUSE_SQL =
			"   and exists ("
			+ "   select 1"
			+ "   from PAYMENT_SALES_RETURN psr"
			+ "   where psr.PAYMENT_ID = ?"
			+ "   and psr.SALES_RETURN_ID = a.ID"
			+ " )";
	
	@Override
	public List<SalesReturn> search(SalesReturnSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getSalesReturnNumber() != null) {
			sql.append(" and a.SALES_RETURN_NO = ?");
			params.add(criteria.getSalesReturnNumber());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and b.CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getSalesInvoice() != null) {
			sql.append(" and SALES_INVOICE_ID = ?");
			params.add(criteria.getSalesInvoice().getId());
		}
		
		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT >= ? and a.POST_DT <= DATE_ADD(?, INTERVAL 1 DAY)");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
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
		
		if (criteria.getPayment() != null) {
			sql.append(PAYMENT_WHERE_CLAUSE_SQL);
			params.add(criteria.getPayment().getId());
		}
		
		sql.append(" order by SALES_RETURN_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), salesReturnRowMapper, params.toArray());
	}

	private static final String SAVE_PAYMENT_SALES_RETURN_SQL = 
			"insert into PAYMENT_SALES_RETURN (PAYMENT_ID, SALES_RETURN_ID) values (?, ?)";
	
	@Override
	public void savePaymentSalesReturn(Payment payment, SalesReturn salesReturn) {
		getJdbcTemplate().update(SAVE_PAYMENT_SALES_RETURN_SQL, payment.getId(), salesReturn.getId());
	}
	
}