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

import com.pj.magic.dao.PromoDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoPrize;
import com.pj.magic.model.PromoType;

@Repository
public class PromoDaoImpl extends MagicDao implements PromoDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_NO, a.NAME, PROMO_TYPE_ID, TARGET_AMOUNT, a.MANUFACTURER_ID,"
			+ " PRODUCT_ID, b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION, UNIT, QUANTITY,"
			+ " a.ACTIVE_IND,"
			+ " c.NAME as MANUFACTURER_NAME"
			+ " from PROMO a"
			+ " left join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " left join MANUFACTURER c"
			+ "   on c.ID = a.MANUFACTURER_ID";
	
	private static final String PROMO_NUMBER_SEQUENCE = "PROMO_NO_SEQ";
	
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
			promo.setPromoType(PromoType.getPromoType(rs.getLong("PROMO_TYPE_ID")));
			promo.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
			promo.setActive("Y".equals(rs.getString("ACTIVE_IND")));
			
			if (rs.getLong("MANUFACTURER_ID") != 0) {
				Manufacturer manufacturer = new Manufacturer();
				manufacturer.setId(rs.getLong("MANUFACTURER_ID"));
				manufacturer.setName(rs.getString("MANUFACTURER_NAME"));
				promo.setManufacturer(manufacturer);
			}
			
			if (rs.getLong("PRODUCT_ID")!= 0) {
				promo.setPrize(mapPromoPrize(rs));
			}
			return promo;
		}
		
		private PromoPrize mapPromoPrize(ResultSet rs) throws SQLException {
			PromoPrize prize = new PromoPrize();
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
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

	@Override
	public void save(Promo promo) {
		if (promo.getId() == null) {
			insert(promo);
		} else {
			update(promo);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO"
			+ " (PROMO_NO, NAME, PROMO_TYPE_ID, MANUFACTURER_ID, TARGET_AMOUNT, PRODUCT_ID, UNIT, QUANTITY,"
			+ " ACTIVE_IND)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final Promo promo) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPromoNumber());
				ps.setString(2, promo.getName());
				ps.setLong(3, promo.getPromoType().getId());
				if (promo.getManufacturer() != null) {
					ps.setLong(4, promo.getManufacturer().getId());
				} else {
					ps.setNull(4, Types.INTEGER);
				}
				ps.setBigDecimal(5, promo.getTargetAmount());
				if (promo.getPrize() != null) {
					ps.setLong(6, promo.getPrize().getProduct().getId());
					ps.setString(7, promo.getPrize().getUnit());
					ps.setInt(8, promo.getPrize().getQuantity());
				} else {
					ps.setNull(6, Types.INTEGER);
					ps.setNull(7, Types.VARCHAR);
					ps.setNull(8, Types.INTEGER);
				}
				ps.setString(9, promo.isActive() ? "Y" : "N");
				return ps;
			}

		}, holder);
		
		Promo updated = get(holder.getKey().longValue());
		promo.setId(updated.getId());
		promo.setPromoNumber(updated.getPromoNumber());
	}
	
	private long getNextPromoNumber() {
		return getNextSequenceValue(PROMO_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL = 
			"update PROMO set NAME = ?, MANUFACTURER_ID = ?, TARGET_AMOUNT = ?, PRODUCT_ID = ?,"
			+ " UNIT = ?, QUANTITY = ?, ACTIVE_IND = ? where ID = ?";
	
	private void update(Promo promo) {
		getJdbcTemplate().update(UPDATE_SQL,
				promo.getName(),
				(promo.getManufacturer() != null) ? promo.getManufacturer().getId() : null,
				promo.getTargetAmount(),
				(promo.getPrize() != null) ? promo.getPrize().getProduct().getId() : null,
				(promo.getPrize() != null) ? promo.getPrize().getUnit() : null,
				(promo.getPrize() != null) ? promo.getPrize().getQuantity() : null,
				promo.isActive() ? "Y" : "N",
				promo.getId());
	}

	private static final String FIND_ALL_BY_ACTIVE_SQL = BASE_SELECT_SQL
			+ " where a.ACTIVE_IND = ?";
	
	@Override
	public List<Promo> findAllByActive(boolean active) {
		return getJdbcTemplate().query(FIND_ALL_BY_ACTIVE_SQL, promoRowMapper, 
				active ? "Y" : "N");
	}

}