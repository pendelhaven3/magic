package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.UserDao;
import com.pj.magic.model.User;
import com.pj.magic.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired private UserDao userDao;
	
	@Override
	public void save(User user) {
		if (user.getId() == null) {
			user.setPassword(generateNewPassword());
		}
		userDao.save(user);
	}

	private String generateNewPassword() {
		return "farmville"; // TODO: Replace with randomly generated password
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
