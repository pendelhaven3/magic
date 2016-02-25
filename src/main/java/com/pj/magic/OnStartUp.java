package com.pj.magic;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.pj.magic.dao.StockQuantityConversionDao;

@Component
public class OnStartUp {

	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	@Autowired private TransactionTemplate transactionTemplate;
	
	public void fire() {
		resetCreateDateOfUnpostedStockQuantityConversions();
	}

	private void resetCreateDateOfUnpostedStockQuantityConversions() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				stockQuantityConversionDao.updateCreateDateOfUnposted(new Date());
			}
		});
	}
	
}
