package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Promo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesInvoiceSearchCriteria {

	private Boolean marked;
	private Boolean cancelled;
	private Long salesInvoiceNumber;
	private Customer customer;
	private Date transactionDate;
	private Date transactionDateFrom;
	private Date transactionDateTo;
	private Boolean paid;
	private String orderBy;
	private Promo unredeemedPromo;
	private PricingScheme pricingScheme;
	private Promo unclaimedRafflePromo;

	public Boolean isMarked() {
		return marked;
	}

	public Boolean isCancelled() {
		return cancelled;
	}
	
	
	
}