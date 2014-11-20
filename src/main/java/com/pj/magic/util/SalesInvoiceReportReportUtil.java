package com.pj.magic.util;

import java.math.BigDecimal;

import com.pj.magic.Constants;

public class SalesInvoiceReportReportUtil extends ReportUtil {

	public static String discount(BigDecimal discount, int length) {
		if (!discount.equals(Constants.ZERO)) {
			return field(discount, length, ALIGN_RIGHT);
		} else {
			return field((String)null, length);
		}
	}
	
}
