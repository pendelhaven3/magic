package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SystemParameterDao;
import com.pj.magic.service.SystemService;

@Service
public class SystemServiceImpl implements SystemService {

	@Autowired private SystemParameterDao systemParameterDao;
	
	@Override
	public String getProgramVersion() {
		return systemParameterDao.getSystemParameterValue("VERSION");
	}

	@Override
	public List<String> getSupportedPrinters() {
		// TODO Auto-generated method stub
		return null;
	}

}
