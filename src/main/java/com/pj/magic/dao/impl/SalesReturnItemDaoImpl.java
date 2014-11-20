package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesReturnItemDao;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;

@Repository
public class SalesReturnItemDaoImpl extends MagicDao implements SalesReturnItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_RETURN_ID, a.SALES_INVOICE_ITEM_ID, a.QUANTITY"
			+ " from SALES_RETURN_ITEM a"
			+ " join SALES_INVOICE_ITEM b"
			+ "   on b.ID = a.SALES_INVOICE_ITEM_ID";
	
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
			item.setItem(new SalesInvoiceItem(rs.getLong("SALES_INVOICE_ITEM_ID")));
			if (rs.getInt("QUANTITY") != 0) {
				item.setQuantity(rs.getInt("QUANTITY"));
			}
			return item;
		}
		
	}
	
}