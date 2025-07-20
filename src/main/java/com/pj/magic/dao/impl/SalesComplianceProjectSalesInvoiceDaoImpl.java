package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesComplianceProjectSalesInvoiceDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.model.SalesInvoice;

@Repository
public class SalesComplianceProjectSalesInvoiceDaoImpl extends MagicDao implements SalesComplianceProjectSalesInvoiceDao {
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, a.SALES_COMPLIANCE_PROJECT_ID, a.SALES_INVOICE_ID, b.SALES_INVOICE_NO, b.TRANSACTION_DT, b.PRICING_SCHEME_ID"
			+ " , b.PRINT_INVOICE_NO"
			+ " , b.CUSTOMER_ID, c.NAME as CUSTOMER_NAME, c.TIN, c.BUSINESS_ADDRESS"
			+ " , d.SALES_COMPLIANCE_PROJECT_NO, d.NAME as SALES_COMPLIANCE_PROJECT_NAME"
			+ " from SALES_COMPLIANCE_PROJECT_SALES_INVOICE a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID"
			+ " join CUSTOMER c"
			+ "   on c.ID = b.CUSTOMER_ID"
			+ " join SALES_COMPLIANCE_PROJECT d"
			+ "   on d.ID = a.SALES_COMPLIANCE_PROJECT_ID"
			+ " where 1 = 1";
	
	private RowMapper<SalesComplianceProjectSalesInvoice> rowMapper = (rs, rownum) -> {
		SalesComplianceProject project = new SalesComplianceProject();
		project.setId(rs.getLong("SALES_COMPLIANCE_PROJECT_ID"));
		project.setSalesComplianceProjectNumber(rs.getLong("SALES_COMPLIANCE_PROJECT_NO"));
		project.setName(rs.getString("SALES_COMPLIANCE_PROJECT_NAME"));
		
		SalesComplianceProjectSalesInvoice projectSalesInvoice = new SalesComplianceProjectSalesInvoice();
		projectSalesInvoice.setId(rs.getLong("ID"));
		projectSalesInvoice.setSalesComplianceProject(project);
		
		Customer customer = new Customer();
		customer.setId(rs.getLong("CUSTOMER_ID"));
		customer.setName(rs.getString("CUSTOMER_NAME"));
		customer.setTin(rs.getString("TIN"));
		customer.setBusinessAddress(rs.getString("BUSINESS_ADDRESS"));
		
		SalesInvoice salesInvoice = new SalesInvoice(rs.getLong("SALES_INVOICE_ID"));
		salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
		salesInvoice.setTransactionDate(rs.getDate("TRANSACTION_DT"));
		salesInvoice.setCustomer(customer);
		salesInvoice.setPricingScheme(new PricingScheme(rs.getLong("PRICING_SCHEME_ID")));
		salesInvoice.setPrintInvoiceNumber(rs.getString("PRINT_INVOICE_NO"));
		projectSalesInvoice.setSalesInvoice(salesInvoice);
		
		return projectSalesInvoice;
	};
	
	private static final String FIND_ALL_BY_SALES_COMPLIANCE_PROJECT_SQL = BASE_SELECT_SQL
			+ " and a.SALES_COMPLIANCE_PROJECT_ID = ? order by b.SALES_INVOICE_NO";
	
	@Override
	public List<SalesComplianceProjectSalesInvoice> findAllBySalesComplianceProject(SalesComplianceProject project) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_COMPLIANCE_PROJECT_SQL, rowMapper, project.getId());
	}

	@Override
	public void save(SalesComplianceProjectSalesInvoice projectSalesInvoice) {
		insert(projectSalesInvoice);
	}

	private static final String INSERT_SQL =
			"insert into SALES_COMPLIANCE_PROJECT_SALES_INVOICE "
			+ " (SALES_COMPLIANCE_PROJECT_ID, SALES_INVOICE_ID)"
			+ " values (?, ?)";
	
	private void insert(final SalesComplianceProjectSalesInvoice salesInvoice) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, salesInvoice.getSalesComplianceProject().getId());
				ps.setLong(2, salesInvoice.getSalesInvoice().getId());
				return ps;
			}
		}, holder);
		
		SalesComplianceProjectSalesInvoice updated = get(holder.getKey().longValue());
		salesInvoice.setId(updated.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public SalesComplianceProjectSalesInvoice get(Long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	/*
	private static final String UPDATE_SQL =
			"update SALES_INVOICE"
			+ " set MARK_IND = ?, MARK_DT = ?, MARK_BY = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ?, PRINT_IND = ?"
			+ " where ID = ?";
	
	private void update(SalesInvoice salesInvoice) {
		getJdbcTemplate().update(UPDATE_SQL,
				salesInvoice.isMarked() ? "Y" : "N",
				salesInvoice.isMarked() ? new Date(salesInvoice.getMarkDate().getTime()) : null,
				salesInvoice.isMarked() ? salesInvoice.getMarkedBy().getId() : null,
				salesInvoice.isCancelled() ? "Y" : "N",
				salesInvoice.isCancelled() ? salesInvoice.getCancelDate() : null,
				salesInvoice.isCancelled() ? salesInvoice.getCancelledBy().getId() : null,
				salesInvoice.isPrinted() ? "Y" : "N",
				salesInvoice.getId());
	}

	private static final String PAID_WHERE_CLAUSE_SQL =
			" and exists("
			+ "   select 1"
			+ "   from PAYMENT_SALES_INVOICE psi"
			+ "   join PAYMENT p"
			+ "     on p.ID = psi.PAYMENT_ID"
			+ "   where psi.SALES_INVOICE_ID = a.ID"
			+ "   and p.POST_IND = 'Y'"
			+ " )";
	
	private static final String UNPAID_WHERE_CLAUSE_SQL =
			" and CANCEL_IND = 'N'"
			+ " and not exists("
			+ "   select 1"
			+ "   from PAYMENT_SALES_INVOICE psi"
			+ "   join PAYMENT p"
			+ "     on p.ID = psi.PAYMENT_ID"
			+ "   where psi.SALES_INVOICE_ID = a.ID"
			+ "   and p.POST_IND = 'Y'"
			+ " )";
	
	private static final String UNREDEEMED_PROMO_WHERE_CLAUSE_SQL =
			"   and not exists("
			+ "   select 1"
			+ "   from PROMO_REDEMPTION_SALES_INVOICE prsi"
			+ "   join PROMO_REDEMPTION pr"
			+ "     on pr.ID = prsi.PROMO_REDEMPTION_ID"
			+ "   where prsi.SALES_INVOICE_ID = a.ID"
			+ "   and pr.PROMO_ID = ?"
			+ " )"
			+ " and a.CANCEL_IND = 'N'";
	
	private static final String UNCLAIMED_RAFFLE_PROMO_WHERE_CLAUSE_SQL =
			"   and not exists("
			+ "   select 1"
			+ "   from PROMO_RAFFLE_TICKET_CLAIM_SALES_INVOICES prtcsi"
			+ "   join PROMO_RAFFLE_TICKET_CLAIMS prtc"
			+ "     on prtc.ID = prtcsi.CLAIM_ID"
			+ "   where prtcsi.SALES_INVOICE_ID = a.ID"
			+ "   and prtc.PROMO_ID = ?"
			+ " )";
	
	@Override
	public List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.isMarked() != null) {
			sql.append(" and MARK_IND = ?");
			params.add(criteria.isMarked() ? "Y" : "N");
		}
		
		if (criteria.isCancelled() != null) {
			sql.append(" and CANCEL_IND = ?");
			params.add(criteria.isCancelled() ? "Y" : "N");
		}
		
		if (criteria.getSalesInvoiceNumber() != null) {
			sql.append(" and SALES_INVOICE_NO = ?");
			params.add(criteria.getSalesInvoiceNumber());
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getTransactionDate() != null) {
			sql.append(" and a.TRANSACTION_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getTransactionDate()));
		}
		
		if (criteria.getTransactionDateFrom() != null) {
			sql.append(" and a.TRANSACTION_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getTransactionDateFrom()));
		}
		
		if (criteria.getTransactionDateTo() != null) {
			sql.append(" and a.TRANSACTION_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getTransactionDateTo()));
		}
		
		if (criteria.getPricingScheme() != null) {
			sql.append(" and a.PRICING_SCHEME_ID = ?");
			params.add(criteria.getPricingScheme().getId());
		}
		
		if (criteria.getPaid() != null) {
			if (criteria.getPaid()) {
				sql.append(PAID_WHERE_CLAUSE_SQL);
			} else {
				sql.append(UNPAID_WHERE_CLAUSE_SQL);
			}
		}
		
		if (criteria.getUnredeemedPromo() != null) {
			sql.append(UNREDEEMED_PROMO_WHERE_CLAUSE_SQL);
			params.add(criteria.getUnredeemedPromo().getId());
		}
		
		if (criteria.getUnclaimedRafflePromo() != null) {
			sql.append(UNCLAIMED_RAFFLE_PROMO_WHERE_CLAUSE_SQL);
			params.add(criteria.getUnclaimedRafflePromo().getId());
		}
		
		if (!StringUtils.isEmpty(criteria.getOrderBy())) {
			sql.append(" order by ").append(criteria.getOrderBy());
		} else {
			sql.append(" order by SALES_INVOICE_NO desc");
		}
		
		return getJdbcTemplate().query(sql.toString(), salesInvoiceRowMapper, params.toArray());
	}

	private static final String FIND_BY_SALES_INVOICE_NUMBER_SQL = BASE_SELECT_SQL
			+ " and a.SALES_INVOICE_NO = ?";
	
	@Override
	public SalesInvoice findBySalesInvoiceNumber(long salesInvoiceNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_SALES_INVOICE_NUMBER_SQL, salesInvoiceRowMapper,
					salesInvoiceNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_ALL_FOR_PAYMENT_BY_CUSTOMER_SQL = BASE_SELECT_SQL
			+ " and a.CUSTOMER_ID = ?"
			+ " and a.CANCEL_IND = 'N'"
			+ " and not exists("
			+ "   select 1"
			+ "   from PAYMENT_SALES_INVOICE psi"
			+ "   join PAYMENT pay"
			+ "     on pay.ID = psi.PAYMENT_ID"
			+ "   where SALES_INVOICE_ID = a.ID"
			+ "   and pay.CANCEL_IND = 'N'"
			+ " )";
	
	@Override
	public List<SalesInvoice> findAllForPaymentByCustomer(Customer customer) {
		return getJdbcTemplate().query(FIND_ALL_FOR_PAYMENT_BY_CUSTOMER_SQL, salesInvoiceRowMapper, 
				customer.getId());
	}

	private static final String FIND_MOST_RECENT_BY_CUSTOMER_AND_PRODUCT_SQL =
			"select a.ID"
			+ " from SALES_INVOICE a"
			+ " join SALES_INVOICE_ITEM b"
			+ "   on b.SALES_INVOICE_ID = a.ID"
			+ " where a.CUSTOMER_ID = ?"
			+ " and b.PRODUCT_ID = ?"
			+ " and a.MARK_IND = 'Y'"
			+ " and a.CANCEL_IND = 'N'"
			+ " order by a.POST_DT desc"
			+ " limit 1";
	
	@Override
	public SalesInvoice findMostRecentByCustomerAndProduct(Customer customer, Product product) {
		Long id = null;
		try {
			id = getJdbcTemplate().queryForObject(FIND_MOST_RECENT_BY_CUSTOMER_AND_PRODUCT_SQL, Long.class,
					customer.getId(), product.getId());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		return get(id);
	}
	*/

}