package com.pj.magic.util;

import java.math.BigDecimal;

public class ReceivingReceiptReportUtil extends ReportUtil {

	public static String discount(BigDecimal discount, int length) {
		String value = "";
		if (!discount.equals(BigDecimal.ZERO.setScale(2))) {
			value = FormatterUtil.formatAmount(discount) + " %";
		}
		return field(value, length, "right");
	}
	
	public static String flatRate(BigDecimal flatRate, int length) {
		String value = "";
		if (!flatRate.equals(BigDecimal.ZERO.setScale(2))) {
			value = FormatterUtil.formatAmount(flatRate);
		}
		return field(value, length, "right");
	}
	
}
