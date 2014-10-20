package com.pj.magic;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductCategoryDao;
import com.pj.magic.dao.ProductPriceDao;
import com.pj.magic.dao.ProductSubcategoryDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ProductService;

public class Bootstrap {

	@Autowired private DataSource dataSource;
	@Autowired private ProductService productService;
	@Autowired private CustomerDao customerDao;
	@Autowired private TransactionTemplate transactionTemplate;
	@Autowired private ProductPriceDao productPriceDao;
	@Autowired private ProductCategoryDao productCategoryDao;
	@Autowired private ProductSubcategoryDao productSubcategoryDao;
	
	private ResourceBundle resources = ResourceBundle.getBundle("application");

	@PostConstruct
	public void initialize() throws Exception {
		runScriptFile("tables.sql", "data.sql");
		if (Boolean.parseBoolean(resources.getString("development"))) {
			loadProductCategoriesFromExcelFile();
			loadProductsFromExcelFile();
			loadCustomersFromExcelFile();
			runScriptFile("data2.sql");
		}
	}
	
	private void loadProductCategoriesFromExcelFile() throws Exception {
		try (
				InputStream in = getClass().getClassLoader().getResourceAsStream("data/product_categories.xls"); // TODO: study XSSF
			) {
				Workbook workbook = new HSSFWorkbook(in);
				Sheet sheet = workbook.getSheetAt(0);
				final Iterator<Row> rows = sheet.iterator();
				rows.next();
				
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						List<ProductCategory> categories = new ArrayList<>();
						while (rows.hasNext()) {
							Row row = rows.next();
							Cell cell = row.getCell(0);
							if (cell != null) {
								final String categoryName = cell.getStringCellValue();
								ProductCategory category = (ProductCategory)CollectionUtils.find(categories, new Predicate() {
									
									@Override
									public boolean evaluate(Object object) {
										return categoryName.equals(((ProductCategory)object).getName());
									}
								});
								if (category == null) {
									category = new ProductCategory();
									category.setName(categoryName.trim());
									productCategoryDao.save(category);
									categories.add(category);
								}
								ProductSubcategory subcategory = new ProductSubcategory();
								subcategory.setParent(category);
								subcategory.setName(row.getCell(1).getStringCellValue().trim());
								productSubcategoryDao.save(subcategory);
							}
						}
					}
				});
				
			}
	}

	private void runScriptFile(String... filenames) throws Exception {
		try (
			Connection conn = dataSource.getConnection();
		) {
			ScriptFileRunner.runScriptFiles(conn, filenames);
		}
	}
	
	private void loadProductsFromExcelFile() throws Exception {
		try (
			InputStream in = getClass().getClassLoader().getResourceAsStream("data/products.xls"); // TODO: study XSSF
		) {
			Workbook workbook = new HSSFWorkbook(in);
			Sheet sheet = workbook.getSheetAt(0);
			final Iterator<Row> rows = sheet.iterator();
			rows.next();
			rows.next();
			
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					while (rows.hasNext()) {
						Row row = rows.next();
						Cell cell = row.getCell(0);
						if (cell != null) {
							Product product = createProductFromRow(row);
							productService.save(product);
							productPriceDao.updateUnitPrices(product, new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
						}
					}
				}
			});
			
		}
	}
	
	private Product createProductFromRow(Row row) {
		Product product = new Product();
		product.setCode(row.getCell(0).getStringCellValue());
		product.setDescription(row.getCell(1).getStringCellValue());
		if (row.getCell(2) != null && "Y".equals(row.getCell(2).getStringCellValue())) {
			product.getUnits().add(Unit.CASE);
			product.addUnitQuantity(Unit.CASE, (int)row.getCell(6).getNumericCellValue());
			product.setUnitPrice(Unit.CASE, new BigDecimal(row.getCell(10).getNumericCellValue()));
			product.setFinalCost(Unit.CASE, new BigDecimal(row.getCell(14).getNumericCellValue()));
			product.setGrossCost(Unit.CASE, new BigDecimal(row.getCell(18).getNumericCellValue()));
			product.setUnitConversion(Unit.CASE, (int)row.getCell(22).getNumericCellValue());
		}
		if (row.getCell(3) != null && "Y".equals(row.getCell(3).getStringCellValue())) {
			product.getUnits().add(Unit.CARTON);
			product.addUnitQuantity(Unit.CARTON, (int)row.getCell(7).getNumericCellValue());
			product.setUnitPrice(Unit.CARTON, new BigDecimal(row.getCell(11).getNumericCellValue()));
			product.setFinalCost(Unit.CARTON, new BigDecimal(row.getCell(15).getNumericCellValue()));
			product.setGrossCost(Unit.CARTON, new BigDecimal(row.getCell(19).getNumericCellValue()));
			product.setUnitConversion(Unit.CARTON, (int)row.getCell(23).getNumericCellValue());
		}
		if (row.getCell(4) != null && "Y".equals(row.getCell(4).getStringCellValue())) {
			product.getUnits().add(Unit.DOZEN);
			product.addUnitQuantity(Unit.DOZEN, (int)row.getCell(8).getNumericCellValue());
			product.setUnitPrice(Unit.DOZEN, new BigDecimal(row.getCell(12).getNumericCellValue()));
			product.setFinalCost(Unit.DOZEN, new BigDecimal(row.getCell(16).getNumericCellValue()));
			product.setGrossCost(Unit.DOZEN, new BigDecimal(row.getCell(20).getNumericCellValue()));
			product.setUnitConversion(Unit.DOZEN, (int)row.getCell(24).getNumericCellValue());
		}
		if (row.getCell(5) != null && "Y".equals(row.getCell(5).getStringCellValue())) {
			product.getUnits().add(Unit.PIECES);
			product.addUnitQuantity(Unit.PIECES, (int)row.getCell(9).getNumericCellValue());
			product.setUnitPrice(Unit.PIECES, new BigDecimal(row.getCell(13).getNumericCellValue()));
			product.setFinalCost(Unit.PIECES, new BigDecimal(row.getCell(17).getNumericCellValue()));
			product.setGrossCost(Unit.PIECES, new BigDecimal(row.getCell(21).getNumericCellValue()));
			product.setUnitConversion(Unit.PIECES, (int)row.getCell(25).getNumericCellValue());
		}
		product.setActive(true);
		return product;
	}
	
	private void loadCustomersFromExcelFile() throws Exception {
		try (
			InputStream in = getClass().getClassLoader().getResourceAsStream("data/customers.xls"); // TODO: study XSSF
		) {
			Workbook workbook = new HSSFWorkbook(in);
			Sheet sheet = workbook.getSheetAt(0);
			final Iterator<Row> rows = sheet.iterator();
			rows.next();
			
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					while (rows.hasNext()) {
						Row row = rows.next();
						Cell cell = row.getCell(0);
						if (cell != null) {
							customerDao.save(createCustomerFromRow(row));
						}
					}
				}
			});
		}
	}
	
	private Customer createCustomerFromRow(Row row) {
		Customer customer = new Customer();
		customer.setCode(row.getCell(0).getStringCellValue());
		customer.setName(row.getCell(1).getStringCellValue());
		customer.setAddress(row.getCell(2).getStringCellValue());
		customer.setPaymentTerm(new PaymentTerm(1L));
		return customer;
	}
	
}
