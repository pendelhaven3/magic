package com.pj.magic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductPriceDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;

@Repository
public class ProductPriceDaoImpl extends MagicDao implements ProductPriceDao {

	@Override
	public void save(Product product) {
		insert(product);
	}

	private static final String INSERT_SQL =
			"insert into PRODUCT_PRICE (PRODUCT_ID, UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN,"
			+ " UNIT_PRICE_DOZ, UNIT_PRICE_PCS) values (?, ?, ?, ?, ?, ?)";
	
	private void insert(final Product product) {
		getJdbcTemplate().update(INSERT_SQL, product.getId(),
				product.getUnitPrice(Unit.CASE), 
				product.getUnitPrice(Unit.TIE), product.getUnitPrice(Unit.CARTON),
				product.getUnitPrice(Unit.DOZEN), product.getUnitPrice(Unit.PIECES));
	}

	private static final String UPDATE_UNIT_PRICES_SQL =
			"update PRODUCT_PRICE"
			+ " set UNIT_PRICE_CSE = ?, UNIT_PRICE_TIE = ?, UNIT_PRICE_CTN = ?,"
			+ " UNIT_PRICE_DOZ = ?, UNIT_PRICE_PCS = ?"
			+ " where PRODUCT_ID = ? and PRICING_SCHEME_ID = ?";

	@Override
	public void updateUnitPrices(Product product, PricingScheme pricingScheme) {
		getJdbcTemplate().update(UPDATE_UNIT_PRICES_SQL,
				product.getUnitPrice(Unit.CASE),
				product.getUnitPrice(Unit.TIE),
				product.getUnitPrice(Unit.CARTON),
				product.getUnitPrice(Unit.DOZEN),
				product.getUnitPrice(Unit.PIECES),
				product.getId(),
				pricingScheme.getId()
		);
	}

}
