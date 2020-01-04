package com.pj.magic.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockReportItemDao;
import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.BadStockReportItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;

@Repository
public class BadStockReportItemDaoImpl extends MagicDao implements BadStockReportItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_STOCK_REPORT_ID, PRODUCT_ID, UNIT, QUANTITY,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION,"
			+ " b.UNIT_IND_CSE, b.UNIT_IND_TIE, b.UNIT_IND_CTN, b.UNIT_IND_DOZ, b.UNIT_IND_PCS"
			+ " from BAD_STOCK_REPORT_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private RowMapper<BadStockReportItem> mapper = (rs, rowNum) -> {
		BadStockReport report = new BadStockReport();
		report.setId(rs.getLong("BAD_STOCK_REPORT_ID"));
		
		Product product = new Product();
		product.setId(rs.getLong("PRODUCT_ID"));
		product.setCode(rs.getString("PRODUCT_CODE"));
		product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
		if ("Y".equals(rs.getString("UNIT_IND_CSE"))) {
			product.getUnits().add(Unit.CASE);
		}
		if ("Y".equals(rs.getString("UNIT_IND_TIE"))) {
			product.getUnits().add(Unit.TIE);
		}
		if ("Y".equals(rs.getString("UNIT_IND_CTN"))) {
			product.getUnits().add(Unit.CARTON);
		}
		if ("Y".equals(rs.getString("UNIT_IND_DOZ"))) {
			product.getUnits().add(Unit.DOZEN);
		}
		if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
			product.getUnits().add(Unit.PIECES);
		}
		
		BadStockReportItem item = new BadStockReportItem();
		item.setId(rs.getLong("ID"));
		item.setParent(report);
		item.setProduct(product);
		item.setUnit(rs.getString("UNIT"));
		item.setQuantity(rs.getInt("QUANTITY"));
		return item;
	};
	
	@Override
	public void save(BadStockReportItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into BAD_STOCK_REPORT_ITEM"
			+ " (BAD_STOCK_REPORT_ID, PRODUCT_ID, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(BadStockReportItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(con -> {
			PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, item.getParent().getId());
			ps.setLong(2, item.getProduct().getId());
			ps.setString(3, item.getUnit());
			ps.setInt(4, item.getQuantity());
			return ps;
		}, holder);
			
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update BAD_STOCK_REPORT_ITEM set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ? where ID = ?";
	
	private void update(BadStockReportItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getId());
	}

	private static final String FIND_ALL_BY_BAD_STOCK_REPORT_SQL = BASE_SELECT_SQL
			+ " where a.BAD_STOCK_REPORT_ID = ?";
	
	@Override
	public List<BadStockReportItem> findAllByBadStockReport(BadStockReport badStockReport) {
		return getJdbcTemplate().query(FIND_ALL_BY_BAD_STOCK_REPORT_SQL, mapper, badStockReport.getId());
	}

	private static final String DELETE_SQL = "delete from BAD_STOCK_REPORT_ITEM where ID = ?";
	
	@Override
	public void delete(BadStockReportItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

}