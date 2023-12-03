package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.repository.PromoRaffleParticipatingItemsRepository;

@Repository
public class PromoRaffleParticipatingItemsRepositoryImpl extends MagicDao implements PromoRaffleParticipatingItemsRepository {

	private static final String BASE_SELECT_SQL =
			"select a.PRODUCT_ID, b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PROMO_RAFFLE_PARTICIPATING_ITEMS a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " where 1 = 1";
	
	private RowMapper<Product> rowMapper = (rs, rowNum) -> {
		Product product = new Product();
		product.setId(rs.getLong("PRODUCT_ID"));
		product.setCode(rs.getString("PRODUCT_CODE"));
		product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
		return product;
	};
	
	private static final String DOES_REPOSITORY_EXISTS_SQL =
			"select count(1) from information_schema.tables where table_name = 'PROMO_RAFFLE_PARTICIPATING_ITEMS'";
	
	@Override
	public boolean doesRepositoryExists() {
		return getJdbcTemplate().queryForObject(DOES_REPOSITORY_EXISTS_SQL, Integer.class) == 1;
	}

	private static final String CREATE_REPOSITORY_SQL =
			"create table PROMO_RAFFLE_PARTICIPATING_ITEMS ("
			+ " PROMO_ID integer not null,"
			+ " PRODUCT_ID integer not null,"
			+ " constraint PROMO_RAFFLE_PARTICIPATING_ITEMS$PK primary key (PROMO_ID, PRODUCT_ID),"
			+ " constraint PROMO_RAFFLE_PARTICIPATING_ITEMS$FK foreign key (PRODUCT_ID) references PRODUCT (ID))";
	
	@Override
	public void createRepository() {
		getJdbcTemplate().update(CREATE_REPOSITORY_SQL);
	}

	private static final String FIND_ALL_BY_PROMO_SQL = BASE_SELECT_SQL + " and a.PROMO_ID = ? order by b.CODE";
	
	@Override
	public List<Product> findAllByPromo(Promo promo) {
		return getJdbcTemplate().query(FIND_ALL_BY_PROMO_SQL, new Object[] {promo.getId()}, rowMapper);
	}

	private static final String FIND_BY_PROMO_AND_PRODUCT_SQL = BASE_SELECT_SQL
			+ " and a.PROMO_ID = ? and a.PRODUCT_ID = ?";
	
	@Override
	public Product findByPromoAndProduct(Promo promo, Product product) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PROMO_AND_PRODUCT_SQL,
					new Object[] {promo.getId(), product.getId()}, rowMapper);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String INSERT_SQL =
			"insert into PROMO_RAFFLE_PARTICIPATING_ITEMS"
			+ " (PROMO_ID, PRODUCT_ID)"
			+ " values (?, ?)";
	
	@Override
	public void add(Promo promo, Product product) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, promo.getId());
				ps.setLong(2, product.getId());
				return ps;
			}
		}, holder);
	}

	private static final String DELETE_SQL = "delete from PROMO_RAFFLE_PARTICIPATING_ITEMS"
			+ " where PROMO_ID = ? and PRODUCT_ID = ?";
	
	@Override
	public void delete(Promo promo, Product product) {
		getJdbcTemplate().update(DELETE_SQL, promo.getId(), product.getId());
	}

}
