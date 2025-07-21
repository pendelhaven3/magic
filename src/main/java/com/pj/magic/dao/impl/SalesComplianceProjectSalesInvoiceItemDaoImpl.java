package com.pj.magic.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesComplianceProjectSalesInvoiceItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.model.SalesComplianceProjectSalesInvoiceItem;

@Repository
public class SalesComplianceProjectSalesInvoiceItemDaoImpl extends MagicDao implements SalesComplianceProjectSalesInvoiceItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PRODUCT_ID, UNIT, QUANTITY, ORIGINAL_QUANTITY, UNIT_PRICE,"
			+ " DISCOUNT_1, DISCOUNT_2, DISCOUNT_3, FLAT_RATE_DISCOUNT, COST,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " where 1 = 1";
	
	private RowMapper<SalesComplianceProjectSalesInvoiceItem> rowMapper = (rs, rownum) -> {
		Product product = new Product();
		product.setId(rs.getLong("PRODUCT_ID"));
		product.setCode(rs.getString("PRODUCT_CODE"));
		product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
		
		SalesComplianceProjectSalesInvoiceItem item = new SalesComplianceProjectSalesInvoiceItem();
		item.setId(rs.getLong("ID"));
		item.setProduct(product);
		item.setUnit(rs.getString("UNIT"));
		item.setQuantity(rs.getInt("QUANTITY"));
		item.setOriginalQuantity(rs.getInt("ORIGINAL_QUANTITY"));
		item.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
		item.setDiscount1(rs.getBigDecimal("DISCOUNT_1"));
		item.setDiscount2(rs.getBigDecimal("DISCOUNT_2"));
		item.setDiscount3(rs.getBigDecimal("DISCOUNT_3"));
		item.setFlatRateDiscount(rs.getBigDecimal("FLAT_RATE_DISCOUNT"));
		item.setCost(rs.getBigDecimal("COST"));
		return item;
	};
	
	private static final String FIND_ALL_BY_SALES_INVOICE_SQL = BASE_SELECT_SQL
			+ " and a.SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ID = ? order by b.CODE";

	@Override
	public List<SalesComplianceProjectSalesInvoiceItem> findAllBySalesInvoice(SalesComplianceProjectSalesInvoice salesInvoice) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_INVOICE_SQL, rowMapper, salesInvoice.getId());
	}

	@Override
	public void save(SalesComplianceProjectSalesInvoiceItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM"
			+ " (SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ID, PRODUCT_ID, UNIT, QUANTITY, ORIGINAL_QUANTITY, UNIT_PRICE, COST"
			+ ", DISCOUNT_1, DISCOUNT_2, DISCOUNT_3, FLAT_RATE_DISCOUNT)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(SalesComplianceProjectSalesInvoiceItem item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getQuantity(),
				item.getUnitPrice(),
				item.getCost(),
				item.getDiscount1(),
				item.getDiscount2(),
				item.getDiscount3(),
				item.getFlatRateDiscount());
	}
	
	private static final String UPDATE_SQL =
			"update SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM set QUANTITY = ? where ID = ?";
	
	private void update(SalesComplianceProjectSalesInvoiceItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getQuantity(),
				item.getId());
	}

	private static final String REMOVE_ALL_BY_SALES_INVOICE_SQL =
			"delete from SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ITEM where SALES_COMPLIANCE_PROJECT_SALES_INVOICE_ID = ?";
	
	@Override
	public void removeAllBySalesInvoice(SalesComplianceProjectSalesInvoice salesInvoice) {
		getJdbcTemplate().update(REMOVE_ALL_BY_SALES_INVOICE_SQL, salesInvoice.getId());
	}

}