package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.Constants;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.util.ProductSearchCriteria;

@Repository
public class ProductDaoImpl extends MagicDao implements ProductDao {
	
	// TODO: Review base SQL for being tied with pricing scheme
	private static final String BASE_SELECT_SQL =
			"select a.ID, CODE, DESCRIPTION, MAX_STOCK_LEVEL, MIN_STOCK_LEVEL, ACTIVE_IND,"
			+ " UNIT_IND_CSE, UNIT_IND_TIE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_TIE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS,"
			+ " UNIT_CONV_CSE, UNIT_CONV_TIE, UNIT_CONV_CTN, UNIT_CONV_DOZ, UNIT_CONV_PCS,"
			+ " GROSS_COST_CSE, GROSS_COST_TIE, GROSS_COST_CTN, GROSS_COST_DOZ, GROSS_COST_PCS,"
			+ " FINAL_COST_CSE, FINAL_COST_TIE, FINAL_COST_CTN, FINAL_COST_DOZ, FINAL_COST_PCS,"
			+ " COMPANY_LIST_PRICE,"
			+ " MANUFACTURER_ID, c.NAME as MANUFACTURER_NAME,"
			+ " CATEGORY_ID, d.NAME as CATEGORY_NAME,"
			+ " SUBCATEGORY_ID, e.NAME as SUBCATEGORY_NAME"
			+ " from PRODUCT a"
			+ " join PRODUCT_PRICE b"
			+ " 	on b.PRODUCT_ID = a.ID"
			+ " left join MANUFACTURER c"
			+ "		on c.ID = a.MANUFACTURER_ID"
			+ " left join PRODUCT_CATEGORY d"
			+ "		on d.ID = a.CATEGORY_ID"
			+ " left join PRODUCT_SUBCATEGORY e"
			+ "		on e.ID = a.SUBCATEGORY_ID"
			+ " where 1 = 1";
	
	private ProductRowMapper productRowMapper = new ProductRowMapper();
	
	@Override
	public List<Product> getAll() {
		return findAllByPricingScheme(new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
	}

	@Override
	public Product findByCode(String code) {
		return findByCodeAndPricingScheme(code, new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
	}
	
	@Override
	public Product get(long id) {
		return getJdbcTemplate().queryForObject(
				FIND_BY_ID_AND_PRICING_SCHEME_SQL, productRowMapper, id, Constants.CANVASSER_PRICING_SCHEME_ID);
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
				product.getUnits().add(Unit.CASE);
				product.getUnitPrices().add(new UnitPrice(Unit.CASE, rs.getBigDecimal("UNIT_PRICE_CSE")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, rs.getInt("AVAIL_QTY_CSE")));
				product.getUnitConversions().add(new UnitConversion(Unit.CASE, rs.getInt("UNIT_CONV_CSE")));
				product.getUnitCosts().add(
						new UnitCost(Unit.CASE, rs.getBigDecimal("GROSS_COST_CSE"), rs.getBigDecimal("FINAL_COST_CSE")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_TIE"))) {
				product.getUnits().add(Unit.TIE);
				product.getUnitPrices().add(new UnitPrice(Unit.TIE, rs.getBigDecimal("UNIT_PRICE_TIE")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.TIE, rs.getInt("AVAIL_QTY_TIE")));
				product.getUnitConversions().add(new UnitConversion(Unit.TIE, rs.getInt("UNIT_CONV_TIE")));
				product.getUnitCosts().add(
						new UnitCost(Unit.TIE, rs.getBigDecimal("GROSS_COST_TIE"), rs.getBigDecimal("FINAL_COST_TIE")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_CTN"))) {
				product.getUnits().add(Unit.CARTON);
				product.getUnitPrices().add(new UnitPrice(Unit.CARTON, rs.getBigDecimal("UNIT_PRICE_CTN")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.CARTON, rs.getInt("AVAIL_QTY_CTN")));
				product.getUnitConversions().add(new UnitConversion(Unit.CARTON, rs.getInt("UNIT_CONV_CTN")));
				product.getUnitCosts().add(
						new UnitCost(Unit.CARTON, rs.getBigDecimal("GROSS_COST_CTN"), rs.getBigDecimal("FINAL_COST_CTN")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_DOZ"))) {
				product.getUnits().add(Unit.DOZEN);
				product.getUnitPrices().add(new UnitPrice(Unit.DOZEN, rs.getBigDecimal("UNIT_PRICE_DOZ")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.DOZEN, rs.getInt("AVAIL_QTY_DOZ")));
				product.getUnitConversions().add(new UnitConversion(Unit.DOZEN, rs.getInt("UNIT_CONV_DOZ")));
				product.getUnitCosts().add(
						new UnitCost(Unit.DOZEN, rs.getBigDecimal("GROSS_COST_DOZ"), rs.getBigDecimal("FINAL_COST_DOZ")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
				product.getUnits().add(Unit.PIECES);
				product.getUnitPrices().add(new UnitPrice(Unit.PIECES, rs.getBigDecimal("UNIT_PRICE_PCS")));
				product.getUnitQuantities().add(new UnitQuantity(Unit.PIECES, rs.getInt("AVAIL_QTY_PCS")));
				product.getUnitConversions().add(new UnitConversion(Unit.PIECES, rs.getInt("UNIT_CONV_PCS")));
				product.getUnitCosts().add(
						new UnitCost(Unit.PIECES, rs.getBigDecimal("GROSS_COST_PCS"), rs.getBigDecimal("FINAL_COST_PCS")));
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
			
			if (rs.getLong("SUBCATEGORY_ID") != 0) {
				ProductSubcategory subcategory = new ProductSubcategory();
				subcategory.setId(rs.getLong("SUBCATEGORY_ID"));
				subcategory.setName(rs.getString("SUBCATEGORY_NAME"));
				product.setSubcategory(subcategory);
			}
			
			product.setCompanyListPrice(rs.getBigDecimal("COMPANY_LIST_PRICE"));
			
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
			+ " UNIT_IND_CSE = ?, AVAIL_QTY_CSE = ?, UNIT_CONV_CSE = ?,"
			+ " UNIT_IND_TIE = ?, AVAIL_QTY_TIE = ?, UNIT_CONV_TIE = ?,"
			+ " UNIT_IND_CTN = ?, AVAIL_QTY_CTN = ?, UNIT_CONV_CTN = ?,"
			+ " UNIT_IND_DOZ = ?, AVAIL_QTY_DOZ = ?, UNIT_CONV_DOZ = ?,"
			+ " UNIT_IND_PCS = ?, AVAIL_QTY_PCS = ?, UNIT_CONV_PCS = ?,"
			+ " MANUFACTURER_ID = ?, CATEGORY_ID = ?, SUBCATEGORY_ID = ?,"
			+ " COMPANY_LIST_PRICE = ? where ID = ?";
	
	private void update(Product product) {
		getJdbcTemplate().update(UPDATE_SQL, 
				product.getCode(), 
				product.getDescription(),
				product.getMaximumStockLevel(),
				product.getMinimumStockLevel(),
				product.isActive() ? "Y" : "N",
				product.hasUnit(Unit.CASE) ? "Y" : "N",
				product.getUnitQuantity(Unit.CASE),
				product.getUnitConversion(Unit.CASE),
				product.hasUnit(Unit.TIE) ? "Y" : "N",
				product.getUnitQuantity(Unit.TIE),
				product.getUnitConversion(Unit.TIE),
				product.hasUnit(Unit.CARTON) ? "Y" : "N",
				product.getUnitQuantity(Unit.CARTON),
				product.getUnitConversion(Unit.CARTON),
				product.hasUnit(Unit.DOZEN) ? "Y" : "N",
				product.getUnitQuantity(Unit.DOZEN),
				product.getUnitConversion(Unit.DOZEN),
				product.hasUnit(Unit.PIECES) ? "Y" : "N",
				product.getUnitQuantity(Unit.PIECES),
				product.getUnitConversion(Unit.PIECES),
				product.getManufacturer() != null ? product.getManufacturer().getId() : null,
				product.getCategory() != null ? product.getCategory().getId() : null,
				product.getSubcategory() != null ? product.getSubcategory().getId() : null,
				product.getCompanyListPrice(),
				product.getId());
	}

	private static final String INSERT_SQL =
			"insert into PRODUCT (CODE, DESCRIPTION, MAX_STOCK_LEVEL, MIN_STOCK_LEVEL, ACTIVE_IND,"
			+ " UNIT_IND_CSE, UNIT_IND_TIE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_TIE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS,"
			+ " UNIT_CONV_CSE, UNIT_CONV_TIE, UNIT_CONV_CTN, UNIT_CONV_DOZ, UNIT_CONV_PCS,"
			+ " GROSS_COST_CSE, GROSS_COST_TIE, GROSS_COST_CTN, GROSS_COST_DOZ, GROSS_COST_PCS,"
			+ " FINAL_COST_CSE, FINAL_COST_TIE, FINAL_COST_CTN, FINAL_COST_DOZ, FINAL_COST_PCS,"
			+ " MANUFACTURER_ID, CATEGORY_ID, SUBCATEGORY_ID, COMPANY_LIST_PRICE)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
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
				ps.setString(7, product.hasUnit(Unit.TIE) ? "Y" : "N");
				ps.setString(8, product.hasUnit(Unit.CARTON) ? "Y" : "N");
				ps.setString(9, product.hasUnit(Unit.DOZEN) ? "Y" : "N");
				ps.setString(10, product.hasUnit(Unit.PIECES) ? "Y" : "N");
				ps.setInt(11, product.getUnitQuantity(Unit.CASE));
				ps.setInt(12, product.getUnitQuantity(Unit.TIE));
				ps.setInt(13, product.getUnitQuantity(Unit.CARTON));
				ps.setInt(14, product.getUnitQuantity(Unit.DOZEN));
				ps.setInt(15, product.getUnitQuantity(Unit.PIECES));
				ps.setInt(16, product.getUnitConversion(Unit.CASE));
				ps.setInt(17, product.getUnitConversion(Unit.TIE));
				ps.setInt(18, product.getUnitConversion(Unit.CARTON));
				ps.setInt(19, product.getUnitConversion(Unit.DOZEN));
				ps.setInt(20, product.getUnitConversion(Unit.PIECES));
				ps.setBigDecimal(21, product.getGrossCost(Unit.CASE));
				ps.setBigDecimal(22, product.getGrossCost(Unit.TIE));
				ps.setBigDecimal(23, product.getGrossCost(Unit.CARTON));
				ps.setBigDecimal(24, product.getGrossCost(Unit.DOZEN));
				ps.setBigDecimal(25, product.getGrossCost(Unit.PIECES));
				ps.setBigDecimal(26, product.getFinalCost(Unit.CASE));
				ps.setBigDecimal(27, product.getFinalCost(Unit.TIE));
				ps.setBigDecimal(28, product.getFinalCost(Unit.CARTON));
				ps.setBigDecimal(29, product.getFinalCost(Unit.DOZEN));
				ps.setBigDecimal(30, product.getFinalCost(Unit.PIECES));
				if (product.getManufacturer() != null) {
					ps.setLong(31, product.getManufacturer().getId());
				} else {
					ps.setNull(31, Types.NUMERIC);
				}
				if (product.getCategory() != null) {
					ps.setLong(32, product.getCategory().getId());
				} else {
					ps.setNull(32, Types.NUMERIC);
				}
				if (product.getSubcategory() != null) {
					ps.setLong(33, product.getSubcategory().getId());
				} else {
					ps.setNull(33, Types.NUMERIC);
				}
				ps.setBigDecimal(34, product.getCompanyListPrice());
				return ps;
			}
		}, holder);
		
		product.setId(holder.getKey().longValue());
	}

	@Override
	public List<Product> search(ProductSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getActive() != null) {
			sql.append(" and ACTIVE_IND = ?");
			params.add(criteria.getActive() ? "Y" : "N");
		}
		
		sql.append(" and b.PRICING_SCHEME_ID = ?");
		params.add(criteria.getPricingScheme().getId());
		
		if (criteria.getCode() != null) {
			sql.append(" and (CODE like ? or DESCRIPTION like ?)");
			params.add(criteria.getCode() + "%");
			params.add("%" + criteria.getCode() + "%");
		}
		
		if (criteria.getManufacturer() != null) {
			sql.append(" and MANUFACTURER_ID = ?");
			params.add(criteria.getManufacturer().getId());
		}
		
		if (criteria.getCategory() != null) {
			sql.append(" and CATEGORY_ID = ?");
			params.add(criteria.getCategory().getId());
		}
		
		if (criteria.getSubcategory() != null) {
			sql.append(" and SUBCATEGORY_ID = ?");
			params.add(criteria.getSubcategory().getId());
		}
		
		sql.append(" order by a.CODE");
		
		return getJdbcTemplate().query(sql.toString(), productRowMapper, params.toArray());
	}

	private static final String FIND_ALL_WITH_PRICING_SCHEME_SQL = BASE_SELECT_SQL +
			" and b.PRICING_SCHEME_ID = ? order by a.CODE";
	
	@Override
	public List<Product> findAllByPricingScheme(PricingScheme pricingScheme) {
		return getJdbcTemplate().query(FIND_ALL_WITH_PRICING_SCHEME_SQL, productRowMapper, pricingScheme.getId());
	}

	private static final String FIND_ALL_ACTIVE_BY_SUPPLIER_SQL = BASE_SELECT_SQL +
			" and a.ACTIVE_IND = 'Y'"
			+ " and exists(select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.ID and sp.SUPPLIER_ID = ?)"
			+ " and b.PRICING_SCHEME_ID = 1"
			+ " order by a.CODE";
			
	
	@Override
	public List<Product> findAllActiveBySupplier(Supplier supplier) {
		return getJdbcTemplate().query(FIND_ALL_ACTIVE_BY_SUPPLIER_SQL, productRowMapper, supplier.getId());
	}

	private static final String UPDATE_COSTS_SQL =
			"update PRODUCT"
			+ " set GROSS_COST_CSE = ?, GROSS_COST_TIE = ?, GROSS_COST_CTN = ?, "
			+ " GROSS_COST_DOZ = ?, GROSS_COST_PCS = ?, FINAL_COST_CSE = ?, "
			+ " FINAL_COST_TIE = ?, FINAL_COST_CTN = ?, FINAL_COST_DOZ = ?, "
			+ " FINAL_COST_PCS = ?"
			+ " where ID = ?"; 
	
	@Override
	public void updateCosts(Product product) {
		getJdbcTemplate().update(UPDATE_COSTS_SQL,
				product.getGrossCost(Unit.CASE),
				product.getGrossCost(Unit.TIE),
				product.getGrossCost(Unit.CARTON),
				product.getGrossCost(Unit.DOZEN),
				product.getGrossCost(Unit.PIECES),
				product.getFinalCost(Unit.CASE),
				product.getFinalCost(Unit.TIE),
				product.getFinalCost(Unit.CARTON),
				product.getFinalCost(Unit.DOZEN),
				product.getFinalCost(Unit.PIECES),
				product.getId());
	}

	private static final String FIND_BY_ID_AND_PRICING_SCHEME_SQL = BASE_SELECT_SQL
			+ " and a.ID = ? and b.PRICING_SCHEME_ID = ?";
	
	@Override
	public Product findByIdAndPricingScheme(long id, PricingScheme pricingScheme) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_ID_AND_PRICING_SCHEME_SQL,
					productRowMapper, id, pricingScheme.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_BY_CODE_AND_PRICING_SCHEME_SQL = BASE_SELECT_SQL
			+ " and a.CODE = ? and b.PRICING_SCHEME_ID = ?";
	
	@Override
	public Product findByCodeAndPricingScheme(String code, PricingScheme pricingScheme) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CODE_AND_PRICING_SCHEME_SQL,
					productRowMapper, code, pricingScheme.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}
