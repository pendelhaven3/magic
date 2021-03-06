package com.pj.magic.httpserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.FormatterUtil;

public class HttpServerHandler extends AbstractHandler {

	@Autowired private CustomerService customerService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PaymentService paymentService;
	
	private Gson gson = new Gson();
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		switch (target) {
        case "/hello":
            response.getWriter().print("Hello World");
            response.flushBuffer();
            break;
		case "/salesInvoice/search":
			searchSalesInvoices(request, response);
			break;
		case "/salesInvoice/markAsPaid":
			markSalesInvoicesAsPaid(request, response);
			break;
		default:
			returnStatusNotFound(response);
		}
	}

	private void returnStatusNotFound(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.NOT_FOUND_404);
		response.flushBuffer();
	}

	private void markSalesInvoicesAsPaid(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		List<Long> salesInvoiceNumbers = new ArrayList<>();
		for (String requestSalesInvoiceNumber : request.getParameterValues("salesInvoiceNumber")) {
			salesInvoiceNumbers.add(Long.valueOf(requestSalesInvoiceNumber));
		}
		paymentService.markAsPaidByPayroll(salesInvoiceNumbers);
		
		response.setStatus(HttpStatus.OK_200);
		response.flushBuffer();
	}

	private void searchSalesInvoices(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<Map<String, String>> salesInvoiceJsons = new ArrayList<>();
		for (SalesInvoice salesInvoice : searchSalesInvoices(request)) {
			Map<String, String> salesInvoiceJson = new HashMap<>();
			salesInvoiceJson.put("salesInvoiceNumber", salesInvoice.getSalesInvoiceNumber().toString());
			salesInvoiceJson.put("transactionDate", FormatterUtil.formatDate(salesInvoice.getTransactionDate()));
			salesInvoiceJson.put("amount", salesInvoice.getTotalNetAmount().toPlainString());
			salesInvoiceJsons.add(salesInvoiceJson);
		}
		response.setContentType("application/json");
		response.getWriter().print(gson.toJson(salesInvoiceJsons));
		response.flushBuffer();
	}

	private List<SalesInvoice> searchSalesInvoices(HttpServletRequest request) {
		String customerCode = request.getParameter("customerCode");
		Boolean paid = Boolean.valueOf(StringUtils.defaultString(request.getParameter("paid"), "false"));
		
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setCustomer(customerService.findCustomerByCode(customerCode));
		criteria.setPaid(paid);
		
		return salesInvoiceService.search(criteria);
	}
	
}
