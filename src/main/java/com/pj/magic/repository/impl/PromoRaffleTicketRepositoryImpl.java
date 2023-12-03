package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.search.RaffleTicketSearchCriteria;
import com.pj.magic.repository.PromoRaffleTicketsRepository;

@Repository
public class PromoRaffleTicketRepositoryImpl extends MagicDao implements PromoRaffleTicketsRepository {

	private static final String BASE_SELECT_SQL =
			"select a.TICKET_NUMBER, d.CLAIM_DT"
			+ " , a.CUSTOMER_ID, b.NAME as CUSTOMER_NAME"
			+ " from PROMO_RAFFLE_TICKETS a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_ID"
			+ " join PROMO_RAFFLE_TICKET_CLAIM_TICKETS c"
			+ "   on c.TICKET_ID = a.ID"
			+ " join PROMO_RAFFLE_TICKET_CLAIMS d"
			+ "   on d.ID = c.CLAIM_ID"
			+ " where 1 = 1";
	
	private RowMapper<PromoRaffleTicket> rowMapper = (rs, rowNum) -> {
		Customer customer = new Customer();
		customer.setName(rs.getString("CUSTOMER_NAME"));
		
		PromoRaffleTicket ticket = new PromoRaffleTicket();
		ticket.setTicketNumber(rs.getInt("TICKET_NUMBER"));
		ticket.setCustomer(customer);
		ticket.setClaimDate(rs.getTimestamp("CLAIM_DT"));
		return ticket;
	};
	
	private static final String GET_NEXT_RAFFLE_TICKET_NUMBER_SQL = 
			"select ifnull(max(TICKET_NUMBER), 0) + 1 from PROMO_RAFFLE_TICKETS where PROMO_ID = ?";
	
	@Override
	public int getNextRaffleTicketNumber(long promoId) {
		return getJdbcTemplate().queryForObject(GET_NEXT_RAFFLE_TICKET_NUMBER_SQL, Integer.class, promoId);
	}

	private static final String INSERT_SQL =
			"insert into PROMO_RAFFLE_TICKETS (PROMO_ID, TICKET_NUMBER, CUSTOMER_ID) values (?, ?, ?)";
	
	@Override
	public void save(final PromoRaffleTicket ticket) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, ticket.getPromo().getId());
				ps.setInt(2, ticket.getTicketNumber());
				ps.setLong(3, ticket.getCustomer().getId());
				return ps;
			}
		}, holder);
		
		ticket.setId(holder.getKey().longValue());
	}

	private static final String FIND_ALL_BY_PROMO_SQL = BASE_SELECT_SQL + " and a.PROMO_ID = ? order by a.TICKET_NUMBER desc";
	
	@Override
	public List<PromoRaffleTicket> findAllByPromo(Promo promo) {
		return getJdbcTemplate().query(FIND_ALL_BY_PROMO_SQL, rowMapper, promo.getId());
	}

	@Override
	public List<PromoRaffleTicket> search(RaffleTicketSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getPromo() != null) {
			sql.append(" and a.PROMO_ID = ?");
			params.add(criteria.getPromo().getId());
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and a.CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getTicketNumber() != null) {
			sql.append(" and a.TICKET_NUMBER = ?");
			params.add(criteria.getTicketNumber());
		}
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}

}
