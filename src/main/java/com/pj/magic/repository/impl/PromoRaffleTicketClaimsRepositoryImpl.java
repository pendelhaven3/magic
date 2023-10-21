package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.User;
import com.pj.magic.repository.PromoRaffleTicketClaimsRepository;
import com.pj.magic.util.DbUtil;

@Repository
public class PromoRaffleTicketClaimsRepositoryImpl extends MagicDao implements PromoRaffleTicketClaimsRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.TRANSACTION_DT, a.CLAIM_DT, a.NO_OF_TICKETS"
			+ " , a.CUSTOMER_ID, b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME"
			+ " , a.PROCESSED_BY, c.USERNAME as PROCESSED_BY_USERNAME"
			+ " from PROMO_RAFFLE_TICKET_CLAIMS a"
			+ " join CUSTOMER b"
			+ "	  on b.ID = a.CUSTOMER_ID"
			+ " join USER c"
			+ "   on c.ID = a.PROCESSED_BY"
			+ " where 1 = 1";

	private RowMapper<PromoRaffleTicketClaim> rowMapper = (rs, rowNum) -> {
		Customer customer = new Customer();
		customer.setId(rs.getLong("CUSTOMER_ID"));
		customer.setCode(rs.getString("CUSTOMER_CODE"));
		customer.setName(rs.getString("CUSTOMER_NAME"));
		
		PromoRaffleTicketClaim claim = new PromoRaffleTicketClaim();
		claim.setId(rs.getLong("ID"));
		claim.setCustomer(customer);
		claim.setTransactionDate(rs.getDate("TRANSACTION_DT"));
		claim.setClaimDate(rs.getTimestamp("CLAIM_DT"));
		claim.setProcessedBy(new User(rs.getLong("PROCESSED_BY"), rs.getString("PROCESSED_BY_USERNAME")));
		claim.setNumberOfTickets(rs.getInt("NO_OF_TICKETS"));
		return claim;
	};
	
	private static final String GET_ALL_CLAIMS_BY_PROMO_SQL = BASE_SELECT_SQL + " and a.PROMO_ID = ? order by a.CLAIM_DT desc";
	
	@Override
	public List<PromoRaffleTicketClaim> getAll(long promoId) {
		return getJdbcTemplate().query(GET_ALL_CLAIMS_BY_PROMO_SQL, rowMapper, promoId);
	}

	private static final String INSERT_SQL =
			"insert into PROMO_RAFFLE_TICKET_CLAIMS"
			+ " (PROMO_ID, CUSTOMER_ID, TRANSACTION_DT, CLAIM_DT, PROCESSED_BY, NO_OF_TICKETS)"
			+ " values (?, ?, ?, ?, ?, ?)";
	
	@Override
	public void save(PromoRaffleTicketClaim claim) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, claim.getPromo().getId());
				ps.setLong(2, claim.getCustomer().getId());
				ps.setDate(3, DbUtil.toSqlDate(claim.getTransactionDate()));
				ps.setTimestamp(4, new Timestamp(claim.getClaimDate().getTime()));
				ps.setLong(5, claim.getProcessedBy().getId());
				ps.setInt(6, claim.getNumberOfTickets());
				return ps;
			}
		}, holder);
		
		claim.setId(holder.getKey().longValue());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public PromoRaffleTicketClaim get(Long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_BY_PROMO_AND_CUSTOMER_AND_TRANSACTION_DATE_SQL = BASE_SELECT_SQL
			+ " and a.PROMO_ID = ? and a.CUSTOMER_ID = ? and a.TRANSACTION_DT = ?";
	
	@Override
	public PromoRaffleTicketClaim findByPromoAndCustomerAndTransactionDate(Promo promo, Customer customer, Date transactionDate) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PROMO_AND_CUSTOMER_AND_TRANSACTION_DATE_SQL,
					rowMapper, promo.getId(), customer.getId(), transactionDate);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}
