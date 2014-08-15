package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.model.Customer;

@Repository
public class CustomerDaoImpl extends MagicDao implements CustomerDao {

	private static final String BASE_SQL =
			"select ID, CODE, NAME, ADDRESS, CONTACT_PERSON, CONTACT_NUMBER from CUSTOMER";
	
	private CustomerRowMapper customerRowMapper = new CustomerRowMapper();
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(Customer customer) {
		entityManager.persist(customer);
	}

	@Override
	public List<Customer> getAll() {
        return entityManager.createQuery("SELECT c FROM Customer c order by c.code", Customer.class).getResultList();
	}
	
	private class CustomerRowMapper implements RowMapper<Customer> {

		@Override
		public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Customer customer = new Customer();
			customer.setCode(rs.getString("CODE"));
			customer.setId(rs.getLong("ID"));
			customer.setName(rs.getString("NAME"));
			customer.setAddress(rs.getString("ADDRESS"));
			customer.setContactPerson(rs.getString("CONTACT_PERSON"));
			customer.setContactNumber(rs.getString("CONTACT_NUMBER"));
			return customer;
		}
		
	}

	@Override
	public Customer get(long id) {
		return entityManager.find(Customer.class, id);
	}

	private static final String FIND_FIRST_WITH_CODE_LIKE_SQL =
			BASE_SQL + " where CODE like ? limit 1";
	
	@Override
	public Customer findFirstWithCodeLike(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_WITH_CODE_LIKE_SQL, customerRowMapper, code + "%");
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_BY_CODE_SQL =
			BASE_SQL + " where CODE = ?";
	
	@Override
	public Customer findByCode(String code) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CODE_SQL, customerRowMapper, code);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}
