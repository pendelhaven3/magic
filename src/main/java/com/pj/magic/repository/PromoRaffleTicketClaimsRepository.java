package com.pj.magic.repository;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicketClaim;

public interface PromoRaffleTicketClaimsRepository {

	List<PromoRaffleTicketClaim> getAll(long promoId);

	void save(PromoRaffleTicketClaim claim);

	PromoRaffleTicketClaim get(Long id);

	PromoRaffleTicketClaim findByPromoAndCustomerAndTransactionDate(Promo promo, Customer customer, Date transactionDate);

	List<PromoRaffleTicketClaim> findAllByPromoAndCustomer(Promo promo, Customer customer);

}
