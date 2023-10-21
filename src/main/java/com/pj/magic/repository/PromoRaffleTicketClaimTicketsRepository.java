package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.PromoRaffleTicketClaim;

public interface PromoRaffleTicketClaimTicketsRepository {

	void save(PromoRaffleTicketClaim claim, PromoRaffleTicket ticket);

	List<PromoRaffleTicket> findAllByClaim(Long claimId);
	
}
