package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;

@Repository
public class SalesInvoiceItemDaoImpl extends MagicDao implements SalesInvoiceItemDao {

	@Override
	public void save(SalesInvoiceItem item) {
		insert(item);
	}

	private static final String INSERT_SQL =
			"insert into SALES_INVOICE_ITEM (SALES_INVOICE_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(SalesInvoiceItem item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getUnitPrice());
	}

	private static final String FIND_ALL_BY_SALES_INVOICE_SQL =
			"select ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE"
			+ " from SALES_INVOICE_ITEM where SALES_INVOICE_ID = ?";
	
	@Override
	public List<SalesInvoiceItem> findAllBySalesInvoice(final SalesInvoice salesInvoice) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_INVOICE_SQL, new RowMapper<SalesInvoiceItem>() {

			@Override
			public SalesInvoiceItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				SalesInvoiceItem item = new SalesInvoiceItem();
				item.setId(rs.getLong("ID"));
				item.setParent(salesInvoice);
				item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
				item.setUnit(rs.getString("UNIT"));
				item.setQuantity(rs.getInt("QUANTITY"));
				item.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
				return item;
			}
			
		}, salesInvoice.getId());
	}
	
}
