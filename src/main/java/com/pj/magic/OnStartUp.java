package com.pj.magic;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReportService;

@Component
public class OnStartUp {

	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	@Autowired private SystemDao systemDao;
	@Autowired private TransactionTemplate transactionTemplate;
	@Autowired private ProductService productService;
	@Autowired private ReportService reportService;
	
	public void fire() {
		resetCreateDateOfUnpostedStockQuantityConversions();
		if (isServer()) {
			generateDailyProductQuantityDiscrepancyReport();
		}
	}

	private boolean isServer() {
		return Files.exists(Paths.get("magic-lib", "httpserver"));
	}

	private void resetCreateDateOfUnpostedStockQuantityConversions() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				stockQuantityConversionDao.updateCreateDateOfUnposted(systemDao.getCurrentDateTime());
			}
		});
	}

	private void generateDailyProductQuantityDiscrepancyReport() {
		if (saveDailyProductStartingQuantities()) {
			reportService.generateDailyProductQuantityDiscrepancyReport();
		};
	}
	
	private boolean saveDailyProductStartingQuantities() {
		return transactionTemplate.execute(new TransactionCallback<Boolean>() {

			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				return productService.saveDailyProductStartingQuantities();
			}
		});
	}
	
}
