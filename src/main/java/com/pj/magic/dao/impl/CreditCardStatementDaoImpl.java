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

import com.pj.magic.dao.CreditCardStatementDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.util.DbUtil;

@Repository
public class CreditCardStatementDaoImpl extends MagicDao implements CreditCardStatementDao {

	private static final String CREDIT_CARD_STATEMENT_NUMBER_SEQUENCE = "CREDIT_CARD_STATEMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, STATEMENT_NO, CREDIT_CARD_ID, STATEMENT_DT,"
			+ " b.USER, b.BANK"
			+ " from CREDIT_CARD_STATEMENT a"
			+ " left join CREDIT_CARD b"
			+ "   on b.ID = a.CREDIT_CARD_ID";
	
	private CreditCardStatementRowMapper statementRowMapper = new CreditCardStatementRowMapper();
	
	@Override
	public List<CreditCardStatement> getAll() {
		return getJdbcTemplate().query(BASE_SELECT_SQL, statementRowMapper);
	}
	
	private class CreditCardStatementRowMapper implements RowMapper<CreditCardStatement> {

		@Override
		public CreditCardStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
			CreditCardStatement statement = new CreditCardStatement();
			statement.setId(rs.getLong("ID"));
			statement.setStatementNumber(rs.getLong("STATEMENT_NO"));
			statement.setCreditCard(mapCreditCard(rs));
			statement.setStatementDate(rs.getDate("STATEMENT_DT"));
			return statement;
		}

		private CreditCard mapCreditCard(ResultSet rs) throws SQLException {
			CreditCard creditCard = new CreditCard();
			creditCard.setId(rs.getLong("CREDIT_CARD_ID"));
			creditCard.setBank(rs.getString("BANK"));
			creditCard.setUser(rs.getString("USER"));
			return creditCard;
		}
	}
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";

	@Override
	public CreditCardStatement get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, statementRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String INSERT_SQL =
			"insert into CREDIT_CARD_STATEMENT (STATEMENT_NO, CREDIT_CARD_ID, STATEMENT_DT) values (?, ?, ?)";
	
	@Override
	public void save(final CreditCardStatement statement) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextStatementNumber());
				ps.setLong(2, statement.getCreditCard().getId());
				ps.setDate(3, DbUtil.toSqlDate(statement.getStatementDate()));
				return ps;
			}
		}, holder);
		
		CreditCardStatement updated = get(holder.getKey().longValue());
		statement.setId(updated.getId());
		statement.setStatementNumber(updated.getStatementNumber());
	}

	protected long getNextStatementNumber() {
		return getNextSequenceValue(CREDIT_CARD_STATEMENT_NUMBER_SEQUENCE);
	}

}
