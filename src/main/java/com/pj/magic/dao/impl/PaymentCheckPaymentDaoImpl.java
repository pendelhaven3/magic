package com.pj.magic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PaymentCheckPaymentDao;
import com.pj.magic.model.PaymentCheckPayment;

@Repository
public class PaymentCheckPaymentDaoImpl extends MagicDao implements PaymentCheckPaymentDao {

	@Override
	public void save(PaymentCheckPayment check) {
		if (check.getId() == null) {
			insert(check);
		} else {
			update(check);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PAYMENT_CHECK_PAYMENT (PAYMENT_ID, BANK, CHECK_NO, AMOUNT) values (?, ?, ?, ?)";
	
	private void insert(PaymentCheckPayment check) {
		getJdbcTemplate().update(INSERT_SQL,
				check.getParent().getId(),
				check.getBank(),
				check.getCheckNumber(),
				check.getAmount());
	}

	private void update(PaymentCheckPayment check) {
		// TODO Auto-generated method stub
		
	}

}
