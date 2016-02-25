package com.pj.magic.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.util.DateUtil;

public class StockQuantityConversionDaoTest extends IntegrationTest {
	
	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;

	@Test
	public void updateCreateDateOfUnposted() {
		jdbcTemplate.batchUpdate(new String[] {
			"insert into STOCK_QTY_CONVERSION (STOCK_QTY_CONV_NO, CREATE_DT, POST_IND) values (1, '2016-02-24', 'Y')",
			"insert into STOCK_QTY_CONVERSION (STOCK_QTY_CONV_NO, CREATE_DT, POST_IND) values (2, '2016-02-24', 'N')"
		});
		
		stockQuantityConversionDao.updateCreateDateOfUnposted(DateUtil.toDate("02/25/2016"));
		
		assertEquals(1, countRowsInTableWhere("STOCK_QTY_CONVERSION", "CREATE_DT = '2016-02-25'"));
		assertEquals(1, countRowsInTableWhere("STOCK_QTY_CONVERSION", "STOCK_QTY_CONV_NO = 2 and CREATE_DT = '2016-02-25'"));
	}
	
}
