package com.pj.magic.util;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

public class NumberUtil {

	public static boolean isAmount(String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		
		try {
			new BigDecimal(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
}
