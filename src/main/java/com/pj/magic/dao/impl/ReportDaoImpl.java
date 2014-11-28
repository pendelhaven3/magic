package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class ReportDaoImpl extends MagicDao implements ReportDao {

	private StockCardInventoryReportItemRowMapper rowMapper = new StockCardInventoryReportItemRowMapper();
	
	private static final String BASE_GET_STOCK_CARD_INVENTORY_REPORT_ITEMS_SQL =
			"   select TRANSACTION_DT, TRANSACTION_NO, CUSTOMER_SUPPLIER_NAME, TRANSACTION_TYPE,"
			+ " UNIT, QUANTITY, UNIT_COST_OR_PRICE, REFERENCE_NO"
			+ " from ("
			+ "   select a.TRANSACTION_DT, a.SALES_INVOICE_NO as TRANSACTION_NO, c.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'SALES INVOICE' as TRANSACTION_TYPE, b.UNIT, b.QUANTITY,"
			+ "   b.UNIT_PRICE as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from SALES_INVOICE a"
			+ "   join SALES_INVOICE_ITEM b"
			+ "     on b.SALES_INVOICE_ID = a.ID"
			+ "   join CUSTOMER c"
			+ "     on c.ID = a.CUSTOMER_ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.CANCEL_IND = 'N'"
			+ "   union all"
			+ "   select a.RECEIVED_DT as TRANSACTION_DT, a.RECEIVING_RECEIPT_NO as TRANSACTION_NO,"
			+ "   c.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'RECEIVING RECEIPT' as TRANSACTION_TYPE, b.UNIT, b.QUANTITY,"
			+ "   b.COST as UNIT_COST_OR_PRICE, a.REFERENCE_NO"
			+ "   from RECEIVING_RECEIPT a"
			+ "   join RECEIVING_RECEIPT_ITEM b"
			+ "     on b.RECEIVING_RECEIPT_ID = a.ID"
			+ "   join SUPPLIER c"
			+ "     on c.ID = a.SUPPLIER_ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DT as TRANSACTION_DT, a.ADJUSTMENT_OUT_NO as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'ADJUSTMENT OUT' as TRANSACTION_TYPE, b.UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from ADJUSTMENT_OUT a"
			+ "   join ADJUSTMENT_OUT_ITEM b"
			+ "     on b.ADJUSTMENT_OUT_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DT as TRANSACTION_DT, a.ADJUSTMENT_IN_NO as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'ADJUSTMENT IN' as TRANSACTION_TYPE, b.UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from ADJUSTMENT_IN a"
			+ "   join ADJUSTMENT_IN_ITEM b"
			+ "     on b.ADJUSTMENT_IN_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DATE as TRANSACTION_DT, a.STOCK_QTY_CONV_NO as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'STOCK QTY CONVERSION FROM' as TRANSACTION_TYPE, b.FROM_UNIT as UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from STOCK_QTY_CONVERSION a"
			+ "   join STOCK_QTY_CONVERSION_ITEM b"
			+ "     on b.STOCK_QTY_CONVERSION_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DATE as TRANSACTION_DT, a.STOCK_QTY_CONV_NO as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'STOCK QTY CONVERSION TO' as TRANSACTION_TYPE, b.TO_UNIT as UNIT, b.CONVERTED_QTY as QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from STOCK_QTY_CONVERSION a"
			+ "   join STOCK_QTY_CONVERSION_ITEM b"
			+ "     on b.STOCK_QTY_CONVERSION_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ " ) m";
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		params.add(criteria.getProduct().getId());
		params.add(criteria.getProduct().getId());
		params.add(criteria.getProduct().getId());
		params.add(criteria.getProduct().getId());
		params.add(criteria.getProduct().getId());
		params.add(criteria.getProduct().getId());
		
		StringBuilder sql = new StringBuilder(BASE_GET_STOCK_CARD_INVENTORY_REPORT_ITEMS_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getFromDate() != null) {
			sql.append(" and TRANSACTION_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and TRANSACTION_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		sql.append(" order by TRANSACTION_DT desc");
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
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
			item.setUnit(rs.getString("UNIT"));
			
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
			case "ADJUSTMENT OUT":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "ADJUSTMENT IN":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "STOCK QTY CONVERSION FROM":
				item.setTransactionType("STOCK QTY CONVERSION");
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "STOCK QTY CONVERSION TO":
				item.setTransactionType("STOCK QTY CONVERSION");
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			}
			
			return item;
		}
		
	}

}
