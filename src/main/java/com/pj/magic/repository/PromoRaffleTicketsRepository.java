package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.search.RaffleTicketSearchCriteria;

public interface PromoRaffleTicketsRepository {

	int getNextRaffleTicketNumber(long promoId);

	void save(PromoRaffleTicket ticket);

	List<PromoRaffleTicket> findAllByPromo(Promo promo);

	List<PromoRaffleTicket> search(RaffleTicketSearchCriteria criteria);
	
}
