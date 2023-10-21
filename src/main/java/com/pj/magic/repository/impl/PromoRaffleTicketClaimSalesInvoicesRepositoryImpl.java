package com.pj.magic.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.repository.PromoRaffleTicketClaimSalesInvoicesRepository;

@Repository
public class PromoRaffleTicketClaimSalesInvoicesRepositoryImpl extends MagicDao
		implements PromoRaffleTicketClaimSalesInvoicesRepository {

	private static final String BASE_SELECT_SQL =
			"select a.SALES_INVOICE_ID"
			+ " from PROMO_RAFFLE_TICKET_CLAIM_SALES_INVOICES a"
			+ " where 1 = 1";
	
	private RowMapper<SalesInvoice> rowMapper = (rs, rowNum) -> {
		return new SalesInvoice(rs.getLong("SALES_INVOICE_ID"));
	};
	
	private static final String FIND_ALL_BY_CLAIM_SQL = BASE_SELECT_SQL + " and a.CLAIM_ID = ? order by a.SALES_INVOICE_ID";
	
	@Override
	public List<SalesInvoice> findAllByClaim(Long claimId) {
		return getJdbcTemplate().query(FIND_ALL_BY_CLAIM_SQL, rowMapper, claimId);
	}

	private static final String INSERT_SQL =
			"insert into PROMO_RAFFLE_TICKET_CLAIM_SALES_INVOICES (CLAIM_ID, SALES_INVOICE_ID) values (?, ?)";
	
	@Override
	public void save(PromoRaffleTicketClaim claim, SalesInvoice salesInvoice) {
		getJdbcTemplate().update(INSERT_SQL, claim.getId(), salesInvoice.getId());
	}

}
