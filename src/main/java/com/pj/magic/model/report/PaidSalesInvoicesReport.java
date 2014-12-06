package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.util.TimePeriod;

public class PaidSalesInvoicesReport {

	private List<PaymentSalesInvoice> paymentSalesInvoices;
	private Date paymentDate;
	private PaymentTerminal paymentTerminal;
	private TimePeriod timePeriod;

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
	
	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}
	
	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}
	
}