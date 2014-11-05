package com.pj.magic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SystemParameterDao;
import com.pj.magic.service.SystemParameterService;

@Service
public class SystemParameterServiceImpl implements SystemParameterService {

	@Autowired private SystemParameterDao systemParameterDao;
	
	@Override
	public String getProgramVersion() {
		return systemParameterDao.getSystemParameterValue("VERSION");
	}

}
