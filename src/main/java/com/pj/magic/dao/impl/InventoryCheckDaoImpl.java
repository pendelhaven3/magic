package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.model.InventoryCheck;

@Repository
public class InventoryCheckDaoImpl extends MagicDao implements InventoryCheckDao {

	private static final String BASE_SELECT_SQL =
			"select ID, INVENTORY_DT from INVENTORY_CHECK";
	
	private InventoryCheckRowMapper inventoryCheckRowMapper = new InventoryCheckRowMapper();
	
	@Override
	public void save(InventoryCheck inventoryCheck) {
		if (inventoryCheck.getId() == null) {
			insert(inventoryCheck);
		} else {
			update(inventoryCheck);
		}
	}

	private static final String INSERT_SQL = "insert into INVENTORY_CHECK (INVENTORY_DT) values (?)";
	
	private void insert(InventoryCheck inventoryCheck) {
		getJdbcTemplate().update(INSERT_SQL, inventoryCheck.getId());
	}

	private void update(InventoryCheck inventoryCheck) {
		
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where ID = ?";
	
	@Override
	public InventoryCheck get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, inventoryCheckRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by INVENTORY_DT desc";
	
	@Override
	public List<InventoryCheck> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, inventoryCheckRowMapper);
	}

	private class InventoryCheckRowMapper implements RowMapper<InventoryCheck> {

		@Override
		public InventoryCheck mapRow(ResultSet rs, int rowNum) throws SQLException {
			InventoryCheck inventoryCheck = new InventoryCheck();
			inventoryCheck.setId(rs.getLong("ID"));
			inventoryCheck.setInventoryDate(rs.getDate("INVENTORY_DT"));
			return inventoryCheck;
		}
		
	}
	
}
