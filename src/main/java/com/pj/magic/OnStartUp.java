package com.pj.magic;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReportService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.QueriesUtil;

@Component
public class OnStartUp {

	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	@Autowired private SystemDao systemDao;
	@Autowired private TransactionTemplate transactionTemplate;
	@Autowired private ProductService productService;
	@Autowired private ReportService reportService;
	@Autowired private PromoService promoService;
	@Autowired private JdbcTemplate jdbcTemplate;
	
	public void fire() {
		resetCreateDateOfUnpostedStockQuantityConversions();
		generateDailyProductQuantityDiscrepancyReport();
        applyScheduledPriceChanges();
        updatePromoStatusBasedOnDuration();
        createBirForm2307ReportTable();
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
	
    private void applyScheduledPriceChanges() {
        transactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                productService.applyScheduledPriceChanges(new Date());
                return true;
            }
        });
    }
	
    private void updatePromoStatusBasedOnDuration() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                promoService.updatePromoStatusBasedOnDuration();
            }
        });
    }
    
    private void createBirForm2307ReportTable() {
        String sql = "select count(1) from information_schema.tables where table_schema = 'magic' and table_name = 'bir_form_2307_report'";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        if (result == 0) {
            jdbcTemplate.update(QueriesUtil.getSql("createBirForm2307ReportTable"));
            jdbcTemplate.update("insert into SEQUENCE (NAME) values ('BIR_FORM_2307_REPORT_NO_SEQ')");
        }
    }
    
}
