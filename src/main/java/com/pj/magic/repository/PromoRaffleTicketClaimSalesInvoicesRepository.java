package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.SalesInvoice;

public interface PromoRaffleTicketClaimSalesInvoicesRepository {

	void save(PromoRaffleTicketClaim claim, SalesInvoice salesInvoice);

	List<SalesInvoice> findAllByClaim(Long claimId);
	
}
