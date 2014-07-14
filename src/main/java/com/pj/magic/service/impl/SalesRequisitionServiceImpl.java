package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.SalesRequisitionService;

@Service
public class SalesRequisitionServiceImpl implements SalesRequisitionService {

	private static List<SalesRequisition> salesRequisitions;
	
	// TODO: temporary
	static {
		salesRequisitions = new ArrayList<>();
		
		for (int i = 1; i <= 10; i++) {
			SalesRequisition salesRequisition = new SalesRequisition();
			salesRequisition.setSalesRequisitionNumber((long)i);
			salesRequisition.setCustomerName("CUSTOMER " + i);
			salesRequisitions.add(salesRequisition);
		}
	}
	
	@Override
	public List<SalesRequisition> getAllSalesRequisitions() {
		return salesRequisitions;
	}

}
