package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicket;

public interface PromoRaffleTicketsRepository {

	int getNextRaffleTicketNumber(long promoId);

	void save(PromoRaffleTicket ticket);

	List<PromoRaffleTicket> findAllByPromo(Promo promo);
	
}
