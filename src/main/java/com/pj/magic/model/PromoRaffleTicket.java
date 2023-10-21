package com.pj.magic.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromoRaffleTicket {

	private Long id;
	private Promo promo;
	private Integer ticketNumber;
	private Customer customer;
	
	// derived fields
	private Date claimDate;
	
	public String getTicketNumberDisplay() {
		return StringUtils.leftPad(String.valueOf(ticketNumber), 4, '0');
	}
	
}
