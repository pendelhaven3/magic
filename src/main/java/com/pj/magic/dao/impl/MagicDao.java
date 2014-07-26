package com.pj.magic.dao.impl;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MagicDao extends JdbcDaoSupport {

	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void initialize() {
		setDataSource(dataSource);
	}
	
}
