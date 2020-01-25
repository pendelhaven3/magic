package com.pj.magic.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockInventoryCheckItemDao;
import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.model.BadStockInventoryCheckItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;

@Repository
public class BadStockInventoryCheckItemDaoImpl extends MagicDao implements BadStockInventoryCheckItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_STOCK_INVENTORY_CHECK_ID, PRODUCT_ID, UNIT, QUANTITY, a.QUANTITY_CHANGE,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION,"
			+ " b.UNIT_IND_CSE, b.UNIT_IND_TIE, b.UNIT_IND_CTN, b.UNIT_IND_DOZ, b.UNIT_IND_PCS"
			+ " from BAD_STOCK_INVENTORY_CHECK_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private RowMapper<BadStockInventoryCheckItem> mapper = (rs, rowNum) -> {
		BadStockInventoryCheck inventoryCheck = new BadStockInventoryCheck();
		inventoryCheck.setId(rs.getLong("BAD_STOCK_INVENTORY_CHECK_ID"));
		
		Product product = new Product();
		product.setId(rs.getLong("PRODUCT_ID"));
		product.setCode(rs.getString("PRODUCT_CODE"));
		product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
		if ("Y".equals(rs.getString("UNIT_IND_CSE"))) {
			product.getUnits().add(Unit.CASE);
		}
		if ("Y".equals(rs.getString("UNIT_IND_TIE"))) {
			product.getUnits().add(Unit.TIE);
		}
		if ("Y".equals(rs.getString("UNIT_IND_CTN"))) {
			product.getUnits().add(Unit.CARTON);
		}
		if ("Y".equals(rs.getString("UNIT_IND_DOZ"))) {
			product.getUnits().add(Unit.DOZEN);
		}
		if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
			product.getUnits().add(Unit.PIECES);
		}
		
		BadStockInventoryCheckItem item = new BadStockInventoryCheckItem();
		item.setId(rs.getLong("ID"));
		item.setParent(inventoryCheck);
		item.setProduct(product);
		item.setUnit(rs.getString("UNIT"));
		item.setQuantity(rs.getInt("QUANTITY"));
		if (rs.getString("QUANTITY_CHANGE") != null) {
			item.setQuantityChange(rs.getInt("QUANTITY_CHANGE"));
		}
		return item;
	};
	
	@Override
	public void save(BadStockInventoryCheckItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into BAD_STOCK_INVENTORY_CHECK_ITEM"
			+ " (BAD_STOCK_INVENTORY_CHECK_ID, PRODUCT_ID, UNIT, QUANTITY, QUANTITY_CHANGE)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(BadStockInventoryCheckItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(con -> {
			PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, item.getParent().getId());
			ps.setLong(2, item.getProduct().getId());
			ps.setString(3, item.getUnit());
			ps.setInt(4, item.getQuantity());
			if (item.getQuantityChange() != null) {
				ps.setInt(5, item.getQuantityChange());
			} else {
				ps.setNull(5, java.sql.Types.INTEGER);
			}
			return ps;
		}, holder);
			
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update BAD_STOCK_INVENTORY_CHECK_ITEM set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, QUANTITY_CHANGE = ? where ID = ?";
	
	private void update(BadStockInventoryCheckItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getQuantityChange(),
				item.getId());
	}

	private static final String FIND_ALL_BY_BAD_STOCK_INVENTORY_CHECK_SQL = BASE_SELECT_SQL
			+ " where a.BAD_STOCK_INVENTORY_CHECK_ID = ?";
	
	@Override
	public List<BadStockInventoryCheckItem> findAllByBadStockInventoryCheck(BadStockInventoryCheck badStockInventoryCheck) {
		return getJdbcTemplate().query(FIND_ALL_BY_BAD_STOCK_INVENTORY_CHECK_SQL, mapper, badStockInventoryCheck.getId());
	}

	private static final String DELETE_SQL = "delete from BAD_STOCK_INVENTORY_CHECK_ITEM where ID = ?";
	
	@Override
	public void delete(BadStockInventoryCheckItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

}