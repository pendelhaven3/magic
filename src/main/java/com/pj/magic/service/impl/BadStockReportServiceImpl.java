package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.BadStockDao;
import com.pj.magic.dao.BadStockReportDao;
import com.pj.magic.dao.BadStockReportItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.BadStockReportItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.model.search.BadStockReportSearchCriteria;
import com.pj.magic.service.BadStockReportService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.StockQuantityConversionService;

@Service
public class BadStockReportServiceImpl implements BadStockReportService {
	
	@Autowired private BadStockReportDao badStockReportDao;
	@Autowired private BadStockReportItemDao badStockReportItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private BadStockDao badStockDao;
	@Autowired private LoginService loginService;
	@Autowired private StockQuantityConversionService stockQuantityConversionService;

	@Override
	public List<BadStockReport> getAllUnpostedBadStockReports() {
		BadStockReportSearchCriteria criteria = new BadStockReportSearchCriteria();
		criteria.setPosted(false);
		
		return badStockReportDao.search(criteria);
	}

	@Transactional
	@Override
	public void save(BadStockReport badStockReport) {
		badStockReportDao.save(badStockReport);
	}

	@Override
	public BadStockReport getBadStockReport(Long id) {
		BadStockReport report = badStockReportDao.get(id);
		if (report != null) {
			report.setItems(badStockReportItemDao.findAllByBadStockReport(report));
		}
		return report;
	}

	@Transactional
	@Override
	public void save(BadStockReportItem item) {
		badStockReportItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(BadStockReportItem item) {
		badStockReportItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(BadStockReport badStockReport) {
        BadStockReport updated = getBadStockReport(badStockReport.getId());
        
        if (updated.isPosted()) {
            throw new AlreadyPostedException("Bad Stock Report is already posted!");
        }
        
        if (!updated.hasItems()) {
        	throw new NoItemException();
        }
        
        List<BadStockReportItem> notEnoughQuantityItems = new ArrayList<>();
        
        for (BadStockReportItem item : updated.getItems()) {
        	if (item.isForceConversion()) {
            	notEnoughQuantityItems.add(item);
        	} else {
                Product product = productDao.get(item.getProduct().getId());
                if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
                	notEnoughQuantityItems.add(item);
                } else {
                    product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
                    productDao.updateAvailableQuantities(product);
                    
                    BadStock badStock = getOrCreateBadStock(item.getProduct());
                    badStock.addUnitQuantity(item.getUnit(), item.getQuantity());
                    badStockDao.save(badStock);
                }
        	}
        }
        
        postNotEnoughQuantityItems(badStockReport, notEnoughQuantityItems);
        
        updated.setPosted(true);
        updated.setPostDate(new Date());
        updated.setPostedBy(loginService.getLoggedInUser());
        badStockReportDao.save(updated);
	}
	
	private BadStock getOrCreateBadStock(Product product) {
        BadStock badStock = badStockDao.get(product.getId());
        if (badStock == null) {
            badStock = new BadStock(product);
        }
        return badStock;
    }

    private void postNotEnoughQuantityItems(BadStockReport badStockReport, List<BadStockReportItem> items) {
    	StockQuantityConversion conversion = new StockQuantityConversion();
    	conversion.setRemarks("FOR BAD STOCK REPORT #" + badStockReport.getBadStockReportNumber().toString());
    	stockQuantityConversionService.save(conversion);
    	
    	for (BadStockReportItem item : items) {
    		StockQuantityConversionItem conversionItem = new StockQuantityConversionItem();
    		conversionItem.setParent(conversion);
    		conversionItem.setProduct(item.getProduct());
    		conversionItem.setQuantity(1);
    		conversionItem.setFromUnit(getNextBiggerUnit(item.getProduct(), item.getUnit()));
    		conversionItem.setToUnit(item.getUnit());
    		stockQuantityConversionService.save(conversionItem);
    	}
    	
    	stockQuantityConversionService.post(conversion);
    	
        for (BadStockReportItem item : items) {
            Product product = productDao.get(item.getProduct().getId());
            if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
            	throw new RuntimeException("Cannot process more than 1 stock quantity conversion for same product in bad stock report");
            } else {
                product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
                productDao.updateAvailableQuantities(product);
                
                BadStock badStock = getOrCreateBadStock(item.getProduct());
                badStock.addUnitQuantity(item.getUnit(), item.getQuantity());
                badStockDao.save(badStock);
            }
        }
	}
	
	private String getNextBiggerUnit(Product product, String unit) {
		List<String> units = product.getUnits();
		int index = units.indexOf(unit);
		if (index == 0) {
			throw new NotEnoughStocksException("Not enough stock for " + product.getCode() + " - " + unit);
		}
		return units.get(index - 1);
	}

	@Override
	public List<BadStockReport> searchBadStockReports(BadStockReportSearchCriteria criteria) {
		return badStockReportDao.search(criteria);
	}

}
