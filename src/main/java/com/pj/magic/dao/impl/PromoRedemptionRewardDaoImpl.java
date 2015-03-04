package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoRedemptionRewardDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;

@Repository
public class PromoRedemptionRewardDaoImpl extends MagicDao implements PromoRedemptionRewardDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_REDEMPTION_ID, PRODUCT_ID, UNIT, QUANTITY,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PROMO_REDEMPTION_REWARD a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private PromoRedemptionRewardRowMapper rewardRowMapper = new PromoRedemptionRewardRowMapper();
	
	private static final String INSERT_SQL =
			"insert into PROMO_REDEMPTION_REWARD"
			+ " (PROMO_REDEMPTION_ID, PRODUCT_ID, UNIT, QUANTITY) values (?, ?, ?, ?)";
	
	@Override
	public void save(final PromoRedemptionReward reward) {
		getJdbcTemplate().update(INSERT_SQL, 		
				reward.getParent().getId(),
				reward.getProduct().getId(),
				reward.getUnit(),
				reward.getQuantity());
	}

	private static final String FIND_ALL_BY_PROMO_REDEMPTION_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_REDEMPTION_ID = ?";
	
	@Override
	public List<PromoRedemptionReward> findAllByPromoRedemption(PromoRedemption promoRedemption) {
		List<PromoRedemptionReward> rewards = getJdbcTemplate().query(FIND_ALL_BY_PROMO_REDEMPTION_SQL, 
				rewardRowMapper, promoRedemption.getId());
		for (PromoRedemptionReward reward : rewards) {
			reward.setParent(promoRedemption);
		}
		return rewards;
	}

	private class PromoRedemptionRewardRowMapper implements RowMapper<PromoRedemptionReward> {

		@Override
		public PromoRedemptionReward mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoRedemptionReward reward = new PromoRedemptionReward();
			reward.setId(rs.getLong("ID"));
			reward.setParent(new PromoRedemption(rs.getLong("PROMO_REDEMPTION_ID")));
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			reward.setProduct(product);
			
			reward.setUnit(rs.getString("UNIT"));
			reward.setQuantity(rs.getInt("QUANTITY"));
			return reward;
		}
		
	}
	
}