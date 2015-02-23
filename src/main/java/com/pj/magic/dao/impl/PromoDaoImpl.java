package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoPrize;

@Repository
public class PromoDaoImpl extends MagicDao implements PromoDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_NO, a.NAME, TARGET_AMOUNT, a.MANUFACTURER_ID,"
			+ " PRODUCT_ID, b.DESCRIPTION as PRODUCT_DESCRIPTION, UNIT, QUANTITY"
			+ " from PROMO a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private PromoRowMapper promoRowMapper = new PromoRowMapper();
	
	@Override
	public List<Promo> getAll() {
		return getJdbcTemplate().query(BASE_SELECT_SQL, promoRowMapper);
	}

	private class PromoRowMapper implements RowMapper<Promo> {

		@Override
		public Promo mapRow(ResultSet rs, int rowNum) throws SQLException {
			Promo promo = new Promo();
			promo.setId(rs.getLong("ID"));
			promo.setPromoNumber(rs.getLong("PROMO_NO"));
			promo.setName(rs.getString("NAME"));
			promo.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
			promo.setManufacturer(new Manufacturer(rs.getLong("MANUFACTURER_ID")));
			promo.setPrize(mapPromoPrize(rs));
			return promo;
		}
		
		private PromoPrize mapPromoPrize(ResultSet rs) throws SQLException {
			PromoPrize prize = new PromoPrize();
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			prize.setProduct(product);
			
			prize.setUnit(rs.getString("UNIT"));
			prize.setQuantity(rs.getInt("QUANTITY"));
			
			return prize;
		}
		
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public Promo get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, promoRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}