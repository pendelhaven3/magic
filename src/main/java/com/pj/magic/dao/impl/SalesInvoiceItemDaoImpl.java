package com.pj.magic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.SalesInvoiceItem;

@Repository
public class SalesInvoiceItemDaoImpl extends MagicDao implements SalesInvoiceItemDao {

	@Override
	public void save(SalesInvoiceItem item) {
		insert(item);
	}

	private static final String INSERT_SQL =
			"insert SALES_INVOICE_ITEM (SALES_INVOICE_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(SalesInvoiceItem item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getUnitPrice());
	}

}
