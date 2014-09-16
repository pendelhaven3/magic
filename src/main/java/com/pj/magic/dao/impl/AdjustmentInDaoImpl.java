package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AdjustmentInDao;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.User;

@Repository
public class AdjustmentInDaoImpl extends MagicDao implements AdjustmentInDao {

	private static final String BASE_SELECT_SQL =
			"select ID, ADJUSTMENT_IN_NO, REMARKS, POST_IND, POST_DT, POSTED_BY"
			+ " from ADJUSTMENT_IN";
	
	private AdjustmentInRowMapper adjustmentInRowMapper = new AdjustmentInRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where ID = ?";
	
	@Override
	public AdjustmentIn get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, adjustmentInRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(AdjustmentIn adjustmentIn) {
		if (adjustmentIn.getId() == null) {
			insert(adjustmentIn);
		} else {
			update(adjustmentIn);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into ADJUSTMENT_IN"
			+ " (REMARKS)"
			+ " values (?)";
	
	private void insert(final AdjustmentIn adjustmentIn) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, adjustmentIn.getRemarks());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		AdjustmentIn updated = get(holder.getKey().longValue());
		adjustmentIn.setId(updated.getId());
		adjustmentIn.setAdjustmentInNumber(updated.getAdjustmentInNumber());
	}
	
	private static final String UPDATE_SQL =
			"update ADJUSTMENT_IN"
			+ " set REMARKS = ?, POST_IND = ?, POST_DT = ?, POSTED_BY = ?"
			+ " where ID = ?";
	
	private void update(AdjustmentIn adjustmentIn) {
		getJdbcTemplate().update(UPDATE_SQL, adjustmentIn.getRemarks(), 
				adjustmentIn.isPosted() ? "Y" : "N",
				adjustmentIn.getPostDate(),
				adjustmentIn.isPosted() ? adjustmentIn.getPostedBy().getId() : null,
				adjustmentIn.getId());
	}
	
	private class AdjustmentInRowMapper implements RowMapper<AdjustmentIn> {

		@Override
		public AdjustmentIn mapRow(ResultSet rs, int rowNum) throws SQLException {
			AdjustmentIn adjustmentIn = new AdjustmentIn();
			adjustmentIn.setId(rs.getLong("ID"));
			adjustmentIn.setAdjustmentInNumber(rs.getLong("ADJUSTMENT_IN_NO"));
			adjustmentIn.setRemarks(rs.getString("REMARKS"));
			adjustmentIn.setPosted("Y".equals(rs.getString("POST_IND")));
			adjustmentIn.setPostDate(rs.getDate("POST_DT"));
			if (rs.getLong("POSTED_BY") != 0) {
				adjustmentIn.setPostedBy(new User(rs.getLong("POSTED_BY")));
			}
			return adjustmentIn;
		}
	}

	private static final String DELETE_SQL = "delete ADJUSTMENT_IN where ID = ?";
	
	@Override
	public void delete(AdjustmentIn adjustmentIn) {
		getJdbcTemplate().update(DELETE_SQL, adjustmentIn.getId());
	}

	@Override
	public List<AdjustmentIn> search(AdjustmentIn criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where POST_IND = ?");
		sql.append(" order by ID desc"); // TODO: change to be more flexible when the need arises
		
		return getJdbcTemplate().query(sql.toString(), adjustmentInRowMapper,
				criteria.isPosted() ? "Y" : "N");
	}

}
