package com.pj.magic.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DbUtil {

	public static String toMySqlDateString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
}
