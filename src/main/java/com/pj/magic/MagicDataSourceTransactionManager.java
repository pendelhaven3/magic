package com.pj.magic;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class MagicDataSourceTransactionManager extends DataSourceTransactionManager {

	@Override
	public DataSource getDataSource() {
		MagicDataSource ds = (MagicDataSource)super.getDataSource();
		return ds.getActualDataSource();
	}
	
}
