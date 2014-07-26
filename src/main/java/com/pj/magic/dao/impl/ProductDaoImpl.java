package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.Product;

@Repository
public class ProductDaoImpl extends MagicDao implements ProductDao {
	
	@Override
	public List<Product> getAllProducts() {
		return getJdbcTemplate().query("select ID, CODE, DESCRIPTION from PRODUCT", new RowMapper<Product>() {

			@Override
			public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
				Product product = new Product();
				product.setId(rs.getInt("ID"));
				product.setCode(rs.getString("CODE"));
				product.setDescription(rs.getString("DESCRIPTION"));
				return product;
			}
			
		});
	}

}
