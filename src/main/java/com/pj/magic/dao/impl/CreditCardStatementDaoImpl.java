package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CreditCardStatementDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;

@Repository
public class CreditCardStatementDaoImpl extends MagicDao implements CreditCardStatementDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, STATEMENT_NO, CREDIT_CARD_ID, STATEMENT_DT,"
			+ " b.USER, b.BANK"
			+ " from CREDIT_CARD_STATEMENT a"
			+ " left join CREDIT_CARD b"
			+ "   on b.ID = a.CREDIT_CARD_ID";
	
	private CreditCardStatementRowMapper statementRowMapper = new CreditCardStatementRowMapper();
	
	@Override
	public List<CreditCardStatement> getAll(CreditCardStatement statement) {
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

}
