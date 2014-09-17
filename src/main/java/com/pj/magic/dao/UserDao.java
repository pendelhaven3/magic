package com.pj.magic.dao;

import com.pj.magic.model.User;

public interface UserDao {

	void save(User user);
	
	User get(long id);
	
	User findByUsername(String username);
	
}
