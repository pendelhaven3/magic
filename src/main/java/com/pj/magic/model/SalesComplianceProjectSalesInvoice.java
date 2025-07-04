package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesComplianceProjectSalesInvoice {

	@Id
	@GeneratedValue
	private Long id;
	
	private SalesComplianceProject salesComplianceProject;
	private SalesInvoice salesInvoice;
	private List<SalesComplianceProjectSalesInvoiceItem> items = new ArrayList<>();
	
	@Transient
	private BigDecimal vatAmount;
	
	public BigDecimal getTotalNetAmount() {
		return items.stream()
				.map(item -> item.getNetAmount())
				.reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
	}

	public BigDecimal getTotalOriginalNetAmount() {
		return items.stream()
				.map(item -> item.getOriginalNetAmount())
				.reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
	}

	public int getTotalQuantity() {
		return items.stream()
				.map(item -> item.getQuantity())
				.reduce(0, (x, y) -> x + y);
	}

	public BigDecimal getTotalAmount() {
		return items.stream()
				.map(item -> item.getAmount())
				.reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
	}

	public BigDecimal getTotalDiscounts() {
		return items.stream()
				.map(item -> item.getDiscountedAmount())
				.reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
	}

	public BigDecimal getVatableSales() {
		return getTotalNetAmount().subtract(vatAmount);
	}
	
}
