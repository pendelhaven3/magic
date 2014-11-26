package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PaymentSalesInvoice;

public class PaidSalesInvoicesReport {

	private List<PaymentSalesInvoice> paymentSalesInvoices;
	private Date paymentDate;

	public List<PaymentSalesInvoice> getPaymentSalesInvoices() {
		return paymentSalesInvoices;
	}

	public void setPaymentSalesInvoices(
			List<PaymentSalesInvoice> paymentSalesInvoices) {
		this.paymentSalesInvoices = paymentSalesInvoices;
	}
	
	public BigDecimal getTotalAmountDue() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice paymentSalesInvoice : paymentSalesInvoices) {
			total = total.add(paymentSalesInvoice.getAmountDue());
		}
		return total;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
}