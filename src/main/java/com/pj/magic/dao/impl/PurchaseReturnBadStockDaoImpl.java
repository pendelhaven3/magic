package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PurchaseReturnBadStockDao;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchaseReturnBadStockSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchaseReturnBadStockDaoImpl extends MagicDao implements PurchaseReturnBadStockDao {

	private static final String PURCHASE_RETURN_BAD_STOCK_NUMBER_SEQUENCE = "PURCHASE_RETURN_BAD_STOCK_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, PURCHASE_RETURN_BAD_STOCK_NO, SUPPLIER_ID, POST_IND, POST_DT, POST_BY, PAID_IND, PAID_DT, PAID_BY,"
			+ " a.REMARKS,"
			+ " b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME,"
			+ " b.FAX_NUMBER as SUPPLIER_FAX_NUMBER, b.CONTACT_NUMBER as SUPPLIER_CONTACT_NUMBER, b.ADDRESS as SUPPLIER_ADDRESS,"
			+ " c.USERNAME as POST_BY_USERNAME,"
			+ " d.USERNAME as PAID_BY_USERNAME"
			+ " from PURCHASE_RETURN_BAD_STOCK a"
			+ " join SUPPLIER b"
			+ "   on b.ID = a.SUPPLIER_ID"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY"
			+ " left join USER d"
			+ "   on d.ID = a.PAID_BY";

	private RowMapper<PurchaseReturnBadStock> rowMapper = new RowMapper<PurchaseReturnBadStock>() {

        @Override
        public PurchaseReturnBadStock mapRow(ResultSet rs, int rowNum) throws SQLException {
            PurchaseReturnBadStock purchaseReturnBadStock = new PurchaseReturnBadStock();
            purchaseReturnBadStock.setId(rs.getLong("ID"));
            purchaseReturnBadStock.setPurchaseReturnBadStockNumber(rs.getLong("PURCHASE_RETURN_BAD_STOCK_NO"));
            purchaseReturnBadStock.setSupplier(mapSupplier(rs));
            
            purchaseReturnBadStock.setPosted("Y".equals(rs.getString("POST_IND")));
            if (purchaseReturnBadStock.isPosted()) {
                purchaseReturnBadStock.setPostDate(rs.getDate("POST_DT"));
                purchaseReturnBadStock.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
            }
            
            purchaseReturnBadStock.setPaid("Y".equals(rs.getString("PAID_IND")));
            if (purchaseReturnBadStock.isPaid()) {
                purchaseReturnBadStock.setPaidDate(rs.getDate("PAID_DT"));
                purchaseReturnBadStock.setPaidBy(new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
            }
            
            purchaseReturnBadStock.setRemarks(rs.getString("REMARKS"));
            
            return purchaseReturnBadStock;
        }

        private Supplier mapSupplier(ResultSet rs) throws SQLException {
            Supplier supplier = new Supplier();
            supplier.setId(rs.getLong("SUPPLIER_ID"));
            supplier.setCode(rs.getString("SUPPLIER_CODE"));
            supplier.setName(rs.getString("SUPPLIER_NAME"));
            supplier.setContactNumber(rs.getString("SUPPLIER_CONTACT_NUMBER"));
            supplier.setFaxNumber(rs.getString("SUPPLIER_FAX_NUMBER"));
            supplier.setAddress(rs.getString("SUPPLIER_ADDRESS"));
            return supplier;
        }
	    
	};
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public PurchaseReturnBadStock get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(PurchaseReturnBadStock purchaseReturnBadStock) {
		if (purchaseReturnBadStock.getId() == null) {
			insert(purchaseReturnBadStock);
		} else {
			update(purchaseReturnBadStock);
		}
	}

	private static final String UPDATE_SQL = 
			"update PURCHASE_RETURN_BAD_STOCK set SUPPLIER_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " PAID_IND = ?, PAID_DT = ?, PAID_BY = ?, REMARKS = ? where ID = ?";
	
	private void update(PurchaseReturnBadStock purchaseReturnBadStock) {
		getJdbcTemplate().update(UPDATE_SQL,
				purchaseReturnBadStock.getSupplier().getId(),
				purchaseReturnBadStock.isPosted() ? "Y" : "N",
				purchaseReturnBadStock.getPostDate(),
				purchaseReturnBadStock.isPosted() ? purchaseReturnBadStock.getPostedBy().getId() : null,
				purchaseReturnBadStock.isPaid() ? "Y" : "N",
				purchaseReturnBadStock.getPaidDate(),
				purchaseReturnBadStock.isPaid() ? purchaseReturnBadStock.getPaidBy().getId() : null,
				purchaseReturnBadStock.getRemarks(),
				purchaseReturnBadStock.getId());
	}

	private static final String INSERT_SQL =
			"insert into PURCHASE_RETURN_BAD_STOCK (PURCHASE_RETURN_BAD_STOCK_NO, SUPPLIER_ID) values (?, ?)";
	
	private void insert(final PurchaseReturnBadStock purchaseReturnBadStock) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPurchaseReturnBadStockNumber());
				ps.setLong(2, purchaseReturnBadStock.getSupplier().getId());
				return ps;
			}
		}, holder);
		
		PurchaseReturnBadStock updated = get(holder.getKey().longValue());
		purchaseReturnBadStock.setId(updated.getId());
		purchaseReturnBadStock.setPurchaseReturnBadStockNumber(updated.getPurchaseReturnBadStockNumber());
	}
	
	private Long getNextPurchaseReturnBadStockNumber() {
		return getNextSequenceValue(PURCHASE_RETURN_BAD_STOCK_NUMBER_SEQUENCE);
	}

	@Override
	public List<PurchaseReturnBadStock> search(PurchaseReturnBadStockSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getPurchaseReturnBadStockNumber() != null) {
			sql.append(" and a.PURCHASE_RETURN_BAD_STOCK_NO = ?");
			params.add(criteria.getPurchaseReturnBadStockNumber());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
		}
		
		if (criteria.getPaid() != null) {
			sql.append(" and a.PAID_IND = ?");
			params.add(criteria.getPaid() ? "Y" : "N");
		}

		if (criteria.getPaidDate() != null) {
			sql.append(" and a.PAID_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPaidDate()));
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and a.SUPPLIER_ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		sql.append(" order by PURCHASE_RETURN_BAD_STOCK_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}

	private static final String FIND_BY_PURCHASE_RETURN_BAD_STOCK_NUMBER_SQL = BASE_SELECT_SQL +
			" where a.PURCHASE_RETURN_BAD_STOCK_NO = ?";
	
	@Override
	public PurchaseReturnBadStock findByPurchaseReturnBadStockNumber(long purchaseReturnBadStockNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PURCHASE_RETURN_BAD_STOCK_NUMBER_SQL,
					rowMapper, purchaseReturnBadStockNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}