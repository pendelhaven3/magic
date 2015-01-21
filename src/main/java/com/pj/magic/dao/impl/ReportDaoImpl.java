package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.util.DbUtil;
import com.pj.magic.util.QueriesUtil;

@Repository
public class ReportDaoImpl extends MagicDao implements ReportDao {

	private StockCardInventoryReportItemRowMapper rowMapper = new StockCardInventoryReportItemRowMapper();
	
	private static final String BASE_GET_STOCK_CARD_INVENTORY_REPORT_ITEMS_SQL =
			"   select TRANSACTION_DT, TRANSACTION_NO, CUSTOMER_SUPPLIER_NAME, TRANSACTION_TYPE, TRANSACTION_TYPE_KEY,"
			+ " UNIT, QUANTITY, UNIT_COST_OR_PRICE, REFERENCE_NO"
			+ " from ("
			+ "   select a.TRANSACTION_DT, a.SALES_INVOICE_NO as TRANSACTION_NO, c.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'SALES INVOICE' as TRANSACTION_TYPE, 'SALES INVOICE' as TRANSACTION_TYPE_KEY, b.UNIT, b.QUANTITY,"
			+ "   b.UNIT_PRICE as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from SALES_INVOICE a"
			+ "   join SALES_INVOICE_ITEM b"
			+ "     on b.SALES_INVOICE_ID = a.ID"
			+ "   join CUSTOMER c"
			+ "     on c.ID = a.CUSTOMER_ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   union all"
			+ "   select a.TRANSACTION_DT, a.SALES_INVOICE_NO as TRANSACTION_NO, c.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'SALES INVOICE' as TRANSACTION_TYPE, 'SALES INVOICE - CANCEL' as TRANSACTION_TYPE_KEY, b.UNIT, b.QUANTITY,"
			+ "   b.UNIT_PRICE as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from SALES_INVOICE a"
			+ "   join SALES_INVOICE_ITEM b"
			+ "     on b.SALES_INVOICE_ID = a.ID"
			+ "   join CUSTOMER c"
			+ "     on c.ID = a.CUSTOMER_ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.CANCEL_IND = 'Y'"
			+ "   union all"
			+ "   select a.RECEIVED_DT as TRANSACTION_DT, a.RECEIVING_RECEIPT_NO as TRANSACTION_NO,"
			+ "   c.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'RECEIVING RECEIPT' as TRANSACTION_TYPE, 'RECEIVING RECEIPT' as TRANSACTION_TYPE_KEY, b.UNIT, b.QUANTITY,"
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
			+ "   'ADJUSTMENT OUT' as TRANSACTION_TYPE, 'ADJUSTMENT OUT' as TRANSACTION_TYPE_KEY, b.UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, a.REMARKS as REFERENCE_NO"
			+ "   from ADJUSTMENT_OUT a"
			+ "   join ADJUSTMENT_OUT_ITEM b"
			+ "     on b.ADJUSTMENT_OUT_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DT as TRANSACTION_DT, a.ADJUSTMENT_IN_NO as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'ADJUSTMENT IN' as TRANSACTION_TYPE, 'ADJUSTMENT IN' as TRANSACTION_TYPE_KEY, b.UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, a.REMARKS as REFERENCE_NO"
			+ "   from ADJUSTMENT_IN a"
			+ "   join ADJUSTMENT_IN_ITEM b"
			+ "     on b.ADJUSTMENT_IN_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DATE as TRANSACTION_DT, a.STOCK_QTY_CONV_NO as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'STOCK QTY CONVERSION' as TRANSACTION_TYPE, 'STOCK QTY CONVERSION FROM' as TRANSACTION_TYPE_KEY, "
			+ "   b.FROM_UNIT as UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, REMARKS as REFERENCE_NO"
			+ "   from STOCK_QTY_CONVERSION a"
			+ "   join STOCK_QTY_CONVERSION_ITEM b"
			+ "     on b.STOCK_QTY_CONVERSION_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DATE as TRANSACTION_DT, a.STOCK_QTY_CONV_NO as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'STOCK QTY CONVERSION' as TRANSACTION_TYPE, 'STOCK QTY CONVERSION TO' as TRANSACTION_TYPE,"
			+ "   b.TO_UNIT as UNIT, b.CONVERTED_QTY as QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, REMARKS as REFERENCE_NO"
			+ "   from STOCK_QTY_CONVERSION a"
			+ "   join STOCK_QTY_CONVERSION_ITEM b"
			+ "     on b.STOCK_QTY_CONVERSION_ID = a.ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select a.POST_DT as TRANSACTION_DT, a.SALES_RETURN_NO as TRANSACTION_NO,"
			+ "   e.NAME as CUSTOMER_SUPPLIER_NAME,"
			+ "   'SALES RETURN' as TRANSACTION_TYPE, 'SALES RETURN' as TRANSACTION_TYPE_KEY, c.UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from SALES_RETURN a"
			+ "   join SALES_RETURN_ITEM b"
			+ "     on b.SALES_RETURN_ID = a.ID"
			+ "   join SALES_INVOICE_ITEM c"
			+ "     on c.ID = b.SALES_INVOICE_ITEM_ID"
			+ "   join SALES_INVOICE d"
			+ "     on d.ID = a.SALES_INVOICE_ID"
			+ "   join CUSTOMER e"
			+ "     on e.ID = d.CUSTOMER_ID"
			+ "   where c.PRODUCT_ID = ?"
			+ "   and a.POST_IND = 'Y'"
			+ "   union all"
			+ "   select c.INVENTORY_DT as TRANSACTION_DT, null as TRANSACTION_NO,"
			+ "   null as CUSTOMER_SUPPLIER_NAME,"
			+ "   'INVENTORY CHECK' as TRANSACTION_TYPE, 'INVENTORY CHECK' as TRANSACTION_TYPE_KEY, b.UNIT, b.QUANTITY,"
			+ "   null as UNIT_COST_OR_PRICE, null as REFERENCE_NO"
			+ "   from AREA_INV_REPORT a"
			+ "   join AREA_INV_REPORT_ITEM b"
			+ "     on b.AREA_INV_REPORT_ID = a.ID"
			+ "   join INVENTORY_CHECK c"
			+ "     on c.ID = a.INVENTORY_CHECK_ID"
			+ "   where b.PRODUCT_ID = ?"
			+ "   and c.POST_IND = 'Y'"
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
		
		sql.append(" order by TRANSACTION_DT desc, TRANSACTION_TYPE, TRANSACTION_NO, TRANSACTION_TYPE_KEY");
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}
	
	private class StockCardInventoryReportItemRowMapper implements RowMapper<StockCardInventoryReportItem> {

		@Override
		public StockCardInventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			StockCardInventoryReportItem item = new StockCardInventoryReportItem();
			item.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			if (rs.getLong("TRANSACTION_NO") != 0) {
				item.setTransactionNumber(rs.getLong("TRANSACTION_NO"));
			}
			item.setSupplierOrCustomerName(rs.getString("CUSTOMER_SUPPLIER_NAME"));
			item.setTransactionType(rs.getString("TRANSACTION_TYPE"));
			item.setCurrentCostOrSellingPrice(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
			item.setReferenceNumber(rs.getString("REFERENCE_NO"));
			item.setUnit(rs.getString("UNIT"));
			
			switch (rs.getString("TRANSACTION_TYPE_KEY")) {
			case "SALES INVOICE - CANCEL":
				item.setTransactionType("CANCELLED SALES INVOICE");
				// no break here; use same logic as SALES INVOICE transaction type key
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
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "STOCK QTY CONVERSION TO":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "SALES RETURN":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "INVENTORY CHECK":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			}
			
			return item;
		}
		
	}

	private static final String GET_ALL_INVENTORY_REPORT_ITEMS_SQL =
			" select ID, CODE, DESCRIPTION, UNIT, QUANTITY, COST"
			+ " from ("
			+ "   select ID, CODE, DESCRIPTION, 'CSE' as UNIT, AVAIL_QTY_CSE as QUANTITY, FINAL_COST_CSE as COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_CSE = 'Y'"
			+ "   and AVAIL_QTY_CSE > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'TIE' as UNIT, AVAIL_QTY_TIE as QUANTITY, FINAL_COST_TIE as COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_TIE = 'Y'"
			+ "   and AVAIL_QTY_TIE > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'CTN' as UNIT, AVAIL_QTY_CTN as QUANTITY, FINAL_COST_CTN as COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_CTN = 'Y'"
			+ "   and AVAIL_QTY_CTN > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'DOZ' as UNIT, AVAIL_QTY_DOZ as QUANTITY, FINAL_COST_DOZ as COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_DOZ = 'Y'"
			+ "   and AVAIL_QTY_DOZ > 0"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'PCS' as UNIT, AVAIL_QTY_PCS as QUANTITY, FINAL_COST_PCS as COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_PCS = 'Y'"
			+ "   and AVAIL_QTY_PCS > 0"
			+ " ) a"
			+ " order by CODE";
	
	@Override
	public List<InventoryReportItem> getAllInventoryReportItems() {
		return getJdbcTemplate().query(GET_ALL_INVENTORY_REPORT_ITEMS_SQL, new RowMapper<InventoryReportItem>() {

			@Override
			public InventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				InventoryReportItem item = new InventoryReportItem();
				
				Product product = new Product();
				product.setCode(rs.getString("CODE"));
				product.setDescription(rs.getString("DESCRIPTION"));
				item.setProduct(product);
				
				item.setUnit(rs.getString("UNIT"));
				item.setQuantity(rs.getInt("QUANTITY"));
				item.setUnitCost(rs.getBigDecimal("COST"));
				
				return item;
			}
			
		});
	}

	@Override
	public List<CustomerSalesSummaryReportItem> searchCustomerSalesSummaryReportItems(
			Date fromDate, Date toDate) {
		String sql = QueriesUtil.getSql("customerSalesSummaryReport");
		
		Map<String, Object> params = new HashMap<>();
		params.put("fromDate", fromDate);
		params.put("toDate", toDate);
		
		return getNamedParameterJdbcTemplate().query(sql,
				params,
				new RowMapper<CustomerSalesSummaryReportItem>() {

					@Override
					public CustomerSalesSummaryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
						CustomerSalesSummaryReportItem item = new CustomerSalesSummaryReportItem();
						
						Customer customer = new Customer();
						customer.setCode(rs.getString("CUSTOMER_CODE"));
						customer.setName(rs.getString("CUSTOMER_NAME"));
						item.setCustomer(customer);
						
						item.setTotalAmount(rs.getBigDecimal("TOTAL_NET_AMOUNT"));
						item.setTotalCost(rs.getBigDecimal("TOTAL_COST"));
						item.setTotalProfit(rs.getBigDecimal("TOTAL_PROFIT"));
						
						return item;
					}
			
		});
	}

}