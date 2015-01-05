package com.pj.magic.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductPriceHistoryDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.User;

@Repository
public class ProductPriceHistoryDaoImpl extends MagicDao implements ProductPriceHistoryDao {

	private static final String INSERT_SQL =
			"insert into PRODUCT_PRICE_HISTORY"
			+ " (PRICING_SCHEME_ID, PRODUCT_ID, UPDATE_DT, UPDATE_BY,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS)"
			+ " values (?, ?, now(), ?, ?, ?, ?, ?, ?)";
	
	@Override
	public void save(ProductPriceHistory history) {
		getJdbcTemplate().update(INSERT_SQL,
				history.getPricingScheme().getId(),
				history.getProduct().getId(),
				history.getUpdatedBy().getId(),
				history.getUnitPrice(Unit.CASE),
				history.getUnitPrice(Unit.TIE),
				history.getUnitPrice(Unit.CARTON),
				history.getUnitPrice(Unit.DOZEN),
				history.getUnitPrice(Unit.PIECES)
		);
	}

	private static final String GET_ALL_SQL =
			"select PRICING_SCHEME_ID, PRODUCT_ID, UPDATE_DT, UPDATE_BY,"
			+ " UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
			+ " b.USERNAME as UPDATE_BY_USERNAME"
			+ " from PRODUCT_PRICE_HISTORY a"
			+ " join USER b"
			+ "   on b.ID = a.UPDATE_BY"
			+ " where PRICING_SCHEME_ID = ?"
			+ " and PRODUCT_ID = ?"
			+ " order by UPDATE_DT desc"; 
	
	@Override
	public List<ProductPriceHistory> getAll(PricingScheme pricingScheme, Product product) {
		return getJdbcTemplate().query(GET_ALL_SQL, new RowMapper<ProductPriceHistory>() {

			@Override
			public ProductPriceHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProductPriceHistory history = new ProductPriceHistory();
				history.setPricingScheme(new PricingScheme(rs.getLong("PRICING_SCHEME_ID")));
				history.setProduct(new Product(rs.getLong("PRODUCT_ID")));
				history.setUpdateDate(rs.getDate("UPDATE_DT"));
				history.setUpdatedBy(new User(rs.getLong("UPDATE_BY"), rs.getString("UPDATE_BY_USERNAME")));
				
				BigDecimal unitPriceCase = rs.getBigDecimal("UNIT_PRICE_CSE");
				if (unitPriceCase != null) {
					history.getUnitPrices().add(new UnitPrice(Unit.CASE, unitPriceCase));
				}
				
				BigDecimal unitPriceTie = rs.getBigDecimal("UNIT_PRICE_TIE");
				if (unitPriceTie != null) {
					history.getUnitPrices().add(new UnitPrice(Unit.TIE, unitPriceTie));
				}
				
				BigDecimal unitPriceCarton = rs.getBigDecimal("UNIT_PRICE_CTN");
				if (unitPriceCarton != null) {
					history.getUnitPrices().add(new UnitPrice(Unit.CARTON, unitPriceCarton));
				}

				BigDecimal unitPriceDozen = rs.getBigDecimal("UNIT_PRICE_DOZ");
				if (unitPriceDozen != null) {
					history.getUnitPrices().add(new UnitPrice(Unit.DOZEN, unitPriceDozen));
				}

				BigDecimal unitPricePieces = rs.getBigDecimal("UNIT_PRICE_PCS");
				if (unitPricePieces != null) {
					history.getUnitPrices().add(new UnitPrice(Unit.PIECES, unitPricePieces));
				}
				
				return history;
			}
			
		}, pricingScheme.getId(), product.getId());
	}

}