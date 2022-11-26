package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.gui.panels.EcashType;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.PaymentEcashPayment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcashPaymentsReport {

	private EcashReceiver ecashReceiver;
	private Date dateFrom;
	private Date dateTo;
	private EcashType ecashType;
	private List<PaymentEcashPayment> payments;
	
	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (PaymentEcashPayment payment : payments) {
			totalAmount = totalAmount.add(payment.getAmount());
		}
		return totalAmount.setScale(2);
	}
	
}
