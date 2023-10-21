package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromoRaffleTicketClaim {

	private Long id;
	private Promo promo;
	private Customer customer;
	private Date transactionDate;
	private Date claimDate;
	private User processedBy;
	private int numberOfTickets;
	private List<PromoRaffleTicket> tickets;
	private List<SalesInvoice> salesInvoices;
	
	public boolean isNew() {
		return id != null;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = BigDecimal.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getTotalNetAmount());
		}
		return total;
	}
	
}
