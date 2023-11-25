package com.pj.magic.model.search;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RaffleTicketSearchCriteria {

	private Promo promo;
	private Integer ticketNumber;
	private Customer customer;
	
}