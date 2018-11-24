package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ScheduledPriceChangeDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ScheduledPriceChange;
import com.pj.magic.model.Unit;
import com.pj.magic.model.User;
import com.pj.magic.util.DbUtil;

@Repository
public class ScheduledPriceChangeDaoImpl implements ScheduledPriceChangeDao {

    private static final String BASE_SELECT_SQL = 
            "select a.ID, EFFECTIVE_DT, PRODUCT_ID, PRICING_SCHEME_ID,"
            + " a.UNIT_PRICE_CSE, a.UNIT_PRICE_TIE, a.UNIT_PRICE_CTN, a.UNIT_PRICE_DOZ, a.UNIT_PRICE_PCS, a.COMPANY_LIST_PRICE,"
            + " APPLIED, a.CREATE_BY, a.CREATE_DT,"
            + " b.NAME as PRICING_SCHEME_NAME,"
            + " c.DESCRIPTION as PRODUCT_DESCRIPTION, c.UNIT_IND_CSE, c.UNIT_IND_TIE, c.UNIT_IND_CTN, c.UNIT_IND_DOZ, c.UNIT_IND_PCS"
            + " from SCHEDULED_PRICE_CHANGE a"
            + " join PRICING_SCHEME b"
            + "   on b.ID = a.PRICING_SCHEME_ID"
            + " join PRODUCT c"
            + "   on c.ID = a.PRODUCT_ID"
            + " where 1 = 1";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private RowMapper<ScheduledPriceChange> rowMapper = new RowMapper<ScheduledPriceChange>() {

        @Override
        public ScheduledPriceChange mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScheduledPriceChange scheduledPriceChange = new ScheduledPriceChange();
            scheduledPriceChange.setId(rs.getLong("ID"));
            scheduledPriceChange.setEffectiveDate(rs.getDate("EFFECTIVE_DT"));
            scheduledPriceChange.setPricingScheme(new PricingScheme(rs.getLong("PRICING_SCHEME_ID"), rs.getString("PRICING_SCHEME_NAME")));
            scheduledPriceChange.setProduct(mapProduct(rs));
            scheduledPriceChange.setApplied("Y".equals(rs.getString("APPLIED")));
            scheduledPriceChange.setCreateBy(new User(rs.getLong("CREATE_BY")));
            return scheduledPriceChange;
        }

        private Product mapProduct(ResultSet rs) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("PRODUCT_ID"));
            product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
            if ("Y".equals(rs.getString("UNIT_IND_CSE"))) {
                product.setUnitPrice(Unit.CASE, rs.getBigDecimal("UNIT_PRICE_CSE"));
            }
            if ("Y".equals(rs.getString("UNIT_IND_TIE"))) {
                product.setUnitPrice(Unit.TIE, rs.getBigDecimal("UNIT_PRICE_TIE"));
            }
            if ("Y".equals(rs.getString("UNIT_IND_CTN"))) {
                product.setUnitPrice(Unit.CARTON, rs.getBigDecimal("UNIT_PRICE_CTN"));
            }
            if ("Y".equals(rs.getString("UNIT_IND_DOZ"))) {
                product.setUnitPrice(Unit.DOZEN, rs.getBigDecimal("UNIT_PRICE_DOZ"));
            }
            if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
                product.setUnitPrice(Unit.PIECES, rs.getBigDecimal("UNIT_PRICE_PCS"));
            }
            product.setCompanyListPrice(rs.getBigDecimal("COMPANY_LIST_PRICE"));
            return product;
        }

    };
    
    private static final String INSERT_SQL =
            "insert into SCHEDULED_PRICE_CHANGE"
            + " (EFFECTIVE_DT, PRODUCT_ID, PRICING_SCHEME_ID, UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS,"
            + " COMPANY_LIST_PRICE, CREATE_DT, CREATE_BY) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    @Override
    public void save(final ScheduledPriceChange scheduledPriceChange) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setDate(1, DbUtil.toSqlDate(scheduledPriceChange.getEffectiveDate()));
                ps.setLong(2,  scheduledPriceChange.getProduct().getId());
                ps.setLong(3,  scheduledPriceChange.getPricingScheme().getId());
                ps.setBigDecimal(4, scheduledPriceChange.getUnitPrice(Unit.CASE));
                ps.setBigDecimal(5, scheduledPriceChange.getUnitPrice(Unit.TIE));
                ps.setBigDecimal(6, scheduledPriceChange.getUnitPrice(Unit.CARTON));
                ps.setBigDecimal(7, scheduledPriceChange.getUnitPrice(Unit.DOZEN));
                ps.setBigDecimal(8, scheduledPriceChange.getUnitPrice(Unit.PIECES));
                if (scheduledPriceChange.getProduct().getCompanyListPrice() != null) {
                    ps.setBigDecimal(9, scheduledPriceChange.getProduct().getCompanyListPrice());
                } else {
                    ps.setNull(9, Types.NUMERIC);
                }
                ps.setTimestamp(10, new Timestamp(scheduledPriceChange.getCreateDate().getTime()));
                ps.setLong(11, scheduledPriceChange.getCreateBy().getId());
                return ps;
            }
        }, holder);
        
        scheduledPriceChange.setId(holder.getKey().longValue());
    }

    private static final String FIND_ALL_BY_EFFECTIVE_DATE_LESS_THAN_EQUAL_AND_APPLIED_SQL = BASE_SELECT_SQL
            + " and EFFECTIVE_DT <= ? and APPLIED = ? order by a.ID";
    
    @Override
    public List<ScheduledPriceChange> findAllByEffectiveDateLessThanEqualAndApplied(Date date, boolean applied) {
        return jdbcTemplate.query(FIND_ALL_BY_EFFECTIVE_DATE_LESS_THAN_EQUAL_AND_APPLIED_SQL, 
                new Object[] {DateUtils.truncate(date, Calendar.DATE), applied ? "Y" : "N"}, rowMapper);
    }

    private static final String MARK_AS_APPLIED_SQL = "update SCHEDULED_PRICE_CHANGE set APPLIED = 'Y' where ID = ?";
    
    @Override
    public void markAsApplied(ScheduledPriceChange scheduledPriceChange) {
        jdbcTemplate.update(MARK_AS_APPLIED_SQL, scheduledPriceChange.getId());
    }

    private static final String FIND_ALL_BY_EFFECTIVE_DATE_SQL = BASE_SELECT_SQL + " and EFFECTIVE_DT >= ? order by EFFECTIVE_DT, a.ID";
    
    @Override
    public List<ScheduledPriceChange> findAllByEffectiveDateGreaterThanOrEqual(Date date) {
        return jdbcTemplate.query(FIND_ALL_BY_EFFECTIVE_DATE_SQL, new Object[] {DateUtils.truncate(date, Calendar.DATE)}, rowMapper);
    }

    private static final String DELETE_SQL = "delete from SCHEDULED_PRICE_CHANGE where ID = ?";
    
    @Override
    public void delete(ScheduledPriceChange scheduledPriceChange) {
        jdbcTemplate.update(DELETE_SQL, scheduledPriceChange.getId());
    }

}
