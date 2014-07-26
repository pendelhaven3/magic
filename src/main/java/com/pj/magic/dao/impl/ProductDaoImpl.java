package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;

@Repository
public class ProductDaoImpl extends MagicDao implements ProductDao {
	
	private static final String SELECT_ALL_PRODUCTS_SQL =
			"select ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS"
			+ " from PRODUCT a, PRODUCT_PRICE b"
			+ " where a.ID = b.PRODUCT_ID";

	private static final String SELECT_PRODUCT_BY_CODE_SQL =
			"select ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS"
			+ " from PRODUCT a, PRODUCT_PRICE b"
			+ " where a.ID = b.PRODUCT_ID"
			+ " and a.CODE = ?";

	private static final String SELECT_PRODUCT_BY_ID_SQL =
			"select ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " AVAIL_QTY_CSE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS"
			+ " from PRODUCT a, PRODUCT_PRICE b"
			+ " where a.ID = b.PRODUCT_ID"
			+ " and a.ID = ?";

	private ProductRowMapper productRowMapper = new ProductRowMapper();
	
	@Override
	public List<Product> getAllProducts() {
		return getJdbcTemplate().query(SELECT_ALL_PRODUCTS_SQL, productRowMapper);
	}

	@Override
	public Product findProductByCode(String code) {
		try {
			return getJdbcTemplate().queryForObject(SELECT_PRODUCT_BY_CODE_SQL, 
					new Object[] {code}, productRowMapper);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public Product getProduct(int id) {
		return getJdbcTemplate().queryForObject(SELECT_PRODUCT_BY_ID_SQL, 
				new Object[] {id}, productRowMapper);
	}

	private class ProductRowMapper implements RowMapper<Product> {

		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setId(rs.getInt("ID"));
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
			if ("Y".equals(rs.getString("UNIT_IND_CSE"))) {
				product.getUnits().add("PCS");
				product.getUnitPrices().add(new UnitPrice("PCS", rs.getBigDecimal("UNIT_PRICE_PCS")));
				product.getUnitQuantities().add(new UnitQuantity("PCS", rs.getInt("AVAIL_QTY_PCS")));
			}
			return product;
		}
		
	}

}
