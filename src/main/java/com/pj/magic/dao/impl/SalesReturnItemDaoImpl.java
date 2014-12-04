package com.pj.magic.dao.impl;

import java.sql.Connection;
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

import com.pj.magic.dao.SalesReturnItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;

@Repository
public class SalesReturnItemDaoImpl extends MagicDao implements SalesReturnItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_RETURN_ID, a.QUANTITY, b.PRODUCT_ID, c.CODE, c.DESCRIPTION,"
			+ " a.SALES_INVOICE_ITEM_ID, b.UNIT, b.UNIT_PRICE"
			+ " from SALES_RETURN_ITEM a"
			+ " join SALES_INVOICE_ITEM b"
			+ "   on b.ID = a.SALES_INVOICE_ITEM_ID"
			+ " join PRODUCT c"
			+ "   on c.ID = b.PRODUCT_ID";
	
	private SalesReturnItemRowMapper salesReturnItemRowMapper = new SalesReturnItemRowMapper();
	
	private static final String FIND_ALL_BY_SALES_RETURN_SQL = BASE_SELECT_SQL
			+ " where a.SALES_RETURN_ID = ?";
	
	@Override
	public List<SalesReturnItem> findAllBySalesReturn(SalesReturn salesReturn) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_RETURN_SQL, salesReturnItemRowMapper,
				salesReturn.getId());
	}

	private class SalesReturnItemRowMapper implements RowMapper<SalesReturnItem> {

		@Override
		public SalesReturnItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesReturnItem item = new SalesReturnItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new SalesReturn(rs.getLong("SALES_RETURN_ID")));
			item.setQuantity(rs.getInt("QUANTITY"));
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("CODE"));
			product.setDescription(rs.getString("DESCRIPTION"));
			
			SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
			salesInvoiceItem.setId(rs.getLong("SALES_INVOICE_ITEM_ID"));
			salesInvoiceItem.setProduct(product);
			salesInvoiceItem.setUnit(rs.getString("UNIT"));
			salesInvoiceItem.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			item.setSalesInvoiceItem(salesInvoiceItem);
			
			return item;
		}
		
	}

	@Override
	public void save(SalesReturnItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL = "insert into SALES_RETURN_ITEM"
			+ " (SALES_RETURN_ID, SALES_INVOICE_ITEM_ID, QUANTITY) values (?, ?, ?)";
	
	private void insert(final SalesReturnItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setLong(2, item.getSalesInvoiceItem().getId());
				ps.setInt(3, item.getQuantity());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update SALES_RETURN_ITEM"
			+ " set SALES_INVOICE_ITEM_ID = ?, QUANTITY = ? where ID = ?";
	
	private void update(SalesReturnItem item) {
		getJdbcTemplate().update(UPDATE_SQL, 
				item.getSalesInvoiceItem().getId(),
				item.getQuantity(),
				item.getId());
	}

	private static final String DELETE_SQL = "delete from SALES_RETURN_ITEM where ID = ?";
	
	@Override
	public void delete(SalesReturnItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}
	
}