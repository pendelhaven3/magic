package com.pj.magic.util;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class ReportUtil {

	private static final String ALIGN_LEFT = "left";
	private static final String ALIGN_RIGHT = "right";
	
	public static String field(final String text, int length) {
		return field(text, length, ALIGN_LEFT);
	}
	
	public static String field(final String text, int length, String align) {
		String value = text;
		
		if (value == null) {
			value = "";
		}
		
		if (value.length() < length) {
			if (ALIGN_LEFT.equals(align)) {
				value = StringUtils.rightPad(value, length);
			} else if (ALIGN_RIGHT.equals(align)) {
				value = StringUtils.leftPad(value, length);
			}
		}
		return value;
	}
	
	public static String field(long number, int length, String align) {
		return field(String.valueOf(number), length, align);
	}
	
	public static String field(int number, int length) {
		return field(number, length, ALIGN_LEFT);
	}
	
	public static String field(int number, int length, String align) {
		return field(String.valueOf(number), length, align);
	}

	public static String field(BigDecimal number, int length, String align) {
		return field(FormatterUtil.formatAmount(number), length, align);
	}
	
}
