package com.pj.magic.util;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.springframework.util.StringUtils;

public class NumberUtil {

	private static final String AMOUNT_FORMAT = "#,##0.00";
	
	public static boolean isAmount(String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		
		try {
			new DecimalFormat(AMOUNT_FORMAT).parse(value);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
}
