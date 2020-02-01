package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.pj.magic.dao.BadStockDao;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.search.BadStockSearchCriteria;

@Repository
public class BadStockDaoImpl extends MagicDao implements BadStockDao {

    private static final String BASE_SELECT_SQL =
            "select b.ID as PRODUCT_ID, b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION,"
            + " a.AVAIL_QTY_CSE, a.AVAIL_QTY_TIE, a.AVAIL_QTY_CTN, a.AVAIL_QTY_DOZ, a.AVAIL_QTY_PCS,"
            + " b.UNIT_IND_CSE, b.UNIT_IND_TIE, b.UNIT_IND_CTN, b.UNIT_IND_DOZ, b.UNIT_IND_PCS,"
            + " b.GROSS_COST_CSE, b.GROSS_COST_TIE, b.GROSS_COST_CTN, b.GROSS_COST_DOZ, b.GROSS_COST_PCS,"
            + " b.FINAL_COST_CSE, b.FINAL_COST_TIE, b.FINAL_COST_CTN, b.FINAL_COST_DOZ, b.FINAL_COST_PCS"
            + " from BAD_STOCK a"
            + " join PRODUCT b"
            + "   on b.ID = a.PRODUCT_ID"
            + " where 1 = 1";
    
    private RowMapper<BadStock> rowMapper = new RowMapper<BadStock>() {

        @Override
        public BadStock mapRow(ResultSet rs, int rowNum) throws SQLException {
            BadStock badStock = new BadStock();
            badStock.setProduct(mapProduct(rs));
            mapUnitQuantities(badStock, rs);
            return badStock;
        }

        private Product mapProduct(ResultSet rs) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("PRODUCT_ID"));
            product.setCode(rs.getString("PRODUCT_CODE"));
            product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
            mapUnits(product.getUnits(), rs);
            mapUnitCosts(product, rs);
            return product;
        }
        
        private void mapUnitQuantities(BadStock badStock, ResultSet rs) throws SQLException {
            mapUnitQuantity(badStock, rs, Unit.CASE, "AVAIL_QTY_CSE");
            mapUnitQuantity(badStock, rs, Unit.TIE, "AVAIL_QTY_TIE");
            mapUnitQuantity(badStock, rs, Unit.CARTON, "AVAIL_QTY_CTN");
            mapUnitQuantity(badStock, rs, Unit.DOZEN, "AVAIL_QTY_DOZ");
            mapUnitQuantity(badStock, rs, Unit.PIECES, "AVAIL_QTY_PCS");
        }
        
        private void mapUnitQuantity(BadStock badStock, ResultSet rs, String unit, String columnName) throws SQLException {
        	if (badStock.getProduct().hasUnit(unit)) {
                int quantity = rs.getString(columnName) != null ? rs.getInt(columnName) : 0;
                badStock.getUnitQuantities().add(new UnitQuantity(unit, quantity));
        	}
        }
        
        private void mapUnits(List<String> units, ResultSet rs) throws SQLException {
            mapUnit(units, rs, Unit.CASE, "UNIT_IND_CSE");
            mapUnit(units, rs, Unit.TIE, "UNIT_IND_TIE");
            mapUnit(units, rs, Unit.CARTON, "UNIT_IND_CTN");
            mapUnit(units, rs, Unit.DOZEN, "UNIT_IND_DOZ");
            mapUnit(units, rs, Unit.PIECES, "UNIT_IND_PCS");
        }

        private void mapUnit(List<String> units, ResultSet rs, String unit, String columnName) throws SQLException {
            if ("Y".equals(rs.getString(columnName))) {
                units.add(unit);
            }
        }
        
        private void mapUnitCosts(Product product, ResultSet rs) throws SQLException {
        	if (product.hasUnit(Unit.CASE)) {
        		product.getUnitCosts().add(new UnitCost(Unit.CASE, rs.getBigDecimal("GROSS_COST_CSE"), rs.getBigDecimal("FINAL_COST_CSE")));
        	}
        	if (product.hasUnit(Unit.TIE)) {
        		product.getUnitCosts().add(new UnitCost(Unit.TIE, rs.getBigDecimal("GROSS_COST_TIE"), rs.getBigDecimal("FINAL_COST_TIE")));
        	}
        	if (product.hasUnit(Unit.CARTON)) {
        		product.getUnitCosts().add(new UnitCost(Unit.CARTON, rs.getBigDecimal("GROSS_COST_CTN"), rs.getBigDecimal("FINAL_COST_CTN")));
        	}
        	if (product.hasUnit(Unit.DOZEN)) {
        		product.getUnitCosts().add(new UnitCost(Unit.DOZEN, rs.getBigDecimal("GROSS_COST_DOZ"), rs.getBigDecimal("FINAL_COST_DOZ")));
        	}
        	if (product.hasUnit(Unit.PIECES)) {
        		product.getUnitCosts().add(new UnitCost(Unit.PIECES, rs.getBigDecimal("GROSS_COST_PCS"), rs.getBigDecimal("FINAL_COST_PCS")));
        	}
		}
        
    };
    
    private static final String GET_SQL = BASE_SELECT_SQL + " and a.PRODUCT_ID = ?";
    
    @Override
    public BadStock get(Long id) {
        try {
            return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public void save(BadStock badStock) {
        if (get(badStock.getProduct().getId()) == null) {
            insert(badStock);
        } else {
            update(badStock);
        }
    }

    private static final String UPDATE_SQL = "update BAD_STOCK set AVAIL_QTY_CSE = ?, AVAIL_QTY_TIE = ?, AVAIL_QTY_CTN = ?,"
            + " AVAIL_QTY_DOZ = ?, AVAIL_QTY_PCS = ? where PRODUCT_ID = ?";
    
    private void update(BadStock badStock) {
        getJdbcTemplate().update(UPDATE_SQL,
                badStock.getUnitQuantity(Unit.CASE),
                badStock.getUnitQuantity(Unit.TIE),
                badStock.getUnitQuantity(Unit.CARTON),
                badStock.getUnitQuantity(Unit.DOZEN),
                badStock.getUnitQuantity(Unit.PIECES),
                badStock.getProduct().getId());
    }

    private static final String INSERT_SQL = "insert into BAD_STOCK"
            + " (PRODUCT_ID, AVAIL_QTY_CSE, AVAIL_QTY_TIE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS)"
            + " values (?, ?, ?, ?, ?, ?)";
    
    private void insert(BadStock badStock) {
        getJdbcTemplate().update(INSERT_SQL,
                badStock.getProduct().getId(),
                badStock.getUnitQuantity(Unit.CASE),
                badStock.getUnitQuantity(Unit.TIE),
                badStock.getUnitQuantity(Unit.CARTON),
                badStock.getUnitQuantity(Unit.DOZEN),
                badStock.getUnitQuantity(Unit.PIECES));
    }

    @Override
    public List<BadStock> search(BadStockSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
        List<Object> params = new ArrayList<>();
        
		if (criteria.getCodeOrDescriptionLike() != null) {
			sql.append(" and (b.CODE like ? or b.DESCRIPTION like ?)");
			params.add(criteria.getCodeOrDescriptionLike() + "%");
			params.add("%" + criteria.getCodeOrDescriptionLike() + "%");
		}
        
        if (criteria.getSupplier() != null) {
            sql.append(" and exists (select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = a.PRODUCT_ID and sp.SUPPLIER_ID = ?)");
            params.add(criteria.getSupplier().getId());
        }
        
        if (criteria.getEmpty() != null) {
            if (!criteria.getEmpty()) {
                sql.append(" and (a.AVAIL_QTY_CSE > 0 or a.AVAIL_QTY_TIE > 0 or a.AVAIL_QTY_CTN > 0 or a.AVAIL_QTY_DOZ > 0 or a.AVAIL_QTY_PCS > 0)");
            } else {
                throw new UnsupportedOperationException("Search empty bad stock not supported");
            }
        }
        
        sql.append(" order by b.DESCRIPTION");
        
        return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
    }

    private static final String SEARCH_ALL_BY_SUPPLIER_SQL =
            "select b.ID as PRODUCT_ID, b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION,"
            + " a.AVAIL_QTY_CSE, a.AVAIL_QTY_TIE, a.AVAIL_QTY_CTN, a.AVAIL_QTY_DOZ, a.AVAIL_QTY_PCS,"
            + " b.UNIT_IND_CSE, b.UNIT_IND_TIE, b.UNIT_IND_CTN, b.UNIT_IND_DOZ, b.UNIT_IND_PCS,"
            + " b.GROSS_COST_CSE, b.GROSS_COST_TIE, b.GROSS_COST_CTN, b.GROSS_COST_DOZ, b.GROSS_COST_PCS,"
            + " b.FINAL_COST_CSE, b.FINAL_COST_TIE, b.FINAL_COST_CTN, b.FINAL_COST_DOZ, b.FINAL_COST_PCS"
            + " from PRODUCT b"
            + " left join BAD_STOCK a"
            + "   on a.PRODUCT_ID = b.ID"
            + " where 1 = 1";
    
	@Override
	public List<BadStock> searchAllBySupplier(Supplier supplier, String codeOrDescription) {
        StringBuilder sql = new StringBuilder(SEARCH_ALL_BY_SUPPLIER_SQL);
        List<Object> params = new ArrayList<>();
        
		if (!StringUtils.isEmpty(codeOrDescription)) {
			sql.append(" and (b.CODE like ? or b.DESCRIPTION like ?)");
			params.add(codeOrDescription + "%");
			params.add("%" + codeOrDescription + "%");
		}
        
        sql.append(" and exists (select 1 from SUPPLIER_PRODUCT sp where sp.PRODUCT_ID = b.ID and sp.SUPPLIER_ID = ?)");
        params.add(supplier.getId());
        
        sql.append(" order by b.DESCRIPTION");
        
        return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}

}
