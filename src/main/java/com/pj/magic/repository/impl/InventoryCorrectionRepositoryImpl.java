package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.Product;
import com.pj.magic.model.User;
import com.pj.magic.repository.InventoryCorrectionRepository;

@Repository
public class InventoryCorrectionRepositoryImpl extends MagicDao implements InventoryCorrectionRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PRODUCT_ID, UNIT, NEW_QUANTITY, OLD_QUANTITY, COST, POST_DT, POST_BY, REMARKS"
			+ " , b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " , c.USERNAME as POST_BY_USERNAME"
			+ " from INVENTORY_CORRECTION a"
			+ " join PRODUCT b"
			+ "	  on b.ID = a.PRODUCT_ID"
			+ " join USER c"
			+ "   on c.ID = a.POST_BY";
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.POST_DT desc";
	
	private InventoryCorrectionRowMapper rowMapper = new InventoryCorrectionRowMapper();
	
	@Override
	public List<InventoryCorrection> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public InventoryCorrection get(long id) {
		return getJdbcTemplate().queryForObject(GET_SQL, new Object[] {id}, rowMapper);
	}

	private static final String SAVE_SQL =
			"insert into INVENTORY_CORRECTION"
			+ " (PRODUCT_ID, UNIT, NEW_QUANTITY, OLD_QUANTITY, COST, POST_DT, POST_BY, REMARKS)"
			+ " values"
			+ " (?, ?, ?, ?, ?, current_date(), ?, ?)";

	@Override
	public void save(final InventoryCorrection inventoryCorrection) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, inventoryCorrection.getProduct().getId());
				ps.setString(2, inventoryCorrection.getUnit());
				ps.setInt(3, inventoryCorrection.getNewQuantity());
				ps.setInt(4, inventoryCorrection.getOldQuantity());
				ps.setBigDecimal(5, inventoryCorrection.getCost());
				ps.setLong(6, inventoryCorrection.getPostedBy().getId());
				ps.setString(7, inventoryCorrection.getRemarks());
				return ps;
			}
		}, holder);
		
		inventoryCorrection.setId(holder.getKey().longValue());
	}

	private class InventoryCorrectionRowMapper implements RowMapper<InventoryCorrection> {

		@Override
		public InventoryCorrection mapRow(ResultSet rs, int rowNum) throws SQLException {
			InventoryCorrection inventoryCorrection = new InventoryCorrection();
			inventoryCorrection.setId(rs.getLong("ID"));
			inventoryCorrection.setProduct(mapProduct(rs));
			inventoryCorrection.setUnit(rs.getString("UNIT"));
			inventoryCorrection.setNewQuantity(rs.getInt("NEW_QUANTITY"));
			inventoryCorrection.setOldQuantity(rs.getInt("OLD_QUANTITY"));
			inventoryCorrection.setCost(rs.getBigDecimal("COST"));
			inventoryCorrection.setRemarks(rs.getString("REMARKS"));
			inventoryCorrection.setPostDate(rs.getTimestamp("POST_DT"));
			inventoryCorrection.setPostedBy(
					new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			return inventoryCorrection;
		}

		private Product mapProduct(ResultSet rs) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			return product;
		}
		
	}
	
}
