package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesRequisitionExtractionWhitelistItemDao;
import com.pj.magic.model.Product;

@Repository
public class SalesRequisitionWhitelistItemDaoImpl extends MagicDao implements SalesRequisitionExtractionWhitelistItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.PRODUCT_ID, b.CODE as PRODUCT_CODE"
			+ " from SALES_REQUISITION_WHITELIST_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	public List<Product> getAll() {
		return getJdbcTemplate().query(BASE_SELECT_SQL, new RowMapper<Product>() {

			@Override
			public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
				Product product = new Product();
				product.setId(rs.getLong("PRODUCT_ID"));
				product.setCode(rs.getString("PRODUCT_CODE"));
				return product;
			}
			
		});
	}
	
}
