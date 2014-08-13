package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.UserDao;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.User;

@Repository
public class UserDaoImpl extends MagicDao implements UserDao {

	private static final String SIMPLE_SELECT_SQL = "select ID, USERNAME from USER";
	
	private UserRowMapper userRowMapper = new UserRowMapper();
	
	@Override
	public void save(User user) {
		if (user.getId() == null) {
			insert(user);
		} else {
			update(user);
		}
	}

	private static final String INSERT_SQL = "insert into USER (USERNAME) values (?)";
	
	private void insert(final User user) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user.getUsername());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		user.setId(holder.getKey().longValue());
	}

	private void update(User user) {
		// TODO: To be implemented
	}

	private static final String GET_SQL = SIMPLE_SELECT_SQL + " where ID = ?";
	
	@Override
	public User get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, userRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("ID"));
			user.setUsername(rs.getString("USERNAME"));
			return user;
		}

	}

}
