package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockDao;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitQuantity;

@Repository
public class BadStockDaoImpl extends MagicDao implements BadStockDao {

    private static final String BASE_SELECT_SQL =
            "select b.ID as PRODUCT_ID, b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION,"
            + " a.AVAIL_QTY_CSE, a.AVAIL_QTY_TIE, a.AVAIL_QTY_CTN, a.AVAIL_QTY_DOZ, a.AVAIL_QTY_PCS"
            + " from BAD_STOCK a"
            + " join PRODUCT b"
            + "   on b.ID = a.PRODUCT_ID";
    
    private RowMapper<BadStock> rowMapper = new RowMapper<BadStock>() {

        @Override
        public BadStock mapRow(ResultSet rs, int rowNum) throws SQLException {
            BadStock badStock = new BadStock();
            badStock.setProduct(mapProduct(rs));
            mapUnitQuantities(badStock.getUnitQuantities(), rs);
            return badStock;
        }

        private Product mapProduct(ResultSet rs) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("PRODUCT_ID"));
            product.setCode(rs.getString("PRODUCT_CODE"));
            product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
            return product;
        }
        
        private void mapUnitQuantities(List<UnitQuantity> unitQuantities, ResultSet rs) throws SQLException {
            mapUnitQuantity(unitQuantities, rs, Unit.CASE, "AVAIL_QTY_CSE");
            mapUnitQuantity(unitQuantities, rs, Unit.TIE, "AVAIL_QTY_TIE");
            mapUnitQuantity(unitQuantities, rs, Unit.CARTON, "AVAIL_QTY_CTN");
            mapUnitQuantity(unitQuantities, rs, Unit.DOZEN, "AVAIL_QTY_DOZ");
            mapUnitQuantity(unitQuantities, rs, Unit.PIECES, "AVAIL_QTY_PCS");
        }
        
        private void mapUnitQuantity(List<UnitQuantity> unitQuantities, ResultSet rs, String unit, String columnName) throws SQLException {
            int quantity = rs.getInt(columnName);
            if (!rs.wasNull()) {
                unitQuantities.add(new UnitQuantity(unit, quantity));
            }
        }
        
    };
    
    private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by b.DESCRIPTION";

    @Override
    public List<BadStock> getAll() {
        return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
    }

}
