package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalesComplianceProject {

	@Id
	@GeneratedValue
	private Long id;
	
	private Long salesComplianceProjectNumber;
	private String name;
	private Date startDate;
	private Date endDate;
	private BigDecimal targetAmount;
	private List<SalesComplianceProjectSalesInvoice> salesInvoices = new ArrayList<>();
	
	public SalesComplianceProject(Long id) {
		this.id = id;
	}

	public BigDecimal getTotalAmount() {
		return salesInvoices.stream()
				.map(salesInvoice -> salesInvoice.getTotalNetAmount())
				.reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
	}

	public BigDecimal getTotalOriginalAmount() {
		return salesInvoices.stream()
				.map(salesInvoice -> salesInvoice.getTotalOriginalNetAmount())
				.reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
	}

	public BigDecimal getRemainingAmount() {
		BigDecimal totalAmount = getTotalAmount();
		return totalAmount.compareTo(targetAmount) > 0 ? totalAmount.subtract(targetAmount) : BigDecimal.ZERO;
	}
	
}
