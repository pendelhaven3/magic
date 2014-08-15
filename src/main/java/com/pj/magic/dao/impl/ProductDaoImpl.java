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

import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;

@Repository
public class ProductDaoImpl extends MagicDao implements ProductDao {
	
	// TODO: Rename other SIMPLE_SELECT_SQL to BASE_SQL
	private static final String BASE_SQL =
			"select a.ID, CODE, DESCRIPTION, MAX_STOCK_LEVEL, MIN_STOCK_LEVEL, ACTIVE_IND,"
			+ " UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS,"
			+ " MANUFACTURER_ID, c.NAME as MANUFACTURER_NAME,"
			+ " CATEGORY_ID, d.NAME as CATEGORY_NAME"
			+ " from PRODUCT a"
			+ " join PRODUCT_PRICE b"
			+ " 	on b.PRODUCT_ID = a.ID"
			+ " left join MANUFACTURER c"
			+ "		on c.ID = a.MANUFACTURER_ID"
			+ " left join PRODUCT_CATEGORY d"
			+ "		on d.ID = a.CATEGORY_ID"
			+ " where 1 = 1";
	
	private static final String GET_ALL_SQL = BASE_SQL
			+ " order by a.CODE";

	private static final String FIND_BY_CODE_SQL = BASE_SQL
			+ " and a.CODE = ?";

	private static final String FIND_BY_ID_SQL = BASE_SQL
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
			product.setMaximumStockLevel(rs.getInt("MAX_STOCK_LEVEL"));
			product.setMinimumStockLevel(rs.getInt("MIN_STOCK_LEVEL"));
			product.setActive("Y".equals(rs.getString("ACTIVE_IND")));
			
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
			
			if (rs.getLong("MANUFACTURER_ID") != 0) {
				Manufacturer manufacturer = new Manufacturer();
				manufacturer.setId(rs.getLong("MANUFACTURER_ID"));
				manufacturer.setName(rs.getString("MANUFACTURER_NAME"));
				product.setManufacturer(manufacturer);
			}
			
			if (rs.getLong("CATEGORY_ID") != 0) {
				ProductCategory category = new ProductCategory();
				category.setId(rs.getLong("CATEGORY_ID"));
				category.setName(rs.getString("CATEGORY_NAME"));
				product.setCategory(category);
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
			"update PRODUCT set CODE = ?, DESCRIPTION = ?,"
			+ " MAX_STOCK_LEVEL = ?, MIN_STOCK_LEVEL = ?, ACTIVE_IND = ?,"
			+ " UNIT_IND_CSE = ?, AVAIL_QTY_CSE = ?,"
			+ " UNIT_IND_CTN = ?, AVAIL_QTY_CTN = ?,"
			+ " UNIT_IND_DOZ = ?, AVAIL_QTY_DOZ = ?,"
			+ " UNIT_IND_PCS = ?, AVAIL_QTY_PCS = ?,"
			+ " MANUFACTURER_ID = ?, CATEGORY_ID = ? where ID = ?";
	
	private void update(Product product) {
		getJdbcTemplate().update(UPDATE_SQL, 
				product.getCode(), 
				product.getDescription(),
				product.getMaximumStockLevel(),
				product.getMinimumStockLevel(),
				product.isActive() ? "Y" : "N",
				product.hasUnit(Unit.CASE) ? "Y" : "N",
				product.getUnitQuantity(Unit.CASE),
				product.hasUnit(Unit.CARTON) ? "Y" : "N",
				product.getUnitQuantity(Unit.CARTON),
				product.hasUnit(Unit.DOZEN) ? "Y" : "N",
				product.getUnitQuantity(Unit.DOZEN),
				product.hasUnit(Unit.PIECES) ? "Y" : "N",
				product.getUnitQuantity(Unit.PIECES),
				product.getManufacturer() != null ? product.getManufacturer().getId() : null,
				product.getCategory() != null ? product.getCategory().getId() : null,
				product.getId());
	}

	private static final String INSERT_SQL =
			"insert into PRODUCT (CODE, DESCRIPTION, MAX_STOCK_LEVEL, MIN_STOCK_LEVEL, ACTIVE_IND,"
			+ " UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS, "
			+ " MANUFACTURER_ID, CATEGORY_ID)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final Product product) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, product.getCode());
				ps.setString(2, product.getDescription());
				ps.setInt(3, product.getMaximumStockLevel());
				ps.setInt(4, product.getMinimumStockLevel());
				ps.setString(5, product.isActive() ? "Y" : "N");
				ps.setString(6, product.hasUnit(Unit.CASE) ? "Y" : "N");
				ps.setString(7, product.hasUnit(Unit.CARTON) ? "Y" : "N");
				ps.setString(8, product.hasUnit(Unit.DOZEN) ? "Y" : "N");
				ps.setString(9, product.hasUnit(Unit.PIECES) ? "Y" : "N");
				ps.setInt(10, product.getUnitQuantity(Unit.CASE));
				ps.setInt(11, product.getUnitQuantity(Unit.CARTON));
				ps.setInt(12, product.getUnitQuantity(Unit.DOZEN));
				ps.setInt(13, product.getUnitQuantity(Unit.PIECES));
				if (product.getManufacturer() != null) {
					ps.setLong(14, product.getManufacturer().getId());
				} else {
					ps.setNull(14, Types.NUMERIC);
				}
				if (product.getCategory() != null) {
					ps.setLong(15, product.getCategory().getId());
				} else {
					ps.setNull(15, Types.NUMERIC);
				}
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		product.setId(holder.getKey().longValue());
	}

	private static final String FIND_FIRST_WITH_CODE_LIKE_SQL = BASE_SQL
			+ " and CODE like ? limit 1";
			
	
	@Override
	public Product findFirstWithCodeLike(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_WITH_CODE_LIKE_SQL, productRowMapper, code + "%");
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Product> search(Product criteria) {
		StringBuilder sql = new StringBuilder(BASE_SQL);
		sql.append(" and ACTIVE_IND = ?");
		sql.append(" order by CODE"); // TODO: change to be more flexible when the need arises
		
		return getJdbcTemplate().query(sql.toString(), productRowMapper,
				criteria.isActive() ? "Y" : "N");
	}

}
