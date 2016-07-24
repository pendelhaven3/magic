package com.pj.magic.repository.impl;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.repository.DailyProductStartingQuantityRepository;

@Repository
public class DailyProductStartingQuantityRepositoryImpl extends MagicDao implements DailyProductStartingQuantityRepository {

	private static final String GET_CHECK_VALUE_SQL =
			"select COUNT from DAILY_PRODUCT_STARTING_QUANTITY_CHECK where DATE = current_date()";
	
	@Override
	public int getCheckValueForToday() {
		return getJdbcTemplate().queryForObject(GET_CHECK_VALUE_SQL, Integer.class);
	}

	private static final String SAVE_CHECK_VALUE_SQL =
			"insert into DAILY_PRODUCT_STARTING_QUANTITY_CHECK"
			+ " (DATE, COUNT)"
			+ " select current_date(), count(*)"
			+ " from PRODUCT"
			+ " where ACTIVE_IND = 'Y'";
	
	@Override
	public void saveCheckValueForToday() {
		getJdbcTemplate().update(SAVE_CHECK_VALUE_SQL);
	}

	private static final String GET_PRODUCT_COUNT_FOR_TODAY_SQL =
			"select count(*)"
			+ " from DAILY_PRODUCT_STARTING_QUANTITY_CHECK"
			+ " where DATE = current_date()";
	
	@Override
	public int getProductCountForToday() {
		return getJdbcTemplate().queryForObject(GET_PRODUCT_COUNT_FOR_TODAY_SQL, Integer.class);
	}

	private static final String SAVE_QUANTITIES_FOR_TODAY_SQL =
			"insert into DAILY_PRODUCT_STARTING_QUANTITY"
			+ " (DATE, PRODUCT_ID, AVAIL_QTY_CSE, AVAIL_QTY_TIE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS)"
			+ " select current_date(), ID, AVAIL_QTY_CSE, AVAIL_QTY_TIE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS"
			+ " from PRODUCT"
			+ " where ACTIVE_IND = 'Y'";
	
	@Override
	public void saveQuantitiesForToday() {
		getJdbcTemplate().update(SAVE_QUANTITIES_FOR_TODAY_SQL);
	}

	private static final String DELETE_QUANTITIES_FOR_TODAY_SQL =
			"delete from DAILY_PRODUCT_STARTING_QUANTITY where DATE = current_date()";
	
	@Override
	public void deleteQuantitiesForToday() {
		getJdbcTemplate().update(DELETE_QUANTITIES_FOR_TODAY_SQL);
	}

}
