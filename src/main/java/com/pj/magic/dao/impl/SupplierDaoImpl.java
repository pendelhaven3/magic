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

import com.pj.magic.dao.SupplierDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;

@Repository
public class SupplierDaoImpl extends MagicDao implements SupplierDao {
	
	private static final String BASE_SQL = "select ID, NAME from SUPPLIER a";
	
	private SupplierRowMapper supplierRowMapper = new SupplierRowMapper();
	
	@Override
	public void save(Supplier supplier) {
		if (supplier.getId() == null) {
			insert(supplier);
		} else {
			update(supplier);
		}
	}

	private static final String INSERT_SQL = "insert into SUPPLIER (NAME) values (?)";
	
	private void insert(final Supplier supplier) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, supplier.getName());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		supplier.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update SUPPLIER set NAME = ? where ID = ?";
	
	private void update(Supplier supplier) {
		getJdbcTemplate().update(UPDATE_SQL, supplier.getName(), supplier.getId());
	}

	private static final String GET_SQL = BASE_SQL + " where id = ?";
	
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
	
}
