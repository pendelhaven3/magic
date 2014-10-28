package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.StockCardInventoryReportItem;

@Repository
public class ReportDaoImpl extends MagicDao implements ReportDao {

	private StockCardInventoryReportItemRowMapper rowMapper = new StockCardInventoryReportItemRowMapper();
	
	private static final String GET_STOCK_CARD_INVENTORY_REPORT_ITEMS_SQL =
			"select a.TRANSACTION_DT, a.SALES_INVOICE_NO, c.NAME as CUSTOMER_NAME,"
			+ " 'SALES INVOICE' as TRANSACTION_TYPE, b.QUANTITY, b.UNIT_PRICE, a.REMARKS"
			+ " from SALES_INVOICE a"
			+ " join SALES_INVOICE_ITEM b"
			+ "   on b.SALES_INVOICE_ID = a.ID"
			+ " join CUSTOMER c"
			+ "   on c.ID = a.CUSTOMER_ID"
			+ " where b.PRODUCT_ID = ?"
			+ " order by TRANSACTION_DT desc";
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(Product product) {
		return getJdbcTemplate().query(GET_STOCK_CARD_INVENTORY_REPORT_ITEMS_SQL, rowMapper, product.getId());
	}
	
	private class StockCardInventoryReportItemRowMapper implements RowMapper<StockCardInventoryReportItem> {

		@Override
		public StockCardInventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
			salesInvoiceItem.setQuantity(rs.getInt("QUANTITY"));
			salesInvoiceItem.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			
			StockCardInventoryReportItem item = new StockCardInventoryReportItem();
			item.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			item.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			item.setSupplierOrCustomerName(rs.getString("CUSTOMER_NAME"));
			item.setTransactionType(rs.getString("TRANSACTION_TYPE"));
			item.setLessQuantity(rs.getInt("QUANTITY"));
			item.setCurrentCostOrSellingPrice(rs.getBigDecimal("UNIT_PRICE"));
			item.setAmount(salesInvoiceItem.getAmount());
			item.setRemarks(rs.getString("REMARKS"));
			return item;
		}
		
	}

}
