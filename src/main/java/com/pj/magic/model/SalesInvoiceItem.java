package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.util.Percentage;

public class SalesInvoiceItem implements Comparable<SalesInvoiceItem> {

	private Long id;
	private SalesInvoice parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal discount1 = BigDecimal.ZERO;
	private BigDecimal discount2 = BigDecimal.ZERO; 
	private BigDecimal discount3 = BigDecimal.ZERO;
	private BigDecimal flatRateDiscount = BigDecimal.ZERO;

	public SalesInvoiceItem() {
		// default constructor
	}
	
	public SalesInvoiceItem(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesInvoice getParent() {
		return parent;
	}

	public void setParent(SalesInvoice parent) {
		this.parent = parent;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getAmount() {
		return unitPrice.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public int compareTo(SalesInvoiceItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

	public BigDecimal getDiscount1() {
		return discount1;
	}

	public void setDiscount1(BigDecimal discount1) {
		this.discount1 = discount1;
	}

	public BigDecimal getDiscount2() {
		return discount2;
	}

	public void setDiscount2(BigDecimal discount2) {
		this.discount2 = discount2;
	}

	public BigDecimal getDiscount3() {
		return discount3;
	}

	public void setDiscount3(BigDecimal discount3) {
		this.discount3 = discount3;
	}

	public BigDecimal getFlatRateDiscount() {
		return flatRateDiscount;
	}

	public void setFlatRateDiscount(BigDecimal flatRateDiscount) {
		this.flatRateDiscount = flatRateDiscount;
	}

	public BigDecimal getDiscountedAmount() {
		return getAmount().subtract(getNetAmount());
	}

	public BigDecimal getNetAmount() {
		BigDecimal netAmount = getAmount();
		if (discount1 != null && !BigDecimal.ZERO.equals(discount1)) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount1).toDecimal()));
		}
		if (discount2 != null && !BigDecimal.ZERO.equals(discount2)) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount2).toDecimal()));
		}
		if (discount3 != null && !BigDecimal.ZERO.equals(discount3)) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount3).toDecimal()));
		}
		if (flatRateDiscount != null) {
			netAmount = netAmount.subtract(flatRateDiscount);
		}
		return netAmount;
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
        if (!(obj instanceof SalesInvoiceItem)) {
            return false;
        }
        SalesInvoiceItem other = (SalesInvoiceItem)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}