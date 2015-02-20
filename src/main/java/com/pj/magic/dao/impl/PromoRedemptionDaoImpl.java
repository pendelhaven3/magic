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
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.User;

@Repository
public class PromoRedemptionDaoImpl extends MagicDao implements PromoRedemptionDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, PROMO_REDEMPTION_NO, CUSTOMER_ID,"
			+ " POST_IND, POST_DT, POST_BY,"
			+ " b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME,"
			+ " d.TARGET_AMOUNT, d.MANUFACTURER_ID"
			+ " from PROMO_REDEMPTION a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_id"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY"
			+ " join PROMO d"
			+ "   on d.ID = a.PROMO_ID";
	
	// TODO: Use separate sequence for each promo
	private static final String PROMO_REDEMPTION_NUMBER_SEQUENCE = "PROMO_REDEMPTION_NO_SEQ";
	
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
				ps.setLong(2, getNextPromoRedemptionNumber());
				ps.setLong(3, promoRedemption.getCustomer().getId());
				return ps;
			}
		}, holder);
		
		PromoRedemption updated = get(holder.getKey().longValue());
		promoRedemption.setId(updated.getId());
		promoRedemption.setPromoRedemptionNumber(updated.getPromoRedemptionNumber());
	}

	private long getNextPromoRedemptionNumber() {
		return getNextSequenceValue(PROMO_REDEMPTION_NUMBER_SEQUENCE);
	}

	private void update(PromoRedemption promoRedemption) {
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
			
			Promo promo = new Promo();
			promo.setId(rs.getLong("PROMO_ID"));
			promo.setTargetAmount(rs.getBigDecimal("TARGET_AMOUNT"));
			promo.setManufacturer(new Manufacturer(rs.getLong("MANUFACTURER_ID")));
			promoRedemption.setPromo(promo);
			
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

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.PROMO_REDEMPTION_NO desc";
	
	@Override
	public List<PromoRedemption> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, promoRedemptionRowMapper);
	}
	
}