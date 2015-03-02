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

import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.User;

@Repository
public class PromoRedemptionDaoImpl extends MagicDao implements PromoRedemptionDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, PROMO_REDEMPTION_NO, CUSTOMER_ID, PRIZE_QUANTITY,"
			+ " POST_IND, POST_DT, POST_BY,"
			+ " b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME"
			+ " from PROMO_REDEMPTION a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_id"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY";
	
	private PromoRedemptionRowMapper promoRedemptionRowMapper = new PromoRedemptionRowMapper();
	
	@Override
	public void save(PromoRedemption promoRedemption) {
		if (promoRedemption.getId() == null) {
			insert(promoRedemption);
		} else {
			update(promoRedemption);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_REDEMPTION (PROMO_ID, PROMO_REDEMPTION_NO, CUSTOMER_ID) values (?, ?, ?)";
	
	private void insert(final PromoRedemption promoRedemption) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, promoRedemption.getPromo().getId());
				ps.setLong(2, getNextPromoRedemptionNumber(promoRedemption.getPromo()));
				ps.setLong(3, promoRedemption.getCustomer().getId());
				return ps;
			}
		}, holder);
		
		PromoRedemption updated = get(holder.getKey().longValue());
		promoRedemption.setId(updated.getId());
		promoRedemption.setPromoRedemptionNumber(updated.getPromoRedemptionNumber());
	}

	private static final String GET_SEQUENCE_NEXT_VALUE_SQL = 
			"select VALUE + 1 from PROMO_REDEMPTION_SEQUENCE where PROMO_ID = ? for update";
	
	private static final String UPDATE_SEQUENCE_VALUE_SQL =
			"update PROMO_REDEMPTION_SEQUENCE set VALUE = ? where PROMO_ID = ?";
	
	private long getNextPromoRedemptionNumber(Promo promo) {
		Long value = getJdbcTemplate().queryForObject(GET_SEQUENCE_NEXT_VALUE_SQL, Long.class, promo.getId());
		getJdbcTemplate().update(UPDATE_SEQUENCE_VALUE_SQL, value, promo.getId());
		return value;
	}
	
	private static final String UPDATE_SQL = "update PROMO_REDEMPTION"
			+ " set CUSTOMER_ID = ?, PRIZE_QUANTITY = ?, POST_IND = ?, POST_DT = ?,"
			+ " POST_BY = ? where ID = ?";
	
	private void update(PromoRedemption promoRedemption) {
		getJdbcTemplate().update(UPDATE_SQL,
				promoRedemption.getCustomer().getId(),
				promoRedemption.getPrizeQuantity(),
				promoRedemption.isPosted() ? "Y" : "N",
				promoRedemption.isPosted() ? promoRedemption.getPostDate() : null,
				promoRedemption.isPosted() ? promoRedemption.getPostedBy().getId() : null,
				promoRedemption.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL
			+ " where a.ID = ?";
	
	@Override
	public PromoRedemption get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, promoRedemptionRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class PromoRedemptionRowMapper implements RowMapper<PromoRedemption> {

		@Override
		public PromoRedemption mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoRedemption promoRedemption = new PromoRedemption();
			promoRedemption.setId(rs.getLong("ID"));
			promoRedemption.setPromoRedemptionNumber(rs.getLong("PROMO_REDEMPTION_NO"));
			promoRedemption.setPromo(new Promo(rs.getLong("PROMO_ID")));
			
			if (rs.getInt("PRIZE_QUANTITY") > 0) {
				promoRedemption.setPrizeQuantity(rs.getInt("PRIZE_QUANTITY"));
			}
			
			Customer customer = new Customer();
			customer.setId(rs.getLong("CUSTOMER_ID"));
			customer.setCode(rs.getString("CUSTOMER_CODE"));
			customer.setName(rs.getString("CUSTOMER_NAME"));
			promoRedemption.setCustomer(customer);
			
			promoRedemption.setPosted("Y".equals(rs.getString("POST_IND")));
			if (promoRedemption.isPosted()) {
				promoRedemption.setPostDate(rs.getDate("POST_DT"));
				promoRedemption.setPostedBy(
						new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			return promoRedemption;
		}

	}

	private static final String FIND_ALL_BY_PROMO_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_ID = ? order by PROMO_REDEMPTION_NO desc";
	
	@Override
	public List<PromoRedemption> findAllByPromo(Promo promo) {
		return getJdbcTemplate().query(FIND_ALL_BY_PROMO_SQL, promoRedemptionRowMapper, promo.getId());
	}

	private static final String INSERT_NEW_PROMO_REDEMPTION_SEQUENCE_SQL =
			"insert PROMO_REDEMPTION_SEQUENCE (PROMO_ID, VALUE) values (?, 0)";
	
	@Override
	public void insertNewPromoRedemptionSequence(Promo promo) {
		getJdbcTemplate().update(INSERT_NEW_PROMO_REDEMPTION_SEQUENCE_SQL, promo.getId());
	}
	
}