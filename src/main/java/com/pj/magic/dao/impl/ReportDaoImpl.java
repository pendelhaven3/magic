package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.report.SalesByManufacturerReportItem;
import com.pj.magic.model.report.StockOfftakeReportItem;
import com.pj.magic.model.search.SalesByManufacturerReportSearchCriteria;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.model.search.StockOfftakeReportCriteria;
import com.pj.magic.util.DbUtil;
import com.pj.magic.util.QueriesUtil;

@Repository
public class ReportDaoImpl extends MagicDao implements ReportDao {

	private StockCardInventoryReportItemRowMapper rowMapper = new StockCardInventoryReportItemRowMapper();
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(QueriesUtil.getSql("stockCardInventoryReport"));
		sql.append(" where 1 = 1");
		
		Map<String, Object> params = new HashMap<>();
		params.put("product", criteria.getProduct().getId());
		
		if (criteria.getFromDate() != null) {
			sql.append(" and POST_DT >= :fromDate");
			params.put("fromDate", DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and POST_DT < date_add(:toDate, interval 1 day)");
			params.put("toDate", DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		if (criteria.getUnit() != null) {
			sql.append(" and UNIT = :unit");
			params.put("unit", criteria.getUnit());
		}
		
		if (!criteria.getTransactionTypes().isEmpty()) {
			sql.append(" and TRANSACTION_TYPE in (")
				.append(DbUtil.toSqlInValues(criteria.getTransactionTypes())).append(")");
		}
		
		sql.append(" order by POST_DT desc, TRANSACTION_TYPE, TRANSACTION_NO, TRANSACTION_TYPE_KEY");
		
		return getNamedParameterJdbcTemplate().query(sql.toString(), params, rowMapper);
	}
	
	private class StockCardInventoryReportItemRowMapper implements RowMapper<StockCardInventoryReportItem> {

		@Override
		public StockCardInventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			StockCardInventoryReportItem item = new StockCardInventoryReportItem();
			item.setPostDate(rs.getTimestamp("POST_DT"));
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
				
				SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
				salesInvoiceItem.setQuantity(rs.getInt("QUANTITY"));
				salesInvoiceItem.setUnitPrice(rs.getBigDecimal("UNIT_COST_OR_PRICE"));
				item.setAmount(salesInvoiceItem.getAmount());
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "SALES INVOICE":
				salesInvoiceItem = new SalesInvoiceItem();
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
			case "SALES RETURN - CANCEL":
				item.setTransactionType("CANCELLED SALES RETURN");
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "INVENTORY CHECK BEFORE":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "INVENTORY CHECK AFTER":
				item.setAddQuantity(rs.getInt("QUANTITY"));
				break;
			case "PROMO REDEMPTION":
				item.setLessQuantity(rs.getInt("QUANTITY"));
				break;
			case "PURCHASE RETURN":
				item.setLessQuantity(rs.getInt("QUANTITY"));
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
		params.put("fromDate", DbUtil.toMySqlDateString(fromDate));
		params.put("toDate", DbUtil.toMySqlDateString(toDate));
		
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

	@Override
	public List<SalesByManufacturerReportItem> searchSalesByManufacturerReportItems(
			SalesByManufacturerReportSearchCriteria criteria) {
		String sql = QueriesUtil.getSql("salesByManufacturerReport");
		
		Map<String, Object> params = new HashMap<>();
		params.put("fromDate", DbUtil.toMySqlDateString(criteria.getFromDate()));
		params.put("toDate", DbUtil.toMySqlDateString(criteria.getToDate()));
		if (criteria.getCustomer() != null) {
			params.put("customer", criteria.getCustomer().getId());
		} else {
			params.put("customer", "");
		}
		
		return getNamedParameterJdbcTemplate().query(sql,
				params,
				new RowMapper<SalesByManufacturerReportItem>() {

					@Override
					public SalesByManufacturerReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
						SalesByManufacturerReportItem item = new SalesByManufacturerReportItem();
						
						Manufacturer manufacturer = new Manufacturer();
						manufacturer.setName(rs.getString("MANUFACTURER_NAME"));
						item.setManufacturer(manufacturer);
						
						item.setAmount(rs.getBigDecimal("TOTAL_AMOUNT"));
						return item;
					}
			
		});
	}

	@Override
	public List<StockOfftakeReportItem> searchStockOfftakeReportItems(StockOfftakeReportCriteria criteria) {
		String sql = QueriesUtil.getSql("stockOfftakeReport");
		
		Map<String, Object> params = new HashMap<>();
		params.put("manufacturer", criteria.getManufacturer().getId());
		params.put("fromDate", DbUtil.toMySqlDateString(criteria.getFromDate()));
		params.put("toDate", DbUtil.toMySqlDateString(criteria.getToDate()));
		
		return getNamedParameterJdbcTemplate().query(sql,
				params,
				new RowMapper<StockOfftakeReportItem>() {

					@Override
					public StockOfftakeReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
						StockOfftakeReportItem item = new StockOfftakeReportItem();
						item.setProduct(mapProduct(rs));
						item.setUnit(rs.getString("UNIT"));
						item.setQuantity(rs.getInt("QUANTITY"));
						return item;
					}

					private Product mapProduct(ResultSet rs) throws SQLException {
						Product product = new Product();
						product.setCode(rs.getString("CODE"));
						product.setDescription(rs.getString("DESCRIPTION"));
						return product;
					}
		});
	}

}