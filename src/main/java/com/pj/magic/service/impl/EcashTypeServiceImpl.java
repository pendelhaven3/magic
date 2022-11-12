package com.pj.magic.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pj.magic.gui.panels.EcashType;
import com.pj.magic.service.EcashTypeService;

@Service
public class EcashTypeServiceImpl implements EcashTypeService {

	@Override
	public List<EcashType> getAllEcashTypes() {
		return Arrays.asList(
				new EcashType(1L, "GCASH"),
				new EcashType(2L, "MAYA"));
	}

}
