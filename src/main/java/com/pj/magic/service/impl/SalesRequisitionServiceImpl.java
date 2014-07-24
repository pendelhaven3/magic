package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.SalesRequisitionService;

@Service
public class SalesRequisitionServiceImpl implements SalesRequisitionService {

	// TODO: temporary implementation until database part comes
	private static List<SalesRequisition> salesRequisitions = new ArrayList<>();
	private static long salesRequisitionNumberCounter = 0;
	
	@Override
	public List<SalesRequisition> getAllSalesRequisitions() {
		return salesRequisitions;
	}

	@Override
	public SalesRequisition save(SalesRequisition salesRequisition) {
		salesRequisition.setSalesRequisitionNumber(++salesRequisitionNumberCounter);
		salesRequisitions.add(salesRequisition);
		return salesRequisition;
	}

}
