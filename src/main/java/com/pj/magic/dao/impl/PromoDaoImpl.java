package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.pj.magic.dao.PromoDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Promo;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PromoDaoImpl extends MagicDao implements PromoDao {

	private static final String BASE_SELECT_SQL =
			"select ID, PROMO_NO, NAME, TARGET_AMOUNT, MANUFACTURER_ID"
			+ " from PROMO a";
	
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
			return promo;
		}
		
	}
	
}
