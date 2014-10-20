package com.pj.magic;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupDatabase {

	private DataSource dataSource;
	
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("datasource-setup.xml");
		SetupDatabase setupDatabase = context.getBean(SetupDatabase.class);
		setupDatabase.run();
	}

	private void run() throws Exception {
		try (
			Connection conn = dataSource.getConnection();
		) {
			ScriptFileRunner.runScriptFiles(conn, "tables.sql", "initial_data.sql");
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
