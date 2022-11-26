package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.EcashTypeDao;
import com.pj.magic.gui.panels.EcashType;
import com.pj.magic.service.EcashTypeService;

@Service
public class EcashTypeServiceImpl implements EcashTypeService {

	@Autowired
	private EcashTypeDao ecashTypeDao;
	
	@Override
	public List<EcashType> getAllEcashTypes() {
		return ecashTypeDao.getAll();
	}

}
