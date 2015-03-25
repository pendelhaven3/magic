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

import com.pj.magic.dao.PromoDao;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType;

@Repository
public class PromoDaoImpl extends MagicDao implements PromoDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_NO, a.NAME, PROMO_TYPE_ID, a.ACTIVE_IND, START_DT"
			+ " from PROMO a";
	
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
			promo.setActive("Y".equals(rs.getString("ACTIVE_IND")));
			promo.setStartDate(rs.getDate("START_DT"));
			return promo;
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
			+ " (PROMO_NO, NAME, PROMO_TYPE_ID, ACTIVE_IND, START_DT)"
			+ " values (?, ?, ?, ?, ?)";
	
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
				ps.setString(4, promo.isActive() ? "Y" : "N");
				ps.setDate(5, new java.sql.Date(promo.getStartDate().getTime()));
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
			"update PROMO set NAME = ?, ACTIVE_IND = ?, START_DT = ? where ID = ?";
	
	private void update(Promo promo) {
		getJdbcTemplate().update(UPDATE_SQL,
				promo.getName(),
				promo.isActive() ? "Y" : "N",
				promo.getStartDate(),
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