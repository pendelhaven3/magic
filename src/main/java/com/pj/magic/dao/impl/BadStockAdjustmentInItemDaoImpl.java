package com.pj.magic.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockAdjustmentInItemDao;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.BadStockAdjustmentInItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;

@Repository
public class BadStockAdjustmentInItemDaoImpl extends MagicDao implements BadStockAdjustmentInItemDao {

    private static final String BASE_SELECT_SQL =
            "select a.ID, BAD_STOCK_ADJUSTMENT_IN_ID, PRODUCT_ID, UNIT, QUANTITY,"
            + " CODE as PRODUCT_CODE, DESCRIPTION as PRODUCT_DESCRIPTION,"
            + " UNIT_IND_CSE, UNIT_IND_TIE, UNIT_IND_CTN, UNIT_IND_DOZ, UNIT_IND_PCS"
            + " from BAD_STOCK_ADJUSTMENT_IN_ITEM a"
            + " join PRODUCT b"
            + "   on b.ID = a.PRODUCT_ID";
    
    private RowMapper<BadStockAdjustmentInItem> rowMapper = new RowMapper<BadStockAdjustmentInItem>() {

        @Override
        public BadStockAdjustmentInItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            BadStockAdjustmentInItem item = new BadStockAdjustmentInItem();
            item.setId(rs.getLong("ID"));
            item.setParent(mapBadStockAdjustmentIn(rs));
            item.setProduct(mapProduct(rs));
            item.setUnit(rs.getString("UNIT"));
            item.setQuantity(rs.getInt("QUANTITY"));
            return item;
        }

        private BadStockAdjustmentIn mapBadStockAdjustmentIn(ResultSet rs) throws SQLException {
            BadStockAdjustmentIn adjustmentIn = new BadStockAdjustmentIn();
            adjustmentIn.setId(rs.getLong("BAD_STOCK_ADJUSTMENT_IN_ID"));
            return adjustmentIn;
        }
        
        private Product mapProduct(ResultSet rs) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("PRODUCT_ID"));
            product.setCode(rs.getString("PRODUCT_CODE"));
            product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
            
            addProductUnit(product.getUnits(), "UNIT_IND_CSE", Unit.CASE, rs);
            addProductUnit(product.getUnits(), "UNIT_IND_TIE", Unit.TIE, rs);
            addProductUnit(product.getUnits(), "UNIT_IND_CTN", Unit.CARTON, rs);
            addProductUnit(product.getUnits(), "UNIT_IND_DOZ", Unit.DOZEN, rs);
            addProductUnit(product.getUnits(), "UNIT_IND_PCS", Unit.PIECES, rs);
            
            return product;
        }

        private void addProductUnit(List<String> units, String columnName, String unit, ResultSet rs) throws SQLException {
            if ("Y".equals(rs.getString(columnName))) {
                units.add(unit);
            }
        }
    };
    
    @Override
    public void save(BadStockAdjustmentInItem item) {
        if (item.getId() == null) {
            insert(item);
        } else {
            update(item);
        }
    }

    private static final String INSERT_SQL = 
            "insert into BAD_STOCK_ADJUSTMENT_IN_ITEM"
            + " (BAD_STOCK_ADJUSTMENT_IN_ID, PRODUCT_ID, UNIT, QUANTITY)"
            + " values"
            + " (?, ?, ?, ?)";
    
    private void insert(BadStockAdjustmentInItem item) {
        KeyHolder holder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, item.getParent().getId());
            ps.setLong(2, item.getProduct().getId());
            ps.setString(3, item.getUnit());
            ps.setInt(4, item.getQuantity());
            return ps;
        };
        
        getJdbcTemplate().update(psc, holder);
        
        item.setId(holder.getKey().longValue());
    }

    private static final String UPDATE_SQL =
            "update BAD_STOCK_ADJUSTMENT_IN_ITEM"
            + " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?"
            + " where ID = ?";
    
    private void update(BadStockAdjustmentInItem item) {
        getJdbcTemplate().update(UPDATE_SQL, 
                item.getProduct().getId(), 
                item.getUnit(),
                item.getQuantity(),
                item.getId());
    }

    private static final String DELETE_SQL = "delete from BAD_STOCK_ADJUSTMENT_IN_ITEM where ID = ?";
    
    @Override
    public void delete(BadStockAdjustmentInItem item) {
        getJdbcTemplate().update(DELETE_SQL, item.getId());
    }

    private static final String FIND_ALL_BY_BAD_STOCK_ADJUSTMENT_IN_SQL =
            BASE_SELECT_SQL + " where BAD_STOCK_ADJUSTMENT_IN_ID = ?";
    
    @Override
    public List<BadStockAdjustmentInItem> findAllByBadStockAdjustmentIn(BadStockAdjustmentIn adjustmentIn) {
        return getJdbcTemplate().query(FIND_ALL_BY_BAD_STOCK_ADJUSTMENT_IN_SQL, rowMapper, adjustmentIn.getId());
    }

}
