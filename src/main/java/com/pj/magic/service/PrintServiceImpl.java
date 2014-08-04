package com.pj.magic.service;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.PrinterUtil;
import com.pj.magic.util.ReportUtil;

@Service 
public class PrintServiceImpl implements PrintService {

	private static final int SALES_INVOICE_ITEMS_PER_PAGE = 44;
	
	public PrintServiceImpl() {
		Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init();
	}
	
	@Override
	public void print(SalesInvoice salesInvoice) {
		Collections.sort(salesInvoice.getItems());
		
		String currentDate = FormatterUtil.formatDate(new Date());
		
		List<List<SalesInvoiceItem>> pageItems = Lists.partition(salesInvoice.getItems(), SALES_INVOICE_ITEMS_PER_PAGE);
		for (int i = 0; i < pageItems.size(); i++) {
			Map<String, Object> reportData = new HashMap<>();
			reportData.put("salesInvoice", salesInvoice);
			reportData.put("items", pageItems.get(i));
			reportData.put("currentDate", currentDate);
			reportData.put("currentPage", i + 1);
			reportData.put("totalPages", pageItems.size());
			reportData.put("isLastPage", (i + 1) == pageItems.size());
			printReport("reports/salesInvoice.vm", reportData);
		}
	}

	private void printReport(String templateName, Map<String, Object> reportData) {
		Template template = Velocity.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext(reportData);
		context.put("report", ReportUtil.class);
		template.merge(context, writer);
//		System.out.println(writer.toString());
		try {
			PrinterUtil.print(writer.toString());
		} catch (PrintException e) {
			throw new RuntimeException(e);
		}
	}
	
}
