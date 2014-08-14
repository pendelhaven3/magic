package com.pj.magic;

import javax.sql.DataSource;

import org.springframework.orm.jpa.JpaTransactionManager;

public class MagicTransactionManager extends JpaTransactionManager {

	@Override
	public DataSource getDataSource() {
		MagicDataSource ds = (MagicDataSource)super.getDataSource();
		return ds.getActualDataSource();
	}
	
}
