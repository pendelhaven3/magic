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

import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;

@Repository
public class ProductDaoImpl extends MagicDao implements ProductDao {
	
	private static final String SIMPLE_SELECT_SQL =
			"select a.ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS"
			+ " from PRODUCT a, PRODUCT_PRICE b"
			+ " where a.ID = b.PRODUCT_ID";
	
	private static final String GET_ALL_SQL = SIMPLE_SELECT_SQL
			+ " order by a.CODE";

	private static final String FIND_BY_CODE_SQL = SIMPLE_SELECT_SQL
			+ " and a.CODE = ?";

	private static final String FIND_BY_ID_SQL = SIMPLE_SELECT_SQL
			+ " and a.ID = ?";

	private ProductRowMapper productRowMapper = new ProductRowMapper();
	
	@Override
	public List<Product> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, productRowMapper);
	}

	@Override
	public Product findByCode(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CODE_SQL, productRowMapper, code);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public Product get(long id) {
		return getJdbcTemplate().queryForObject(FIND_BY_ID_SQL, productRowMapper, id);
	}

	private class ProductRowMapper implements RowMapper<Product> {

		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("ID"));
			product.setCode(rs.getString("CODE"));
			product.setDescription(rs.getString("DESCRIPTION"));
			if ("Y".equals(rs.getString("UNIT_IND_CSE"))) {
				product.getUnits().add("CSE");
				product.getUnitPrices().add(new UnitPrice("CSE", rs.getBigDecimal("UNIT_PRICE_CSE")));
				product.getUnitQuantities().add(new UnitQuantity("CSE", rs.getInt("AVAIL_QTY_CSE")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_CTN"))) {
				product.getUnits().add("CTN");
				product.getUnitPrices().add(new UnitPrice("CTN", rs.getBigDecimal("UNIT_PRICE_CTN")));
				product.getUnitQuantities().add(new UnitQuantity("CTN", rs.getInt("AVAIL_QTY_CTN")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_DOZ"))) {
				product.getUnits().add("DOZ");
				product.getUnitPrices().add(new UnitPrice("DOZ", rs.getBigDecimal("UNIT_PRICE_DOZ")));
				product.getUnitQuantities().add(new UnitQuantity("DOZ", rs.getInt("AVAIL_QTY_DOZ")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
				product.getUnits().add("PCS");
				product.getUnitPrices().add(new UnitPrice("PCS", rs.getBigDecimal("UNIT_PRICE_PCS")));
				product.getUnitQuantities().add(new UnitQuantity("PCS", rs.getInt("AVAIL_QTY_PCS")));
			}
			return product;
		}
		
	}

	private static final String UPDATE_AVAILABLE_QUANTITIES_SQL =
			"update PRODUCT"
			+ " set AVAIL_QTY_CSE = ?, AVAIL_QTY_CTN = ?, AVAIL_QTY_DOZ = ?, AVAIL_QTY_PCS = ?"
			+ " where ID = ?";
	
	@Override
	public void updateAvailableQuantities(Product product) {
		getJdbcTemplate().update(UPDATE_AVAILABLE_QUANTITIES_SQL,
				product.getUnitQuantity(Unit.CASE),
				product.getUnitQuantity(Unit.CARTON),
				product.getUnitQuantity(Unit.DOZEN),
				product.getUnitQuantity(Unit.PIECES),
				product.getId());
	}

	@Override
	public void save(Product product) {
		if (product.getId() == null) {
			insert(product);
		} else {
			update(product);
		}
	}

	private static final String UPDATE_SQL =
			"update PRODUCT set CODE = ?, DESCRIPTION = ? where ID = ?";
	
	private void update(Product product) {
		getJdbcTemplate().update(UPDATE_SQL, product.getCode(), product.getDescription(), product.getId());
	}

	private static final String INSERT_SQL =
			"insert into PRODUCT (CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final Product product) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, product.getCode());
				ps.setString(2, product.getDescription());
				ps.setString(3, product.hasUnit(Unit.CASE) ? "Y" : "N");
				ps.setString(4, product.hasUnit(Unit.CARTON) ? "Y" : "N");
				ps.setString(5, product.hasUnit(Unit.DOZEN) ? "Y" : "N");
				ps.setString(6, product.hasUnit(Unit.PIECES) ? "Y" : "N");
				ps.setInt(7, product.getUnitQuantity(Unit.CASE));
				ps.setInt(8, product.getUnitQuantity(Unit.CARTON));
				ps.setInt(9, product.getUnitQuantity(Unit.DOZEN));
				ps.setInt(10, product.getUnitQuantity(Unit.PIECES));
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		product.setId(holder.getKey().longValue());
	}

	private static final String FIND_FIRST_WITH_CODE_LIKE_SQL = SIMPLE_SELECT_SQL
			+ " and CODE like ? limit 1";
			
	
	@Override
	public Product findFirstWithCodeLike(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_WITH_CODE_LIKE_SQL, productRowMapper, code + "%");
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}
