package com.pj.magic.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatterUtil {

	private static final String DATE_FORMAT = "MM/dd/yyyy";
	private static final String AMOUNT_FORMAT = "#,##0.00";
	
	public static final String formatDate(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
	
	public static final String formatAmount(BigDecimal amount) {
		return new DecimalFormat(AMOUNT_FORMAT).format(amount);
	}
	
}
