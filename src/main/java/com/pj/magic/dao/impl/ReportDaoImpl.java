package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.StockCardInventoryReportItem;

@Repository
public class ReportDaoImpl extends MagicDao implements ReportDao {

	private StockCardInventoryReportItemRowMapper rowMapper = new StockCardInventoryReportItemRowMapper();
	
	private static final String GET_STOCK_CARD_INVENTORY_REPORT_ITEMS_SQL =
			"   select TRANSACTION_DT, TRANSACTION_NO, CUSTOMER_SUPPLIER_NAME, TRANSACTION_TYPE,"
			+ " QUANTITY, UNIT_COST_OR_PRICE, REFERENCE_NO"
			+ " from ("
			+ "   select a.TRANSACTION_DT, a.SALES_INVOICE_NO as TRANSACTION_NO, c.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'SALES INVOICE' as TRANSACTION_TYPE, b.QUANTITY,"
			+ "   b.UNIT_PRICE as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from SALES_INVOICE a"
			+ "   join SALES_INVOICE_ITEM b"
			+ "     on b.SALES_INVOICE_ID = a.ID"
			+ "   join CUSTOMER c"
			+ "     on c.ID = a.CUSTOMER_ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.MARK_IND = 'Y'"
			+ "   union all"
			+ "   select a.RECEIVED_DT as TRANSACTION_DT, a.RECEIVING_RECEIPT_NO as TRANSACTION_NO,"
			+ "   c.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'RECEIVING RECEIPT' as TRANSACTION_TYPE, b.QUANTITY,"
			+ "   b.COST as UNIT_COST_OR_PRICE, a.REFERENCE_NO"
			+ "   from RECEIVING_RECEIPT a"
			+ "   join RECEIVING_RECEIPT_ITEM b"
			+ "     on b.RECEIVING_RECEIPT_ID = a.ID"
			+ "   join SUPPLIER c"
			+ "     on c.ID = a.SUPPLIER_ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ " ) m"
			+ " order by TRANSACTION_DT desc";
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(Product product) {
		return getJdbcTemplate().query(GET_STOCK_CARD_INVENTORY_REPORT_ITEMS_SQL, rowMapper, 
				product.getId(),
				product.getId());
	}
	
	private class StockCardInventoryReportItemRowMapper implements RowMapper<StockCardInventoryReportItem> {

		@Override
		public StockCardInventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			StockCardInventoryReportItem item = new StockCardInventoryReportItem();
			item.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			item.setTransactionNumber(rs.getLong("TRANSACTION_NO"));
			item.setSupplierOrCustomerName(rs.getString("CUSTOMER_SUPPLIER_NAME"));
			item.setTransactionType(rs.getString("TRANSACTION_TYPE"));
			item.setCurrentCostOrSellingPrice(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
			item.setReferenceNumber(rs.getString("REFERENCE_NO"));
			
			switch (rs.getString("TRANSACTION_TYPE")) {
			case "SALES INVOICE":
				SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
				salesInvoiceItem.setQuantity(rs.getInt("QUANTITY"));
				salesInvoiceItem.setUnitPrice(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
				item.setAmount(salesInvoiceItem.getAmount());
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "RECEIVING RECEIPT":
				ReceivingReceiptItem receivingReceiptItem = new ReceivingReceiptItem();
				receivingReceiptItem.setQuantity(rs.getInt("QUANTITY"));
				receivingReceiptItem.setCost(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
				item.setAmount(receivingReceiptItem.getAmount());
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			}
			
			return item;
		}
		
	}

}
