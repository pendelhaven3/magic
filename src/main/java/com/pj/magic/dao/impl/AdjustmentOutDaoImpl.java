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

import com.pj.magic.dao.AdjustmentOutDao;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.User;

@Repository
public class AdjustmentOutDaoImpl extends MagicDao implements AdjustmentOutDao {

	private static final String BASE_SELECT_SQL =
			"select ID, ADJUSTMENT_OUT_NO, REMARKS, POST_IND, POST_DT, POSTED_BY"
			+ " from ADJUSTMENT_OUT";
	
	private static final String ADJUSTMENT_OUT_NUMBER_SEQUENCE = "ADJUSTMENT_OUT_NO_SEQ";
	
	private AdjustmentOutRowMapper adjustmentOutRowMapper = new AdjustmentOutRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where ID = ?";
	
	@Override
	public AdjustmentOut get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, adjustmentOutRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(AdjustmentOut adjustmentOut) {
		if (adjustmentOut.getId() == null) {
			insert(adjustmentOut);
		} else {
			update(adjustmentOut);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into ADJUSTMENT_OUT"
			+ " (ADJUSTMENT_OUT_NO, REMARKS)"
			+ " values (?, ?)";
	
	private void insert(final AdjustmentOut adjustmentOut) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextAdjustmentOutNumber());
				ps.setString(2, adjustmentOut.getRemarks());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		AdjustmentOut updated = get(holder.getKey().longValue());
		adjustmentOut.setId(updated.getId());
		adjustmentOut.setAdjustmentOutNumber(updated.getAdjustmentOutNumber());
	}
	
	private long getNextAdjustmentOutNumber() {
		return getNextSequenceValue(ADJUSTMENT_OUT_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL =
			"update ADJUSTMENT_OUT"
			+ " set REMARKS = ?, POST_IND = ?, POST_DT = ?, POSTED_BY = ?"
			+ " where ID = ?";
	
	private void update(AdjustmentOut adjustmentOut) {
		getJdbcTemplate().update(UPDATE_SQL, adjustmentOut.getRemarks(), 
				adjustmentOut.isPosted() ? "Y" : "N",
				adjustmentOut.getPostDate(),
				adjustmentOut.isPosted() ? adjustmentOut.getPostedBy().getId() : null,
				adjustmentOut.getId());
	}
	
	private class AdjustmentOutRowMapper implements RowMapper<AdjustmentOut> {

		@Override
		public AdjustmentOut mapRow(ResultSet rs, int rowNum) throws SQLException {
			AdjustmentOut adjustmentOut = new AdjustmentOut();
			adjustmentOut.setId(rs.getLong("ID"));
			adjustmentOut.setAdjustmentOutNumber(rs.getLong("ADJUSTMENT_OUT_NO"));
			adjustmentOut.setRemarks(rs.getString("REMARKS"));
			adjustmentOut.setPosted("Y".equals(rs.getString("POST_IND")));
			adjustmentOut.setPostDate(rs.getDate("POST_DT"));
			if (rs.getLong("POSTED_BY") != 0) {
				adjustmentOut.setPostedBy(new User(rs.getLong("POSTED_BY")));
			}
			return adjustmentOut;
		}
	}

	private static final String DELETE_SQL = "delete ADJUSTMENT_OUT where ID = ?";
	
	@Override
	public void delete(AdjustmentOut adjustmentOut) {
		getJdbcTemplate().update(DELETE_SQL, adjustmentOut.getId());
	}

	@Override
	public List<AdjustmentOut> search(AdjustmentOut criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where POST_IND = ?");
		sql.append(" order by ID desc"); // TODO: change to be more flexible when the need arises
		
		return getJdbcTemplate().query(sql.toString(), adjustmentOutRowMapper,
				criteria.isPosted() ? "Y" : "N");
	}

}
