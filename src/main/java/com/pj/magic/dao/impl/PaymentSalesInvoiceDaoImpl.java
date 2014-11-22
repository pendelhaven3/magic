package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PaymentSalesInvoiceDao;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.SalesInvoice;

@Repository
public class PaymentSalesInvoiceDaoImpl extends MagicDao implements PaymentSalesInvoiceDao {

	@Override
	public void save(PaymentSalesInvoice item) {
		insert(item);
	}

	private static final String INSERT_SQL =
			"insert into PAYMENT_SALES_INVOICE (PAYMENT_ID, SALES_INVOICE_ID) values (?, ?)";
	
	private void insert(PaymentSalesInvoice item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getSalesInvoice().getId());
	}

	private static final String FIND_ALL_BY_PAYMENT_SQL =
			"   select a.ID, SALES_INVOICE_ID, b.SALES_INVOICE_NO"
			+ " from PAYMENT_SALES_INVOICE a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID"
			+ " where PAYMENT_ID = ?";
	
	@Override
	public List<PaymentSalesInvoice> findAllByPayment(final Payment payment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PAYMENT_SQL, new RowMapper<PaymentSalesInvoice>() {

			@Override
			public PaymentSalesInvoice mapRow(ResultSet rs, int rowNum) throws SQLException {
				PaymentSalesInvoice item = new PaymentSalesInvoice();
				item.setId(rs.getLong("ID"));
				item.setParent(payment);
				
				SalesInvoice salesInvoice = new SalesInvoice();
				salesInvoice.setId(rs.getLong("SALES_INVOICE_ID"));
				salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
				item.setSalesInvoice(salesInvoice);
				
				return item;
			}
			
		}, payment.getId());
	}
	
}
