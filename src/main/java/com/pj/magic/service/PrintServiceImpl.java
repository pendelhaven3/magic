package com.pj.magic.service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintException;

import org.apache.commons.lang.StringUtils;
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
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesInvoiceReport;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.model.report.PaidSalesInvoicesReport;
import com.pj.magic.model.report.PostedSalesAndProfitReport;
import com.pj.magic.model.report.UnpaidSalesInvoicesReport;
import com.pj.magic.model.util.InventoryCheckReportType;
import com.pj.magic.model.util.InventoryCheckSummaryPrintItem;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.PaymentReportUtil;
import com.pj.magic.util.PriceListReportUtil;
import com.pj.magic.util.PrinterUtil;
import com.pj.magic.util.ReceivingReceiptReportUtil;
import com.pj.magic.util.ReportUtil;
import com.pj.magic.util.PostedSalesAndProfitReportReportUtil;

@Service 
public class PrintServiceImpl implements PrintService {

	private static final Logger logger = LoggerFactory.getLogger(PrintServiceImpl.class);
	
	private static final int SALES_INVOICE_ITEMS_PER_PAGE = 44;
	private static final int STOCK_QUANTITY_CONVERSION_ITEMS_PER_PAGE = 44;
	private static final int PURCHASE_ORDER_ITEMS_PER_PAGE = 44;
	private static final int RECEIVING_RECEIPT_ITEMS_PER_PAGE = 44;
	private static final int PRICING_SCHEME_REPORT_LINES_PER_PAGE = 44;
	private static final int INVENTORY_CHECK_SUMMARY_ITEMS_PER_PAGE = 52;
	public static final int INVENTORY_REPORT_COLUMNS_PER_LINE = 84;
	public static final int INVENTORY_REPORT_COMPLETE_COLUMNS_PER_LINE = 96;
	private static final int SALES_INVOICE_BIR_FORM_ITEMS_PER_PAGE = 13;
	private static final int AREA_INVENTORY_REPORT_ITEMS_PER_PAGE = 44;
	private static final int SALES_INVOICE_REPORT_ITEMS_PER_PAGE = 30;
	private static final int ADJUSTMENT_OUT_ITEMS_PER_PAGE = 44;
	private static final int ADJUSTMENT_IN_ITEMS_PER_PAGE = 44;
	private static final int UNPAID_SALES_INVOICE_ITEMS_PER_PAGE = 44;
	private static final int PAID_SALES_INVOICE_ITEMS_PER_PAGE = 44;
	private static final int PRICE_LIST_ITEMS_PER_PAGE = 46;
	private static final int POSTED_SALES_AND_PROFIT_REPORT_ITEMS_PER_PAGE = 44;
	
	public static final int PRICE_LIST_CHARACTERS_PER_LINE = 84;
	public static final int SALES_INVOICE_REPORT_COST_PROFIT_CHARACTERS_PER_LINE = 113;
	public static final int POSTED_SALES_AND_PROFIT_REPORT_CHARACTERS_PER_LINE = 115;
	
	private static final int LEFT_PADDING_SIZE_FOR_CONDENSED_FONT = 25;
	public static final String LEFT_PADDING_FOR_CONDENSED_FONT =
			StringUtils.repeat(" ", LEFT_PADDING_SIZE_FOR_CONDENSED_FONT);
	
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

	@Override
	public void print(PurchaseOrder purchaseOrder, boolean includeCost) {
		try {
			for (String printPage : generateReportAsString(purchaseOrder, includeCost)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails) {
		try {
			for (String printPage : generateReportAsString(receivingReceipt, includeDiscountDetails)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PurchaseOrder purchaseOrder, boolean includeCost) {
		purchaseOrder.setSupplier(supplierDao.get(purchaseOrder.getSupplier().getId()));
		
		String currentDate = FormatterUtil.formatDate(new Date());
		
		String reportFile = (includeCost) ? "purchaseOrder-withCost.vm" : "purchaseOrder.vm";
		
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
			printPages.add(generateReportAsString("reports/" + reportFile, reportData));
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
	public List<String> generateReportAsString(PricingScheme pricingScheme, List<Product> products,
			boolean includeCosts) {
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<Product>> pageItems = null;
		if (includeCosts) {
			pageItems = partitionPricingSchemeProducts(products);
		} else {
			pageItems = Lists.partition(products, PRICE_LIST_ITEMS_PER_PAGE);
		}
		
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("pricingScheme", pricingScheme);
			reportData.put("products", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			if (includeCosts) {
				printPages.add(generateReportAsString("reports/pricingScheme.vm", reportData));
			} else {
				reportData.put("reportUtil", PriceListReportUtil.class);
				printPages.add(generateReportAsString("reports/pricingScheme-pricesOnly.vm", reportData));
			}
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
	public void print(PricingScheme pricingScheme, List<Product> products, boolean includeCosts) {
		try {
			for (String printPage : generateReportAsString(pricingScheme, products, includeCosts)) {
				if (includeCosts) {
					PrinterUtil.print(printPage);
				} else {
					PrinterUtil.printWithCondensedFont(printPage);
				}
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
	public void printWithCondensedFont(List<String> printPages) {
		try {
			for (String printPage : printPages) {
				PrinterUtil.printWithCondensedFont(printPage);
//				PrinterUtil.print(addLeftPaddingForCondensedFont(printPage));
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(SalesInvoice salesInvoice) {
		Collections.sort(salesInvoice.getItems());
		
		String transactionDate = FormatterUtil.formatDate(salesInvoice.getTransactionDate());
		
		List<List<SalesInvoiceItem>> pageItems = Lists.partition(salesInvoice.getItems(), 
				SALES_INVOICE_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("salesInvoice", salesInvoice);
			reportData.put("items", pageItems.get(i));
			reportData.put("transactionDate", transactionDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/salesInvoice.vm", reportData));
		}
		return printPages;
	}

	@Override
	public List<String> generateReportAsString(ReceivingReceipt receivingReceipt,
			boolean includeDiscountDetails) {
		receivingReceipt.setSupplier(supplierDao.get(receivingReceipt.getSupplier().getId()));
		receivingReceipt.setReceivedBy(userDao.get(receivingReceipt.getReceivedBy().getId()));
		
		String receivedDate = FormatterUtil.formatDate(receivingReceipt.getReceivedDate());
		
		List<List<ReceivingReceiptItem>> pageItems = Lists.partition(receivingReceipt.getItems(), 
				RECEIVING_RECEIPT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
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
				printPages.add(generateReportAsString("reports/receivingReceipt.vm", reportData));
			} else {
				printPages.add(
						generateReportAsString("reports/receivingReceipt-noDiscountDetails.vm", reportData));
			}
		}
		return printPages;
	}

	@Override
	public void print(StockQuantityConversion stockQuantityConversion) {
		try {
			for (String printPage : generateReportAsString(stockQuantityConversion)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(StockQuantityConversion stockQuantityConversion) {
		Collections.sort(stockQuantityConversion.getItems());
		
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<StockQuantityConversionItem>> pageItems = Lists.partition(stockQuantityConversion.getItems(), 
				STOCK_QUANTITY_CONVERSION_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("stockQuantityConversion", stockQuantityConversion);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/stockQuantityConversion.vm", reportData));
		}
		return printPages;
	}

	@Override
	public List<String> generateReportAsString(InventoryCheck inventoryCheck, InventoryCheckReportType reportType) {
		List<InventoryCheckSummaryItem> items = null;
		String title = null;
		BigDecimal totalValue = null;
		
		switch (reportType) {
		case BEGINNING_INVENTORY:
			items = inventoryCheck.getSummaryItemsWithBeginningInventoriesOnly();
			title = "BEGINNING INVENTORY";
			totalValue = inventoryCheck.getTotalBeginningValue();
			break;
		case ACTUAL_COUNT:
			items = inventoryCheck.getSummaryItemsWithActualCountOnly();
			title = "ACTUAL COUNT";
			totalValue = inventoryCheck.getTotalActualValue();
			break;
		case COMPLETE:
			items = inventoryCheck.getNonEmptySummaryItems();
			title = "INVENTORY CHECK";
			totalValue = inventoryCheck.getTotalActualValue();
			break;
		}
		
		Collections.sort(items);
		
		String inventoryDate = FormatterUtil.formatDate(inventoryCheck.getInventoryDate());
		
		List<InventoryCheckSummaryPrintItem> printItems = new ArrayList<>();
		for (InventoryCheckSummaryItem item : items) {
			printItems.add(new InventoryCheckSummaryPrintItem(item, reportType));
		}
		
		List<List<InventoryCheckSummaryPrintItem>> pageItems = Lists.partition(printItems, 
				INVENTORY_CHECK_SUMMARY_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("inventoryCheck", inventoryCheck);
			reportData.put("reportType", title);
			reportData.put("items", pageItems.get(i));
			reportData.put("inventoryDate", inventoryDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			reportData.put("totalValue", totalValue);
			if (reportType == InventoryCheckReportType.COMPLETE) {
				printPages.add(generateReportAsString("reports/inventoryReport-complete.vm", reportData));
			} else {
				printPages.add(generateReportAsString("reports/inventoryReport.vm", reportData));
			}
		}
		return printPages;
	}

	@Override
	public void print(InventoryCheck inventoryCheck, InventoryCheckReportType reportType) {
		try {
			for (String printPage : generateReportAsString(inventoryCheck, reportType)) {
				PrinterUtil.print(addLeftPaddingForCondensedFont(printPage));
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String addLeftPaddingForCondensedFont(String printPage) {
		return LEFT_PADDING_FOR_CONDENSED_FONT +
				printPage.replaceAll("\n", "\n" + LEFT_PADDING_FOR_CONDENSED_FONT);
	}

	@Override
	public void printBirForm(SalesInvoice salesInvoice) {
		Collections.sort(salesInvoice.getItems());
		
		String transactionDate = FormatterUtil.formatDate(salesInvoice.getTransactionDate());
		
		List<List<SalesInvoiceItem>> pageItems = Lists.partition(salesInvoice.getItems(), 
				SALES_INVOICE_BIR_FORM_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("salesInvoice", salesInvoice);
			reportData.put("items", pageItems.get(i));
			reportData.put("transactionDate", transactionDate);
			reportData.put("fillerLines", createFillerLines(pageItems.get(i).size()));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/salesInvoiceBirForm.vm", reportData));
		}
		
		try {
			for (String printPage : printPages) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private List<String> createFillerLines(int referenceSize) {
		List<String> fillerLines = new ArrayList<>();
		int numberOfLines = SALES_INVOICE_BIR_FORM_ITEMS_PER_PAGE - referenceSize;
		for (int i = 0; i < numberOfLines; i++) {
			fillerLines.add("");
		}
		return fillerLines;
	}

	@Override
	public List<String> generateReportAsString(AreaInventoryReport areaInventoryReport) {
		Collections.sort(areaInventoryReport.getItems());
		
		String inventoryDate = FormatterUtil.formatDate(areaInventoryReport.getParent().getInventoryDate());
		
		List<List<AreaInventoryReportItem>> pageItems = Lists.partition(areaInventoryReport.getItems(), 
				AREA_INVENTORY_REPORT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("areaInventoryReport", areaInventoryReport);
			if (areaInventoryReport.getArea() != null) {
				reportData.put("area", areaInventoryReport.getArea().getName());
			} else {
				reportData.put("area", "");
			}
			reportData.put("items", pageItems.get(i));
			reportData.put("inventoryDate", inventoryDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/areaInventoryReport.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(AreaInventoryReport areaInventoryReport) {
		try {
			for (String printPage : generateReportAsString(areaInventoryReport)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(SalesInvoiceReport salesInvoiceReport, boolean includeCostAndProfit) {
		Collections.sort(salesInvoiceReport.getSalesInvoices(), new Comparator<SalesInvoice>() {

			@Override
			public int compare(SalesInvoice o1, SalesInvoice o2) {
				return o1.getSalesInvoiceNumber().compareTo(o2.getSalesInvoiceNumber());
			}
		});
		
		String reportDate = FormatterUtil.formatDate(salesInvoiceReport.getReportDate());
		
		List<List<SalesInvoice>> pageItems = Lists.partition(salesInvoiceReport.getSalesInvoices(), 
				SALES_INVOICE_REPORT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("salesInvoiceReport", salesInvoiceReport);
			reportData.put("salesInvoices", pageItems.get(i));
			reportData.put("reportDate", reportDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			reportData.put("reportUtil", PostedSalesAndProfitReportReportUtil.class);
			if (includeCostAndProfit) {
				printPages.add(generateReportAsString("reports/salesInvoiceReport-withCostAndProfit.vm", 
						reportData));
			} else {
				printPages.add(generateReportAsString("reports/salesInvoiceReport.vm", reportData));
			}
		}
		return printPages;
	}

	@Override
	public void print(SalesInvoiceReport salesInvoiceReport, boolean includeCostAndProfit) {
		try {
			for (String printPage : generateReportAsString(salesInvoiceReport, includeCostAndProfit)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(AdjustmentOut adjustmentOut) {
		Collections.sort(adjustmentOut.getItems());
		
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<AdjustmentOutItem>> pageItems = Lists.partition(adjustmentOut.getItems(), 
				ADJUSTMENT_OUT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("adjustmentOut", adjustmentOut);
			reportData.put("currentDate", currentDate);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/adjustmentOut.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(AdjustmentOut adjustmentOut) {
		try {
			for (String printPage : generateReportAsString(adjustmentOut)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(AdjustmentIn adjustmentIn) {
		Collections.sort(adjustmentIn.getItems());
		
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<AdjustmentInItem>> pageItems = Lists.partition(adjustmentIn.getItems(), 
				ADJUSTMENT_IN_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("adjustmentIn", adjustmentIn);
			reportData.put("currentDate", currentDate);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/adjustmentIn.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(AdjustmentIn adjustmentIn) {
		try {
			for (String printPage : generateReportAsString(adjustmentIn)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(Payment payment) {
		List<String> printPages = new ArrayList<>();
		Map<String, Object> reportData = new HashMap<>();
		reportData.put("payment", payment);
		reportData.put("reportUtil", PaymentReportUtil.class);
		printPages.add(generateReportAsString("reports/payment.vm", reportData));
		return printPages;
	}

	@Override
	public void print(Payment payment) {
		try {
			for (String printPage : generateReportAsString(payment)) {
				PrinterUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(UnpaidSalesInvoicesReport report) {
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<SalesInvoice>> pageItems = Lists.partition(report.getSalesInvoices(), 
				UNPAID_SALES_INVOICE_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("totalAmount", report.getTotalAmount());
			reportData.put("currentDate", currentDate);
			reportData.put("salesInvoices", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/unpaidSalesInvoices.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(UnpaidSalesInvoicesReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void print(PaidSalesInvoicesReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				PrinterUtil.print(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> generateReportAsString(PaidSalesInvoicesReport report) {
		String paymentDate = FormatterUtil.formatDate(report.getPaymentDate());
		
		List<List<PaymentSalesInvoice>> pageItems = Lists.partition(report.getPaymentSalesInvoices(), 
				PAID_SALES_INVOICE_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("paymentDate", paymentDate);
			reportData.put("totalAmountDue", report.getTotalAmountDue());
			reportData.put("paymentSalesInvoices", pageItems.get(i));
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printPages.add(generateReportAsString("reports/paidSalesInvoices.vm", reportData));
		}
		return printPages;
	}

	@Override
	public List<String> generateReportAsString(PostedSalesAndProfitReport report) {
		List<List<SalesInvoice>> pageItems = Lists.partition(report.getSalesInvoices(), 
				POSTED_SALES_AND_PROFIT_REPORT_ITEMS_PER_PAGE);
		List<String> printPages = new ArrayList<>();
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("charsPerLine", POSTED_SALES_AND_PROFIT_REPORT_CHARACTERS_PER_LINE);
			reportData.put("salesReport", report);
			reportData.put("salesInvoices", pageItems.get(i));
			reportData.put("customer", report.getCustomerName());
			reportData.put("transactionDate", report.getTransactionDate());
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			reportData.put("reportUtil", PostedSalesAndProfitReportReportUtil.class);
			printPages.add(generateReportAsString("reports/postedSalesAndProfitReport.vm", reportData));
		}
		return printPages;
	}

	@Override
	public void print(PostedSalesAndProfitReport report) {
		try {
			for (String printPage : generateReportAsString(report)) {
				PrinterUtil.printWithCondensedFont(printPage);
			}
		} catch (PrintException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}