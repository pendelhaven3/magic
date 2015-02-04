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

import com.pj.magic.dao.BadPurchaseReturnDao;
import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadPurchaseReturnSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class BadPurchaseReturnDaoImpl extends MagicDao implements BadPurchaseReturnDao {

	private static final String BAD_PURCHASE_RETURN_NUMBER_SEQUENCE = "BAD_PURCHASE_RETURN_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_PURCHASE_RETURN_NO, SUPPLIER_ID, POST_IND, POST_DT, POST_BY,"
			+ " a.REMARKS,"
			+ " b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME"
			+ " from BAD_PURCHASE_RETURN a"
			+ " join SUPPLIER b"
			+ "   on b.ID = a.SUPPLIER_ID"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY";

	private BadPurchaseReturnRowMapper badStockReturnRowMapper = new BadPurchaseReturnRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public BadPurchaseReturn get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, badStockReturnRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(BadPurchaseReturn badStockReturn) {
		if (badStockReturn.getId() == null) {
			insert(badStockReturn);
		} else {
			update(badStockReturn);
		}
	}

	private static final String UPDATE_SQL = 
			"update BAD_PURCHASE_RETURN set SUPPLIER_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " REMARKS = ? where ID = ?";
	
	private void update(BadPurchaseReturn badStockReturn) {
		getJdbcTemplate().update(UPDATE_SQL,
				badStockReturn.getSupplier().getId(),
				badStockReturn.isPosted() ? "Y" : "N",
				badStockReturn.getPostDate(),
				badStockReturn.isPosted() ? badStockReturn.getPostedBy().getId() : null,
				badStockReturn.getRemarks(),
				badStockReturn.getId());
	}

	private static final String INSERT_SQL =
			"insert into BAD_PURCHASE_RETURN (BAD_PURCHASE_RETURN_NO, SUPPLIER_ID) values (?, ?)";
	
	private void insert(final BadPurchaseReturn badStockReturn) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextBadPurchaseReturnNumber());
				ps.setLong(2, badStockReturn.getSupplier().getId());
				return ps;
			}
		}, holder);
		
		BadPurchaseReturn updated = get(holder.getKey().longValue());
		badStockReturn.setId(updated.getId());
		badStockReturn.setBadPurchaseReturnNumber(updated.getBadPurchaseReturnNumber());
	}
	
	private Long getNextBadPurchaseReturnNumber() {
		return getNextSequenceValue(BAD_PURCHASE_RETURN_NUMBER_SEQUENCE);
	}

	private class BadPurchaseReturnRowMapper implements RowMapper<BadPurchaseReturn> {

		@Override
		public BadPurchaseReturn mapRow(ResultSet rs, int rowNum) throws SQLException {
			BadPurchaseReturn badStockReturn = new BadPurchaseReturn();
			badStockReturn.setId(rs.getLong("ID"));
			badStockReturn.setBadPurchaseReturnNumber(rs.getLong("BAD_PURCHASE_RETURN_NO"));
			
			Supplier customer = new Supplier();
			customer.setId(rs.getLong("SUPPLIER_ID"));
			customer.setCode(rs.getString("SUPPLIER_CODE"));
			customer.setName(rs.getString("SUPPLIER_NAME"));
			badStockReturn.setSupplier(customer);
			
			badStockReturn.setPosted("Y".equals(rs.getString("POST_IND")));
			if (badStockReturn.isPosted()) {
				badStockReturn.setPostDate(rs.getDate("POST_DT"));
				badStockReturn.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			badStockReturn.setRemarks(rs.getString("REMARKS"));
			
			return badStockReturn;
		}
		
	}

	@Override
	public List<BadPurchaseReturn> search(BadPurchaseReturnSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getBadPurchaseReturnNumber() != null) {
			sql.append(" and a.BAD_PURCHASE_RETURN_NO = ?");
			params.add(criteria.getBadPurchaseReturnNumber());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and a.SUPPLIER_ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		sql.append(" order by BAD_PURCHASE_RETURN_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), badStockReturnRowMapper, params.toArray());
	}

	private static final String FIND_BY_BAD_PURCHASE_RETURN_NUMBER_SQL = BASE_SELECT_SQL +
			" where a.BAD_PURCHASE_RETURN_NO = ?";
	
	@Override
	public BadPurchaseReturn findByBadPurchaseReturnNumber(long badStockReturnNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_BAD_PURCHASE_RETURN_NUMBER_SQL,
					badStockReturnRowMapper, badStockReturnNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}