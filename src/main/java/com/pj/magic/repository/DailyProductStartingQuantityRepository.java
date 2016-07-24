package com.pj.magic.repository;

public interface DailyProductStartingQuantityRepository {

	int getCheckValueForToday();

	void saveCheckValueForToday();

	int getProductCountForToday();

	void saveQuantitiesForToday();

	void deleteQuantitiesForToday();

}
