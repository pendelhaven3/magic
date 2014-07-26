package com.pj.magic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap {

	@Autowired private DataSource dataSource;

	// TODO: Make method accept list of strings instead
	
	@PostConstruct
	public void initialize() throws Exception {
		runScriptFile("tables.sql");
		runScriptFile("data.sql");
	}
	
	private void runScriptFile(String filename) throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("sql/" + filename);
		try (
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			Connection conn = dataSource.getConnection();
		) {
			ScriptRunner runner = new ScriptRunner(conn);
			runner.setLogWriter(null);
			runner.runScript(reader);
		}
	}
	
}
