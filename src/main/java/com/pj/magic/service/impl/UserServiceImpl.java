package com.pj.magic.service.impl;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.UserDao;
import com.pj.magic.model.User;
import com.pj.magic.service.UserService;
import com.pj.magic.util.PasswordTransformer;

@Service
public class UserServiceImpl implements UserService {

	@Autowired private UserDao userDao;
	
	@Override
	public void save(User user) {
		if (user.getId() == null) {
			user.setPlainPassword(generateNewPassword());
			user.setPassword(PasswordTransformer.transform(user.getPlainPassword()));
		}
		userDao.save(user);
	}

	private String generateNewPassword() {
		return RandomStringUtils.randomAlphanumeric(8);
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.getAll();
	}

	@Override
	public User getUser(long id) {
		return userDao.get(id);
	}

	@Override
	public User findUserByUsername(String username) {
		return userDao.findByUsername(username);
	}

}
