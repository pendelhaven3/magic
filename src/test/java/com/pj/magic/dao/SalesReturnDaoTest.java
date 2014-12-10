package com.pj.magic.dao;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.search.SalesReturnSearchCriteria;

public class SalesReturnDaoTest extends IntegrationTest {

	@Autowired private SalesReturnDao salesReturnDao;
	
	@Test
	public void search() {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setPosted(true);
		
		List<SalesReturn> salesReturns = salesReturnDao.search(criteria);
		for (SalesReturn salesReturn : salesReturns) {
			System.out.println(ToStringBuilder.reflectionToString(salesReturn));
		}
	}
	
}