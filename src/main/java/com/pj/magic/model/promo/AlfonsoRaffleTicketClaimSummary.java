package com.pj.magic.model.promo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pj.magic.model.Product;
import com.pj.magic.model.PromoRaffleTicketClaimSummary;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.service.impl.PromoServiceImpl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlfonsoRaffleTicketClaimSummary extends PromoRaffleTicketClaimSummary {

	private List<String> participatingProductCodes = new ArrayList<>();
	
	public void setParticipatingItems(List<Product> products) {
		participatingProductCodes = products.stream().map(product -> product.getCode()).collect(Collectors.toList());
	}
	
	
	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			for (SalesInvoiceItem item : salesInvoice.getItems()) {
				if (participatingProductCodes.contains(item.getProduct().getCode())) {
					totalAmount = totalAmount.add(item.getNetAmount());
				}
			}
		}
		return totalAmount;
	}
	
	public int getNumberOfTickets() {
		return getTotalAmount().divideToIntegralValue(PromoServiceImpl.ALFONSO_RAFFLE_SALES_AMOUNT_PER_TICKET).intValue();
	}

	public static List<PromoRaffleTicketClaimSummary> toSummaries(List<SalesInvoice> salesInvoices, List<Product> participatingItems) {
		salesInvoices.sort((o1, o2) -> {
			int result = o1.getTransactionDate().compareTo(o2.getTransactionDate());
			if (result != 0) {
				return result;
			} else {
				return o1.getSalesInvoiceNumber().compareTo(o2.getSalesInvoiceNumber());
			}
		});
		
		List<PromoRaffleTicketClaimSummary> summaries = new ArrayList<>();
		
		AlfonsoRaffleTicketClaimSummary summary = null;
		for (SalesInvoice salesInvoice : salesInvoices) {
			if (summary == null || !summary.getTransactionDate().equals(salesInvoice.getTransactionDate())) {
				summary = new AlfonsoRaffleTicketClaimSummary();
				summary.setTransactionDate(salesInvoice.getTransactionDate());
				summary.setParticipatingItems(participatingItems);
				summaries.add(summary);
			}
			summary.getSalesInvoices().add(salesInvoice);
		}
		
		return summaries;
	}
	
}
