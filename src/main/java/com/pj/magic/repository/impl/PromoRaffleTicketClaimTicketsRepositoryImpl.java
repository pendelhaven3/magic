package com.pj.magic.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.repository.PromoRaffleTicketClaimTicketsRepository;

@Repository
public class PromoRaffleTicketClaimTicketsRepositoryImpl extends MagicDao implements PromoRaffleTicketClaimTicketsRepository {

	private static final String BASE_SELECT_SQL =
			"select b.TICKET_NUMBER"
			+ " from PROMO_RAFFLE_TICKET_CLAIM_TICKETS a"
			+ " join PROMO_RAFFLE_TICKETS b"
			+ "	  on b.ID = a.TICKET_ID"
			+ " where 1 = 1";
	
	private RowMapper<PromoRaffleTicket> rowMapper = (rs, rowNum) -> {
		PromoRaffleTicket ticket = new PromoRaffleTicket();
		ticket.setTicketNumber(rs.getInt("TICKET_NUMBER"));
		return ticket;
	};
	
	private static final String FIND_ALL_BY_CLAIM_SQL = BASE_SELECT_SQL + " and a.CLAIM_ID = ? order by b.TICKET_NUMBER";
	
	@Override
	public List<PromoRaffleTicket> findAllByClaim(Long claimId) {
		return getJdbcTemplate().query(FIND_ALL_BY_CLAIM_SQL, rowMapper, claimId);
	}

	private static final String INSERT_SQL =
			"insert into PROMO_RAFFLE_TICKET_CLAIM_TICKETS (CLAIM_ID, TICKET_ID) values (?, ?)";
	
	@Override
	public void save(PromoRaffleTicketClaim claim, PromoRaffleTicket ticket) {
		getJdbcTemplate().update(INSERT_SQL, claim.getId(), ticket.getId());
	}

}
