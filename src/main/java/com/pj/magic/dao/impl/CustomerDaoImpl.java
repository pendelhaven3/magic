package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.model.Customer;

@Repository
public class CustomerDaoImpl extends MagicDao implements CustomerDao {

	private CustomerRowMapper customerRowMapper = new CustomerRowMapper();
	
	@Override
	public void save(Customer customer) {
		if (customer.getId() == null) {
			insert(customer);
		} else {
			update(customer);
		}
	}

	private static final String INSERT_SQL =
			"insert into CUSTOMER (CODE, NAME, ADDRESS) values (?, ?, ?)";
	
	private void insert(final Customer customer) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, customer.getCode());
				ps.setString(2, customer.getName());
				ps.setString(3, customer.getAddress());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		customer.setId(holder.getKey().longValue());
	}

	private void update(Customer customer) {
		// TODO: add implementation
	}

	private static final String GET_ALL_SQL =
			"select ID, CODE, NAME, ADDRESS from CUSTOMER order by NAME";
	
	@Override
	public List<Customer> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, customerRowMapper);
	}
	
	private class CustomerRowMapper implements RowMapper<Customer> {

		@Override
		public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Customer customer = new Customer();
			customer.setCode(rs.getString("CODE"));
			customer.setId(rs.getLong("ID"));
			customer.setName(rs.getString("NAME"));
			customer.setAddress(rs.getString("ADDRESS"));
			return customer;
		}
		
	}

	private static final String GET_SQL =
			"select ID, CODE, NAME, ADDRESS from CUSTOMER where ID = ?";
	
	@Override
	public Customer get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, customerRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}
