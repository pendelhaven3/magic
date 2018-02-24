package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoType6RuleDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType6Rule;

@Repository
public class PromoType6RuleDaoImpl implements PromoType6RuleDao {

	private static final String BASE_SELECT_SQL =
			"   select a.ID, PROMO_ID, TARGET_QUANTITY, PRODUCT_ID, UNIT, QUANTITY,"
            + " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PROMO_TYPE_6_RULE a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private RowMapper<PromoType6Rule> rowMapper = (rs, rownum) -> {
        PromoType6Rule rule = new PromoType6Rule();
        rule.setId(rs.getLong("ID"));
        rule.setParent(new Promo(rs.getLong("PROMO_ID")));
        rule.setTargetQuantity(rs.getInt("TARGET_QUANTITY"));
        
        Product product = new Product();
        product.setId(rs.getLong("PRODUCT_ID"));
        product.setCode(rs.getString("PRODUCT_CODE"));
        product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
        rule.setProduct(product);
        
        rule.setUnit(rs.getString("UNIT"));
        rule.setQuantity(rs.getInt("QUANTITY"));
        
        return rule;
	};
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
	public void save(PromoType6Rule rule) {
		if (rule.isNew()) {
			insert(rule);
		} else {
			update(rule);
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_TYPE_6_RULE (PROMO_ID, TARGET_QUANTITY, PRODUCT_ID, UNIT, QUANTITY) values (?, ?, ?, ?, ?)";
	
	private void insert(final PromoType6Rule rule) {
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, rule.getParent().getId());
				ps.setLong(2, rule.getTargetQuantity());
				ps.setLong(3, rule.getProduct().getId());
                ps.setString(4, rule.getUnit());
                ps.setLong(5, rule.getQuantity());
				return ps;
			}

		}, holder);
		
		PromoType6Rule updated = get(holder.getKey().longValue());
		rule.setId(updated.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	private PromoType6Rule get(long id) {
		try {
			return jdbcTemplate.queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String UPDATE_SQL =
			"update PROMO_TYPE_6_RULE set TARGET_QUANTITY = ?, PRODUCT_ID = ?, UNIT = ?, QUANTITY = ? where ID = ?";
	
	private void update(PromoType6Rule rule) {
		jdbcTemplate.update(UPDATE_SQL,
				rule.getTargetQuantity(),
				rule.getProduct().getId(),
				rule.getUnit(),
				rule.getQuantity(),
				rule.getId());
	}

	private static final String FIND_BY_PROMO_SQL = BASE_SELECT_SQL + " where a.PROMO_ID = ?";
	
	@Override
	public PromoType6Rule findByPromo(Promo promo) {
		try {
			PromoType6Rule rule = jdbcTemplate.queryForObject(FIND_BY_PROMO_SQL, rowMapper, promo.getId());
			rule.setParent(promo);
			return rule;
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}