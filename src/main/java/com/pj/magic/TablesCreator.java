package com.pj.magic;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TablesCreator {

	private DataSource dataSource;
	
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("datasource-create.xml");
		TablesCreator tablesCreator = context.getBean(TablesCreator.class);
		tablesCreator.run();
	}

	private void run() throws Exception {
		try (
			Connection conn = dataSource.getConnection();
		) {
			ScriptFileRunner.runScriptFiles(conn, "tables.sql");
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
