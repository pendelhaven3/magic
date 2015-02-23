package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoRedemptionSalesInvoiceDao;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.SalesInvoice;

@Repository
public class PromoRedemptionSalesInvoiceDaoImpl extends MagicDao implements PromoRedemptionSalesInvoiceDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_REDEMPTION_ID, SALES_INVOICE_ID,"
			+ " b.SALES_INVOICE_NO, b.TRANSACTION_DT"
			+ " from PROMO_REDEMPTION_SALES_INVOICE a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID";
	
	private static final String SAVE_SQL = "insert into PROMO_REDEMPTION_SALES_INVOICE"
			+ " (PROMO_REDEMPTION_ID, SALES_INVOICE_ID) values (?, ?)";
	
	private PromoRedemptionSalesInvoiceRowMapper rowMapper = new PromoRedemptionSalesInvoiceRowMapper();
	
	@Override
	public void save(PromoRedemptionSalesInvoice salesInvoice) {
		getJdbcTemplate().update(SAVE_SQL, 
				salesInvoice.getParent().getId(), salesInvoice.getSalesInvoice().getId());
	}

	private static final String FIND_ALL_BY_PROMO_REDEMPTION_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_REDEMPTION_ID = ?";
	
	@Override
	public List<PromoRedemptionSalesInvoice> findAllByPromoRedemption(PromoRedemption promoRedemption) {
		return getJdbcTemplate().query(FIND_ALL_BY_PROMO_REDEMPTION_SQL, 
				rowMapper, promoRedemption.getId());
	}

	private class PromoRedemptionSalesInvoiceRowMapper implements RowMapper<PromoRedemptionSalesInvoice> {

		@Override
		public PromoRedemptionSalesInvoice mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoRedemptionSalesInvoice promoRedemptionSalesInvoice = new PromoRedemptionSalesInvoice();
			promoRedemptionSalesInvoice.setId(rs.getLong("ID"));
			promoRedemptionSalesInvoice.setParent(new PromoRedemption(rs.getLong("PROMO_REDEMPTION_ID")));
			
			SalesInvoice salesInvoice = new SalesInvoice();
			salesInvoice.setId(rs.getLong("SALES_INVOICE_ID"));
			salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			salesInvoice.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			promoRedemptionSalesInvoice.setSalesInvoice(salesInvoice);
			
			return promoRedemptionSalesInvoice;
		}
		
	}

	private static final String DELETE_SQL = "delete from PROMO_REDEMPTION_SALES_INVOICE where ID = ?";
	
	@Override
	public void delete(PromoRedemptionSalesInvoice salesInvoice) {
		getJdbcTemplate().update(DELETE_SQL, salesInvoice.getId());
	}

	private static final String DELETE_ALL_BY_PROMO_REDEMPTION_SQL = 
			"delete from PROMO_REDEMPTION_SALES_INVOICE where PROMO_REDEMPTION_ID = ?";
	
	@Override
	public void deleteAllByPromoRedemption(PromoRedemption promoRedemption) {
		getJdbcTemplate().update(DELETE_ALL_BY_PROMO_REDEMPTION_SQL, promoRedemption.getId());
	}
	
}