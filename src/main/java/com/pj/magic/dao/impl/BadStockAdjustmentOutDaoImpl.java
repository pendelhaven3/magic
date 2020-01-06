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

import com.pj.magic.dao.BadStockAdjustmentOutDao;
import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadStockAdjustmentOutSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class BadStockAdjustmentOutDaoImpl extends MagicDao implements BadStockAdjustmentOutDao {

    private static final String BASE_SELECT_SQL =
            "select a.ID, BAD_STOCK_ADJUSTMENT_OUT_NO, REMARKS, PILFERAGE, POST_IND, POST_DT, POSTED_BY,"
            + " b.USERNAME as POSTED_BY_USERNAME"
            + " from BAD_STOCK_ADJUSTMENT_OUT a"
            + " left join USER b"
            + "   on b.ID = a.POSTED_BY";
    
    private static final String BAD_STOCK_ADJUSTMENT_OUT_NUMBER_SEQUENCE = "BAD_STOCK_ADJUSTMENT_OUT_NO_SEQ";
    
    private RowMapper<BadStockAdjustmentOut> rowMapper = new RowMapper<BadStockAdjustmentOut>() {

        @Override
        public BadStockAdjustmentOut mapRow(ResultSet rs, int rowNum) throws SQLException {
            BadStockAdjustmentOut adjustmentOut = new BadStockAdjustmentOut();
            adjustmentOut.setId(rs.getLong("ID"));
            adjustmentOut.setBadStockAdjustmentOutNumber(rs.getLong("BAD_STOCK_ADJUSTMENT_OUT_NO"));
            adjustmentOut.setRemarks(rs.getString("REMARKS"));
            adjustmentOut.setPilferage(rs.getBoolean("PILFERAGE"));
            adjustmentOut.setPosted("Y".equals(rs.getString("POST_IND")));
            adjustmentOut.setPostDate(rs.getTimestamp("POST_DT"));
            adjustmentOut.setPostedBy(mapUser(rs));
            return adjustmentOut;
        }

        private User mapUser(ResultSet rs) throws SQLException {
            long id = rs.getLong("POSTED_BY");
            return rs.wasNull() ? null : new User(id, rs.getString("POSTED_BY_USERNAME"));
        }
        
    };
    
    @Override
    public List<BadStockAdjustmentOut> search(BadStockAdjustmentOutSearchCriteria criteria) {
        List<Object> params = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
        sb.append(" where 1 = 1");
        
        if (criteria.getPosted() != null) {
            sb.append(" and POST_IND = ?");
            params.add(criteria.getPosted() ? "Y" : "N");
        }
        
        if (criteria.getBadStockAdjustmentOutNumber() != null) {
            sb.append(" and BAD_STOCK_ADJUSTMENT_OUT_NO = ?");
            params.add(criteria.getBadStockAdjustmentOutNumber());
        }
        
        if (criteria.getPostDateFrom() != null) {
            sb.append(" and POST_DT >= ?");
            params.add(DbUtil.toSqlDate(criteria.getPostDateFrom()));
        }

        if (criteria.getPostDateTo() != null) {
            sb.append(" and POST_DT < ?");
            params.add(DbUtil.toSqlDate(DateUtils.addDays(criteria.getPostDateTo(), 1)));
        }

        sb.append(" order by BAD_STOCK_ADJUSTMENT_OUT_NO desc");
        
        return getJdbcTemplate().query(sb.toString(), rowMapper, params.toArray());
    }

    private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
    
    @Override
    public BadStockAdjustmentOut get(Long id) {
        try {
            return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public void save(BadStockAdjustmentOut adjustmentOut) {
        if (adjustmentOut.getId() == null) {
            insert(adjustmentOut);
        } else {
            update(adjustmentOut);
        }
    }

    private static final String INSERT_SQL =
            "insert into BAD_STOCK_ADJUSTMENT_OUT"
            + " (BAD_STOCK_ADJUSTMENT_OUT_NO, REMARKS, PILFERAGE)"
            + " values (?, ?, ?)";
    
    private void insert(BadStockAdjustmentOut adjustmentOut) {
        long badStockAdjustmentOutNumber = getNextBadStockAdjustmentOutNumber();
        
        KeyHolder holder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, badStockAdjustmentOutNumber);
                ps.setString(2, adjustmentOut.getRemarks());
                ps.setBoolean(3, adjustmentOut.isPilferage());
                return ps;
            }
        }, holder);
        
        adjustmentOut.setId(holder.getKey().longValue());
        adjustmentOut.setBadStockAdjustmentOutNumber(badStockAdjustmentOutNumber);
    }

    private long getNextBadStockAdjustmentOutNumber() {
        return getNextSequenceValue(BAD_STOCK_ADJUSTMENT_OUT_NUMBER_SEQUENCE);
    }
    
    private static final String UPDATE_SQL =
            "update BAD_STOCK_ADJUSTMENT_OUT"
            + " set REMARKS = ?, PILFERAGE = ?, POST_IND = ?, POST_DT = ?, POSTED_BY = ?"
            + " where ID = ?";
    
    private void update(BadStockAdjustmentOut adjustmentOut) {
        getJdbcTemplate().update(UPDATE_SQL,
        		adjustmentOut.getRemarks(),
        		adjustmentOut.isPilferage(),
                adjustmentOut.isPosted() ? "Y" : "N",
                adjustmentOut.getPostDate(),
                adjustmentOut.isPosted() ? adjustmentOut.getPostedBy().getId() : null,
                adjustmentOut.getId());
    }

}
