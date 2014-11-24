package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PaymentSalesInvoice {

	private Long id;
	private Payment parent;
	private SalesInvoice salesInvoice;
	private BigDecimal adjustmentAmount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Payment getParent() {
		return parent;
	}

	public void setParent(Payment parent) {
		this.parent = parent;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public BigDecimal getAmountDue() {
		BigDecimal amount = getSalesInvoice().getTotalNetAmount();
		if (adjustmentAmount != null) {
			amount = amount.subtract(adjustmentAmount);
		}
		return amount;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof PaymentSalesInvoice)) {
            return false;
        }
        PaymentSalesInvoice other = (PaymentSalesInvoice)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}