package com.pj.magic.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockReportDao;
import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadStockReportSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class BadStockReportDaoImpl extends MagicDao implements BadStockReportDao {

	private static final String BAD_STOCK_REPORT_NUMBER_SEQUENCE = "BAD_STOCK_REPORT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_STOCK_REPORT_NO, LOCATION, POST_IND, POST_DT, POST_BY, REMARKS, a.RECEIVED_DT,"
			+ " b.USERNAME as POST_BY_USERNAME"
			+ " from BAD_STOCK_REPORT a"
			+ " left join USER b"
			+ "   on b.ID = a.POST_BY";

	private RowMapper<BadStockReport> mapper = (rs, rownum) -> {
		BadStockReport report = new BadStockReport();
		report.setId(rs.getLong("ID"));
		report.setBadStockReportNumber(rs.getLong("BAD_STOCK_REPORT_NO"));
		report.setLocation(rs.getString("LOCATION"));
		report.setRemarks(rs.getString("REMARKS"));
		report.setPosted("Y".equals(rs.getString("POST_IND")));
		report.setPostDate(rs.getTimestamp("POST_DT"));
		report.setReceivedDate(rs.getDate("RECEIVED_DT"));
		
		if (!StringUtils.isEmpty(rs.getString("POST_BY"))) {
			report.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
		}
		
		return report;
	};

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public BadStockReport get(Long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, mapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(BadStockReport badStockReport) {
		if (badStockReport.getId() == null) {
			insert(badStockReport);
		} else {
			update(badStockReport);
		}
	}

	private static final String INSERT_SQL =
			"insert into BAD_STOCK_REPORT (BAD_STOCK_REPORT_NO, LOCATION) values (?, ?)";
	
	private void insert(BadStockReport badStockReport) {
		Long badStockReportNumber = getNextBadStockReturnNumber();
		
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(con -> {
			PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, badStockReportNumber);
			ps.setString(2, badStockReport.getLocation());
			return ps;
		}, holder);
		
		badStockReport.setId(holder.getKey().longValue());
		badStockReport.setBadStockReportNumber(badStockReportNumber);
	}
	
	private Long getNextBadStockReturnNumber() {
		return getNextSequenceValue(BAD_STOCK_REPORT_NUMBER_SEQUENCE);
	}
	
	private static final String UPDATE_SQL = 
			"update BAD_STOCK_REPORT set LOCATION = ?, REMARKS = ?, RECEIVED_DT = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?"
			+ " where ID = ?";
	
	private void update(BadStockReport badStockReport) {
		getJdbcTemplate().update(UPDATE_SQL,
				badStockReport.getLocation(),
				badStockReport.getRemarks(),
				badStockReport.getReceivedDate(),
				badStockReport.isPosted() ? "Y" : "N",
				badStockReport.getPostDate(),
				badStockReport.getPostedBy() != null ? badStockReport.getPostedBy().getId() : null,
				badStockReport.getId());
	}


	@Override
	public List<BadStockReport> search(BadStockReportSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getBadStockReportNumber() != null) {
			sql.append(" and a.BAD_STOCK_REPORT_NO = ?");
			params.add(criteria.getBadStockReportNumber());
		}

		if (!StringUtils.isEmpty(criteria.getLocation())) {
			sql.append(" and a.LOCATION = ?");
			params.add(criteria.getLocation());
		}

		if (criteria.getPostDateFrom() != null) {
			sql.append(" and a.POST_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateFrom()));
		}
		
		if (criteria.getPostDateTo() != null) {
			sql.append(" and a.POST_DT < date_add(?, interval 1 day)");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateTo()));
		}
		
		sql.append(" order by BAD_STOCK_REPORT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), mapper, params.toArray());
	}

}