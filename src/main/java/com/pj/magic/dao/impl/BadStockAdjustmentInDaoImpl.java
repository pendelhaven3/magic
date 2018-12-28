package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockAdjustmentInDao;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadStockAdjustmentInSearchCriteria;

@Repository
public class BadStockAdjustmentInDaoImpl extends MagicDao implements BadStockAdjustmentInDao {

    private static final String BASE_SELECT_SQL =
            "select a.ID, BAD_STOCK_ADJUSTMENT_IN_NO, REMARKS, POST_IND, POST_DT, POSTED_BY,"
            + " b.USERNAME as POSTED_BY_USERNAME"
            + " from BAD_STOCK_ADJUSTMENT_IN a"
            + " left join USER b"
            + "   on b.ID = a.POSTED_BY";
    
    private RowMapper<BadStockAdjustmentIn> rowMapper = new RowMapper<BadStockAdjustmentIn>() {

        @Override
        public BadStockAdjustmentIn mapRow(ResultSet rs, int rowNum) throws SQLException {
            BadStockAdjustmentIn adjustmentIn = new BadStockAdjustmentIn();
            adjustmentIn.setId(rs.getLong("ID"));
            adjustmentIn.setBadStockAdjustmentInNumber(rs.getLong("BAD_STOCK_ADJUSTMENT_IN_NO"));
            adjustmentIn.setRemarks(rs.getString("REMARKS"));
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

        sb.append(" order by BAD_STOCK_ADJUSTMENT_IN_NO desc");
        
        return getJdbcTemplate().query(sb.toString(), rowMapper, params.toArray());
    }

}
