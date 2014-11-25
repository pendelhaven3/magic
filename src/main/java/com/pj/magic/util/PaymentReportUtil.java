package com.pj.magic.util;

import java.math.BigDecimal;

import com.pj.magic.Constants;

public class PaymentReportUtil extends ReportUtil {

	public static String adjustmentAmount(BigDecimal adjustmentAmount, int length) {
		if (adjustmentAmount != null && !adjustmentAmount.equals(Constants.ZERO)) {
			return field(adjustmentAmount, length, ALIGN_RIGHT);
		} else {
			return field((String)null, length);
		}
	}
	
}
