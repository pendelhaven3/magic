package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesReturn;

public interface SalesReturnDao {

	List<SalesReturn> getAll();

	void save(SalesReturn salesReturn);
	
	SalesReturn get(long id);
	
}
