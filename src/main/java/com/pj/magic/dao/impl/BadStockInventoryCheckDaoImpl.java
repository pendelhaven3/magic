package com.pj.magic.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockInventoryCheckDao;
import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.model.User;

@Repository
public class BadStockInventoryCheckDaoImpl extends MagicDao implements BadStockInventoryCheckDao {

	private static final String BAD_STOCK_INVENTORY_CHECK_NUMBER_SEQUENCE = "BAD_STOCK_INVENTORY_CHECK_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_STOCK_INVENTORY_CHECK_NO, REMARKS, POST_IND, POST_DT, POST_BY,"
			+ " b.USERNAME as POST_BY_USERNAME"
			+ " from BAD_STOCK_INVENTORY_CHECK a"
			+ " left join USER b"
			+ "   on b.ID = a.POST_BY";

	private RowMapper<BadStockInventoryCheck> mapper = (rs, rownum) -> {
		BadStockInventoryCheck inventoryCheck = new BadStockInventoryCheck();
		inventoryCheck.setId(rs.getLong("ID"));
		inventoryCheck.setBadStockInventoryCheckNumber(rs.getLong("BAD_STOCK_INVENTORY_CHECK_NO"));
		inventoryCheck.setRemarks(rs.getString("REMARKS"));
		inventoryCheck.setPosted("Y".equals(rs.getString("POST_IND")));
		inventoryCheck.setPostDate(rs.getTimestamp("POST_DT"));
		
		if (!StringUtils.isEmpty(rs.getString("POST_BY"))) {
			inventoryCheck.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
		}
		
		return inventoryCheck;
	};

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.ID desc";
	
	@Override
	public List<BadStockInventoryCheck> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, mapper);
	}
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public BadStockInventoryCheck get(Long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, mapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(BadStockInventoryCheck inventoryCheck) {
		if (inventoryCheck.getId() == null) {
			insert(inventoryCheck);
		} else {
			update(inventoryCheck);
		}
	}

	private static final String INSERT_SQL =
			"insert into BAD_STOCK_INVENTORY_CHECK (BAD_STOCK_INVENTORY_CHECK_NO, REMARKS) values (?, ?)";
	
	private void insert(BadStockInventoryCheck inventoryCheck) {
		Long badStockInventoryCheckNumber = getNextBadStockInventoryCheckNumber();
		
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(con -> {
			PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, badStockInventoryCheckNumber);
			ps.setString(2, inventoryCheck.getRemarks());
			return ps;
		}, holder);
		
		inventoryCheck.setId(holder.getKey().longValue());
		inventoryCheck.setBadStockInventoryCheckNumber(badStockInventoryCheckNumber);
	}
	
	private Long getNextBadStockInventoryCheckNumber() {
		return getNextSequenceValue(BAD_STOCK_INVENTORY_CHECK_NUMBER_SEQUENCE);
	}
	
	private static final String UPDATE_SQL = 
			"update BAD_STOCK_INVENTORY_CHECK set REMARKS = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?"
			+ " where ID = ?";
	
	private void update(BadStockInventoryCheck inventoryCheck) {
		getJdbcTemplate().update(UPDATE_SQL,
				inventoryCheck.getRemarks(),
				inventoryCheck.isPosted() ? "Y" : "N",
				inventoryCheck.getPostDate(),
				inventoryCheck.getPostedBy() != null ? inventoryCheck.getPostedBy().getId() : null,
				inventoryCheck.getId());
	}

}