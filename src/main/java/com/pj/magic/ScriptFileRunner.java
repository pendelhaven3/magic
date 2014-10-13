package com.pj.magic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import org.apache.ibatis.jdbc.ScriptRunner;

public class ScriptFileRunner {

	public static void runScriptFiles(Connection conn, String... filenames) throws IOException {
		for (String filename : filenames) {
			InputStream in = ScriptFileRunner.class.getClassLoader().getResourceAsStream("sql/" + filename);
			try (
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			) {
				ScriptRunner runner = new ScriptRunner(conn);
				runner.setLogWriter(null);
				runner.runScript(reader);
			}
		}
	}
	
}
