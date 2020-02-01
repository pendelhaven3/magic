package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockAdjustmentInDao;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadStockAdjustmentInSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class BadStockAdjustmentInDaoImpl extends MagicDao implements BadStockAdjustmentInDao {

    private static final String BASE_SELECT_SQL =
            "select a.ID, BAD_STOCK_ADJUSTMENT_IN_NO, REMARKS, PILFERAGE, POST_IND, POST_DT, POSTED_BY,"
            + " b.USERNAME as POSTED_BY_USERNAME"
            + " from BAD_STOCK_ADJUSTMENT_IN a"
            + " left join USER b"
            + "   on b.ID = a.POSTED_BY";
    
    private static final String BAD_STOCK_ADJUSTMENT_IN_NUMBER_SEQUENCE = "BAD_STOCK_ADJUSTMENT_IN_NO_SEQ";
    
    private RowMapper<BadStockAdjustmentIn> rowMapper = new RowMapper<BadStockAdjustmentIn>() {

        @Override
        public BadStockAdjustmentIn mapRow(ResultSet rs, int rowNum) throws SQLException {
            BadStockAdjustmentIn adjustmentIn = new BadStockAdjustmentIn();
            adjustmentIn.setId(rs.getLong("ID"));
            adjustmentIn.setBadStockAdjustmentInNumber(rs.getLong("BAD_STOCK_ADJUSTMENT_IN_NO"));
            adjustmentIn.setRemarks(rs.getString("REMARKS"));
            adjustmentIn.setPilferage(rs.getBoolean("PILFERAGE"));
            adjustmentIn.setPosted("Y".equals(rs.getString("POST_IND")));
            adjustmentIn.setPostDate(rs.getTimestamp("POST_DT"));
            adjustmentIn.setPostedBy(mapUser(rs));
            return adjustmentIn;
        }

        private User mapUser(ResultSet rs) throws SQLException {
            long id = rs.getLong("POSTED_BY");
            return rs.wasNull() ? null : new User(id, rs.getString("POSTED_BY_USERNAME"));
        }
        
    };
    
    @Override
    public List<BadStockAdjustmentIn> search(BadStockAdjustmentInSearchCriteria criteria) {
        List<Object> params = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
        sb.append(" where 1 = 1");
        
        if (criteria.getPosted() != null) {
            sb.append(" and POST_IND = ?");
            params.add(criteria.getPosted() ? "Y" : "N");
        }
        
        if (criteria.getBadStockAdjustmentInNumber() != null) {
            sb.append(" and BAD_STOCK_ADJUSTMENT_IN_NO = ?");
            params.add(criteria.getBadStockAdjustmentInNumber());
        }
        
        if (criteria.getPostDateFrom() != null) {
            sb.append(" and POST_DT >= ?");
            params.add(DbUtil.toSqlDate(criteria.getPostDateFrom()));
        }

        if (criteria.getPostDateTo() != null) {
            sb.append(" and POST_DT < ?");
            params.add(DbUtil.toSqlDate(DateUtils.addDays(criteria.getPostDateTo(), 1)));
        }

        sb.append(" order by BAD_STOCK_ADJUSTMENT_IN_NO desc");
        
        return getJdbcTemplate().query(sb.toString(), rowMapper, params.toArray());
    }

    private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
    
    @Override
    public BadStockAdjustmentIn get(Long id) {
        try {
            return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public void save(BadStockAdjustmentIn adjustmentIn) {
        if (adjustmentIn.getId() == null) {
            insert(adjustmentIn);
        } else {
            update(adjustmentIn);
        }
    }

    private static final String INSERT_SQL =
            "insert into BAD_STOCK_ADJUSTMENT_IN"
            + " (BAD_STOCK_ADJUSTMENT_IN_NO, REMARKS, PILFERAGE)"
            + " values (?, ?)";
    
    private void insert(BadStockAdjustmentIn adjustmentIn) {
        long badStockAdjustmentInNumber = getNextBadStockAdjustmentInNumber();
        
        KeyHolder holder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, badStockAdjustmentInNumber);
                ps.setString(2, adjustmentIn.getRemarks());
                ps.setBoolean(3, adjustmentIn.isPilferage());
                return ps;
            }
        }, holder);
        
        adjustmentIn.setId(holder.getKey().longValue());
        adjustmentIn.setBadStockAdjustmentInNumber(badStockAdjustmentInNumber);
    }

    private long getNextBadStockAdjustmentInNumber() {
        return getNextSequenceValue(BAD_STOCK_ADJUSTMENT_IN_NUMBER_SEQUENCE);
    }
    
    private static final String UPDATE_SQL =
            "update BAD_STOCK_ADJUSTMENT_IN"
            + " set REMARKS = ?, PILFERAGE = ?, POST_IND = ?, POST_DT = ?, POSTED_BY = ?"
            + " where ID = ?";
    
    private void update(BadStockAdjustmentIn adjustmentIn) {
        getJdbcTemplate().update(UPDATE_SQL, adjustmentIn.getRemarks(),
        		adjustmentIn.isPilferage(),
                adjustmentIn.isPosted() ? "Y" : "N",
                adjustmentIn.getPostDate(),
                adjustmentIn.isPosted() ? adjustmentIn.getPostedBy().getId() : null,
                adjustmentIn.getId());
    }

}
