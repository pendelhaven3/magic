package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PromoRedemptionSearchCriteria;

@Repository
public class PromoRedemptionDaoImpl extends MagicDao implements PromoRedemptionDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_ID, PROMO_REDEMPTION_NO, CUSTOMER_ID,"
			+ " POST_IND, POST_DT, POST_BY,"
			+ " b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME,"
			+ " d.NAME as PROMO_NAME"
			+ " from PROMO_REDEMPTION a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_id"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY"
			+ " join PROMO d"
			+ "   on d.ID = a.PROMO_ID";
	
	private PromoRedemptionRowMapper promoRedemptionRowMapper = new PromoRedemptionRowMapper();
	
	@Override
	public void save(PromoRedemption promoRedemption) {
		if (promoRedemption.getId() == null) {
			insert(promoRedemption);
		} else {
			update(promoRedemption);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_REDEMPTION (PROMO_ID, PROMO_REDEMPTION_NO, CUSTOMER_ID) values (?, ?, ?)";
	
	private void insert(final PromoRedemption promoRedemption) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, promoRedemption.getPromo().getId());
				ps.setLong(2, getNextPromoRedemptionNumber(promoRedemption.getPromo()));
				ps.setLong(3, promoRedemption.getCustomer().getId());
				return ps;
			}
		}, holder);
		
		PromoRedemption updated = get(holder.getKey().longValue());
		promoRedemption.setId(updated.getId());
		promoRedemption.setPromoRedemptionNumber(updated.getPromoRedemptionNumber());
	}

	private static final String GET_SEQUENCE_NEXT_VALUE_SQL = 
			"select VALUE + 1 from PROMO_REDEMPTION_SEQUENCE where PROMO_ID = ? for update";
	
	private static final String UPDATE_SEQUENCE_VALUE_SQL =
			"update PROMO_REDEMPTION_SEQUENCE set VALUE = ? where PROMO_ID = ?";
	
	private long getNextPromoRedemptionNumber(Promo promo) {
		Long value = getJdbcTemplate().queryForObject(GET_SEQUENCE_NEXT_VALUE_SQL, Long.class, promo.getId());
		getJdbcTemplate().update(UPDATE_SEQUENCE_VALUE_SQL, value, promo.getId());
		return value;
	}
	
	private static final String UPDATE_SQL = "update PROMO_REDEMPTION"
			+ " set CUSTOMER_ID = ?, POST_IND = ?, POST_DT = ?,"
			+ " POST_BY = ? where ID = ?";
	
	private void update(PromoRedemption promoRedemption) {
		getJdbcTemplate().update(UPDATE_SQL,
				promoRedemption.getCustomer().getId(),
				promoRedemption.isPosted() ? "Y" : "N",
				promoRedemption.isPosted() ? promoRedemption.getPostDate() : null,
				promoRedemption.isPosted() ? promoRedemption.getPostedBy().getId() : null,
				promoRedemption.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL
			+ " where a.ID = ?";
	
	@Override
	public PromoRedemption get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, promoRedemptionRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class PromoRedemptionRowMapper implements RowMapper<PromoRedemption> {

		@Override
		public PromoRedemption mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoRedemption promoRedemption = new PromoRedemption();
			promoRedemption.setId(rs.getLong("ID"));
			promoRedemption.setPromoRedemptionNumber(rs.getLong("PROMO_REDEMPTION_NO"));
			
			Promo promo = new Promo();
			promo.setId(rs.getLong("PROMO_ID"));
			promo.setName(rs.getString("PROMO_NAME"));
			promoRedemption.setPromo(promo);
			
			Customer customer = new Customer();
			customer.setId(rs.getLong("CUSTOMER_ID"));
			customer.setCode(rs.getString("CUSTOMER_CODE"));
			customer.setName(rs.getString("CUSTOMER_NAME"));
			promoRedemption.setCustomer(customer);
			
			promoRedemption.setPosted("Y".equals(rs.getString("POST_IND")));
			if (promoRedemption.isPosted()) {
				promoRedemption.setPostDate(rs.getDate("POST_DT"));
				promoRedemption.setPostedBy(
						new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			return promoRedemption;
		}

	}

	private static final String FIND_ALL_BY_PROMO_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_ID = ? order by PROMO_REDEMPTION_NO desc";
	
	@Override
	public List<PromoRedemption> findAllByPromo(Promo promo) {
		return getJdbcTemplate().query(FIND_ALL_BY_PROMO_SQL, promoRedemptionRowMapper, promo.getId());
	}

	private static final String INSERT_NEW_PROMO_REDEMPTION_SEQUENCE_SQL =
			"insert PROMO_REDEMPTION_SEQUENCE (PROMO_ID, VALUE) values (?, 0)";
	
	@Override
	public void insertNewPromoRedemptionSequence(Promo promo) {
		getJdbcTemplate().update(INSERT_NEW_PROMO_REDEMPTION_SEQUENCE_SQL, promo.getId());
	}

	private static final String FIND_ALL_BY_SALES_INVOICE_SQL = BASE_SELECT_SQL
			+ " where exists ("
			+ "   select 1"
			+ "   from PROMO_REDEMPTION_SALES_INVOICE prsi"
			+ "   where prsi.PROMO_REDEMPTION_ID = a.ID"
			+ "   and prsi.SALES_INVOICE_ID = ?"
			+ " )"
			+ " and a.POST_IND = 'Y'";
	
	@Override
	public List<PromoRedemption> findAllBySalesInvoice(SalesInvoice salesInvoice) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_INVOICE_SQL, promoRedemptionRowMapper,
				salesInvoice.getId());	
	}

	private static final String SALES_INVOICE_WHERE_CLAUSE_SQL =
			"   and exists ("
			+ "   select 1"
			+ "   from PROMO_REDEMPTION_SALES_INVOICE prsi"
			+ "   where prsi.PROMO_REDEMPTION_ID = a.ID"
			+ "   and prsi.SALES_INVOICE_ID = ?"
			+ " )";
	
	@Override
	public List<PromoRedemption> search(PromoRedemptionSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPosted() != null) {
			sql.append(" and POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getSalesInvoice() != null) {
			sql.append(SALES_INVOICE_WHERE_CLAUSE_SQL);
			params.add(criteria.getSalesInvoice().getId());
		}
		
		if (criteria.getPromoType() != null) {
			sql.append(" and d.PROMO_TYPE_ID = ?");
			params.add(criteria.getPromoType().getId());
		}
		
		return getJdbcTemplate().query(sql.toString(), promoRedemptionRowMapper, params.toArray());
	}
	
}