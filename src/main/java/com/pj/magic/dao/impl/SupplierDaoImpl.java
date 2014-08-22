package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SupplierDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;

@Repository
public class SupplierDaoImpl extends MagicDao implements SupplierDao {
	
	private static final String BASE_SQL = "select a.ID, a.NAME, ADDRESS, CONTACT_NUMBER, CONTACT_PERSON,"
			+ " FAX_NUMBER, EMAIL_ADDRESS, TIN, PAYMENT_TERM_ID, b.NAME as PAYMENT_TERM_NAME, b.NUMBER_OF_DAYS"
			+ " from SUPPLIER a"
			+ " left join PAYMENT_TERM b"
			+ " 	on b.ID = a.PAYMENT_TERM_ID";
	
	private SupplierRowMapper supplierRowMapper = new SupplierRowMapper();
	
	@Override
	public void save(Supplier supplier) {
		if (supplier.getId() == null) {
			insert(supplier);
		} else {
			update(supplier);
		}
	}

	private static final String INSERT_SQL = "insert into SUPPLIER"
			+ " (NAME, ADDRESS, CONTACT_NUMBER, CONTACT_PERSON, FAX_NUMBER, EMAIL_ADDRESS, TIN, PAYMENT_TERM_ID)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final Supplier supplier) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, supplier.getName());
				ps.setString(2, supplier.getAddress());
				ps.setString(3, supplier.getContactNumber());
				ps.setString(4, supplier.getContactPerson());
				ps.setString(5, supplier.getFaxNumber());
				ps.setString(6, supplier.getEmailAddress());
				ps.setString(7, supplier.getTin());
				if (supplier.getPaymentTerm() != null) {
					ps.setLong(8, supplier.getPaymentTerm().getId());
				} else {
					ps.setNull(8, Types.INTEGER);
				}
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		supplier.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update SUPPLIER"
			+ " set NAME = ?, ADDRESS = ?, CONTACT_NUMBER = ?, CONTACT_PERSON = ?,"
			+ " FAX_NUMBER = ?, EMAIL_ADDRESS = ?, TIN = ?, PAYMENT_TERM_ID = ? where ID = ?";
	
	private void update(Supplier supplier) {
		getJdbcTemplate().update(UPDATE_SQL, 
				supplier.getName(),
				supplier.getAddress(),
				supplier.getContactNumber(),
				supplier.getContactPerson(),
				supplier.getFaxNumber(),
				supplier.getEmailAddress(),
				supplier.getTin(),
				(supplier.getPaymentTerm() != null) ? supplier.getPaymentTerm().getId() : null,
				supplier.getId());
	}

	private static final String GET_SQL = BASE_SQL + " where a.ID = ?";
	
	@Override
	public Supplier get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, supplierRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SQL + " order by NAME";
	
	@Override
	public List<Supplier> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, supplierRowMapper);
	}

	private class SupplierRowMapper implements RowMapper<Supplier> {

		@Override
		public Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("ID"));
			supplier.setName(rs.getString("NAME"));
			supplier.setAddress(rs.getString("ADDRESS"));
			supplier.setContactNumber(rs.getString("CONTACT_NUMBER"));
			supplier.setContactPerson(rs.getString("CONTACT_PERSON"));
			supplier.setFaxNumber(rs.getString("FAX_NUMBER"));
			supplier.setEmailAddress(rs.getString("EMAIL_ADDRESS"));
			if (rs.getLong("PAYMENT_TERM_ID") != 0) {
				PaymentTerm paymentTerm = new PaymentTerm();
				paymentTerm.setId(rs.getLong("PAYMENT_TERM_ID"));
				paymentTerm.setName(rs.getString("PAYMENT_TERM_NAME"));
				paymentTerm.setNumberOfDays(rs.getInt("NUMBER_OF_DAYS"));
				supplier.setPaymentTerm(paymentTerm);
			}
			supplier.setTin(rs.getString("TIN"));
			return supplier;
		}
		
	}

	private static final String FIND_ALL_BY_PRODUCT = BASE_SQL
			+ " join SUPPLIER_PRODUCT b"
			+ "		on b.SUPPLIER_ID = a.ID"
			+ " where b.PRODUCT_ID = ?"
			+ " order by a.NAME";
	
	@Override
	public List<Supplier> findAllByProduct(Product product) {
		return getJdbcTemplate().query(FIND_ALL_BY_PRODUCT, supplierRowMapper, product.getId());
	}

	private static final String SAVE_PRODUCT_SUPPLIER_SQL =
			" insert into SUPPLIER_PRODUCT (SUPPLIER_ID, PRODUCT_ID) values (?, ?)";
	
	@Override
	public void saveSupplierProduct(Supplier supplier, Product product) {
		getJdbcTemplate().update(SAVE_PRODUCT_SUPPLIER_SQL, supplier.getId(), product.getId());
	}
	
	private static final String FIND_ALL_NOT_HAVING_PRODUCT_SQL = BASE_SQL +
			" where not exists("
			+ "		select 1"
			+ "		from SUPPLIER_PRODUCT b"
			+ "		where b.SUPPLIER_ID = a.ID"
			+ "		and b.PRODUCT_ID = ?"
			+ ")";

	@Override
	public List<Supplier> findAllNotHavingProduct(Product product) {
		return getJdbcTemplate().query(FIND_ALL_NOT_HAVING_PRODUCT_SQL, supplierRowMapper, product.getId());
	}

	private static final String DELETE_SUPPLIER_PRODUCT_SQL =
			" delete from SUPPLIER_PRODUCT where SUPPLIER_ID = ? and PRODUCT_ID = ?";
	
	@Override
	public void deleteSupplierProduct(Supplier supplier, Product product) {
		getJdbcTemplate().update(DELETE_SUPPLIER_PRODUCT_SQL, supplier.getId(), product.getId());
	}
	
}
