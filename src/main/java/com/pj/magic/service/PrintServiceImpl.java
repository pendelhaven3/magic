package com.pj.magic.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.pj.magic.dao.SupplierDao;
import com.pj.magic.dao.UserDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.PrinterUtil;
import com.pj.magic.util.ReceivingReceiptReportUtil;
import com.pj.magic.util.ReportUtil;

@Service 
public class PrintServiceImpl implements PrintService {

	private static final Logger logger = LoggerFactory.getLogger(PrintServiceImpl.class);
	
	private static final int SALES_INVOICE_ITEMS_PER_PAGE = 44;
	private static final int PURCHASE_ORDER_ITEMS_PER_PAGE = 44;
	private static final int RECEIVING_RECEIPT_ITEMS_PER_PAGE = 44;
	private static final int PRICING_SCHEME_REPORT_LINES_PER_PAGE = 44;
	
	@Autowired private SupplierDao supplierDao;
	@Autowired private UserDao userDao;
	
	public PrintServiceImpl() {
		Velocity.setProperty("file.resource.loader.class", 
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init();
	}
	
	@Override
	public void print(SalesInvoice salesInvoice) {
		try {
			for (String printPage : generateReportAsString(salesInvoice)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void printReport(String templateName, Map<String, Object> reportData) {
		Template template = Velocity.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext(reportData);
		if (reportData.containsKey("reportUtil")) {
			context.put("report", reportData.get("reportUtil"));
		} else {
			context.put("report", ReportUtil.class);
		}
		template.merge(context, writer);
		try {
			PrinterUtil.print(writer.toString());
		} catch (PrintException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO: Modify other reports to follow this implementation structure
	@Override
	public void print(PurchaseOrder purchaseOrder) {
		try {
			for (String printPage : generateReportAsString(purchaseOrder)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails) {
		receivingReceipt.setSupplier(supplierDao.get(receivingReceipt.getSupplier().getId()));
		receivingReceipt.setReceivedBy(userDao.get(receivingReceipt.getReceivedBy().getId()));
		
		Collections.sort(receivingReceipt.getItems());
		
		String receivedDate = FormatterUtil.formatDate(receivingReceipt.getReceivedDate());
		
		List<List<ReceivingReceiptItem>> pageItems = Lists.partition(receivingReceipt.getItems(), 
				RECEIVING_RECEIPT_ITEMS_PER_PAGE);
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("receivingReceipt", receivingReceipt);
			reportData.put("items", pageItems.get(i));
			reportData.put("receivedDate", receivedDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			if (includeDiscountDetails) {
				reportData.put("reportUtil", ReceivingReceiptReportUtil.class);
				printReport("reports/receivingReceipt.vm", reportData);
			} else {
				printReport("reports/receivingReceipt-noDiscountDetails.vm", reportData);
			}
		}
	}

	@Override
	public List<String> generateReportAsString(PurchaseOrder purchaseOrder) {
		purchaseOrder.setSupplier(supplierDao.get(purchaseOrder.getSupplier().getId()));
		
		Collections.sort(purchaseOrder.getItems());
		
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<PurchaseOrderItem>> pageItems = Lists.partition(purchaseOrder.getItems(), 
				PURCHASE_ORDER_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("purchaseOrder", purchaseOrder);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/purchaseOrder.vm", reportData));
		}
		return printPages;
	}

	private String generateReportAsString(String templateName, Map<String, Object> reportData) {
		Template template = Velocity.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext(reportData);
		if (reportData.containsKey("reportUtil")) {
			context.put("report", reportData.get("reportUtil"));
		} else {
			context.put("report", ReportUtil.class);
		}
		template.merge(context, writer);
		return writer.toString();
	}

	@Override
	public List<String> generateReportAsString(PricingScheme pricingScheme, List<Product> products) {
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<Product>> pageItems = partitionPricingSchemeProducts(products);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("pricingScheme", pricingScheme);
			reportData.put("products", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/pricingScheme.vm", reportData));
		}
		return printPages;
	}
	
	private List<List<Product>> partitionPricingSchemeProducts(List<Product> products) {
		List<List<Product>> pageItems = new ArrayList<>();
		List<Product> pageItem = new ArrayList<>();
		int counter = 0;
		for (Product product : products) {
			int lineCount = 1 + product.getUnits().size();
			if (counter + lineCount > PRICING_SCHEME_REPORT_LINES_PER_PAGE) {
				pageItems.add(pageItem);
				pageItem = new ArrayList<>();
				counter = 0;
			}
			pageItem.add(product);
			counter += lineCount;
		}
		pageItems.add(pageItem);
		return pageItems;
	}

	@Override
	public void print(PricingScheme pricingScheme, List<Product> products) {
		try {
			for (String printPage : generateReportAsString(pricingScheme, products)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(List<String> printPages) {
		try {
			for (String printPage : printPages) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(SalesInvoice salesInvoice) {
		Collections.sort(salesInvoice.getItems());
		
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<SalesInvoiceItem>> pageItems = Lists.partition(salesInvoice.getItems(), 
				SALES_INVOICE_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("salesInvoice", salesInvoice);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/salesInvoice.vm", reportData));
		}
		return printPages;
	}
	
}
