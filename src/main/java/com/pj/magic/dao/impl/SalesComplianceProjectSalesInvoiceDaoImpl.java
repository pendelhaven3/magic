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

	private static final String DELETE_SQL = "delete from SALES_COMPLIANCE_PROJECT_SALES_INVOICE where ID = ?";
	
	@Override
	public void remove(SalesComplianceProjectSalesInvoice salesInvoice) {
		getJdbcTemplate().update(DELETE_SQL, salesInvoice.getId());
	}

}