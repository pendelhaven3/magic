package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.service.impl.PromoServiceImpl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromoRaffleTicketClaimSummary {

	private Date transactionDate;
	protected List<SalesInvoice> salesInvoices = new ArrayList<>();

	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			totalAmount = totalAmount.add(salesInvoice.getTotalNetAmount());
		}
		return totalAmount;
	}
	
	public int getNumberOfTickets() {
		return getTotalAmount().divideToIntegralValue(PromoServiceImpl.JCHS_RAFFLE_SALES_AMOUNT_PER_TICKET).intValue();
	}
	
	public String getSalesInvoicesAsString() {
		StringBuilder sb = new StringBuilder();
		for (SalesInvoice salesInvoice : salesInvoices) {
			if (sb.length() > 0) {
				sb.append("\r\n");
			}
			sb.append(String.valueOf(salesInvoice.getSalesInvoiceNumber()));
		}
		return sb.toString();
	}

	public static List<PromoRaffleTicketClaimSummary> toSummaries(List<SalesInvoice> salesInvoices) {
		salesInvoices.sort((o1, o2) -> {
			int result = o1.getTransactionDate().compareTo(o2.getTransactionDate());
			if (result != 0) {
				return result;
			} else {
				return o1.getSalesInvoiceNumber().compareTo(o2.getSalesInvoiceNumber());
			}
		});
		
		List<PromoRaffleTicketClaimSummary> summaries = new ArrayList<>();
		
		PromoRaffleTicketClaimSummary summary = null;
		for (SalesInvoice salesInvoice : salesInvoices) {
			if (summary == null || !summary.getTransactionDate().equals(salesInvoice.getTransactionDate())) {
				summary = new PromoRaffleTicketClaimSummary();
				summary.setTransactionDate(salesInvoice.getTransactionDate());
				summaries.add(summary);
			}
			summary.getSalesInvoices().add(salesInvoice);
		}
		
		return summaries;
	}

}
