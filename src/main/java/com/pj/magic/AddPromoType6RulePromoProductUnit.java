package com.pj.magic;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddPromoType6RulePromoProductUnit {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String CHECK_SQL =
			"select count(*) from information_schema.columns"
			+ " where table_schema= 'magic' and table_name = 'PROMO_TYPE_6_RULE_PROMO_PRODUCT' and column_name = 'UNIT'";
	
	@PostConstruct
	public void onStartUp() {
		try {
			Integer result = jdbcTemplate.queryForObject(CHECK_SQL, Integer.class);
			if (result == 0) {
				jdbcTemplate.update("alter table PROMO_TYPE_6_RULE_PROMO_PRODUCT add column UNIT varchar(3) default 'CSE'");
				System.out.println("PROMO_TYPE_6_RULE_PROMO_PRODUCT.UNIT added");
			} else {
				System.out.println("PROMO_TYPE_6_RULE_PROMO_PRODUCT.UNIT already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
