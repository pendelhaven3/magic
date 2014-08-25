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

import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.model.StockQuantityConversion;

@Repository
public class StockQuantityConversionDaoImpl extends MagicDao implements StockQuantityConversionDao {
	
	private static final String BASE_SELECT_SQL = "select ID, STOCK_QTY_CONV_NO, REMARKS from STOCK_QTY_CONVERSION";
	
	private StockQuantityConversionRowMapper stockQuantityConversionRowMapper = new StockQuantityConversionRowMapper();
	
	@Override
	public void save(StockQuantityConversion stockQuantityConversion) {
		if (stockQuantityConversion.getId() == null) {
			insert(stockQuantityConversion);
		} else {
			update(stockQuantityConversion);
		}
	}

	private static final String INSERT_SQL = "insert into STOCK_QTY_CONVERSION"
			+ " (REMARKS) values (?)";
	
	private void insert(final StockQuantityConversion stockQuantityConversion) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, stockQuantityConversion.getRemarks());
				return ps;
			}
		}, holder); // TODO: check if keyholder works with oracle db
		
		StockQuantityConversion updated = get(holder.getKey().longValue());
		stockQuantityConversion.setId(updated.getId());
		stockQuantityConversion.setStockQuantityConversionNumber(updated.getStockQuantityConversionNumber());
	}

	private static final String UPDATE_SQL = "update STOCK_QTY_CONVERSION set REMARKS = ? where ID = ?";
	
	private void update(StockQuantityConversion stockQuantityConversion) {
		getJdbcTemplate().update(UPDATE_SQL, stockQuantityConversion.getRemarks(), stockQuantityConversion.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where id = ?";
	
	@Override
	public StockQuantityConversion get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, stockQuantityConversionRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by ID desc";
	
	@Override
	public List<StockQuantityConversion> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, stockQuantityConversionRowMapper);
	}

	private class StockQuantityConversionRowMapper implements RowMapper<StockQuantityConversion> {

		@Override
		public StockQuantityConversion mapRow(ResultSet rs, int rowNum) throws SQLException {
			StockQuantityConversion stockQuantityConversion = new StockQuantityConversion();
			stockQuantityConversion.setId(rs.getLong("ID"));
			stockQuantityConversion.setStockQuantityConversionNumber(rs.getLong("STOCK_QTY_CONV_NO"));
			stockQuantityConversion.setRemarks(rs.getString("REMARKS"));
			return stockQuantityConversion;
		}
		
	}
	
}
