package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.Constants;
import com.pj.magic.model.util.Percentage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalesComplianceProjectSalesInvoiceItem implements Comparable<SalesComplianceProjectSalesInvoiceItem>, Serializable {

    private Long id;
	private SalesComplianceProjectSalesInvoice parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private Integer originalQuantity;
	private BigDecimal unitPrice;
	private BigDecimal discount1 = BigDecimal.ZERO;
	private BigDecimal discount2 = BigDecimal.ZERO; 
	private BigDecimal discount3 = BigDecimal.ZERO;
	private BigDecimal flatRateDiscount = BigDecimal.ZERO;
	private BigDecimal cost;

	public SalesComplianceProjectSalesInvoiceItem(SalesInvoiceItem item) {
		product = item.getProduct();
		unit = item.getUnit();
		quantity = item.getQuantity();
		unitPrice = item.getUnitPrice();
		discount1 = item.getDiscount1();
		discount2 = item.getDiscount2();
		discount3 = item.getDiscount3();
		flatRateDiscount = item.getFlatRateDiscount();
		cost = item.getCost();
	}
	
	public BigDecimal getAmount() {
		return unitPrice.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public int compareTo(SalesComplianceProjectSalesInvoiceItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

	public BigDecimal getDiscountedAmount() {
		return getAmount().subtract(getNetAmount());
	}

	public BigDecimal getNetAmount() {
		BigDecimal netAmount = getAmount();
		if (discount1.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount1).toDecimal()));
		}
		if (discount2.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount2).toDecimal()));
		}
		if (discount3.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount3).toDecimal()));
		}
		if (flatRateDiscount.compareTo(Constants.ZERO) != 0) {
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
        if (!(obj instanceof SalesComplianceProjectSalesInvoiceItem)) {
            return false;
        }
        SalesComplianceProjectSalesInvoiceItem other = (SalesComplianceProjectSalesInvoiceItem)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public BigDecimal getNetCost() {
		return cost.multiply(new BigDecimal(quantity));
	}
	
	public BigDecimal getDiscountedUnitPrice() {
		BigDecimal price = unitPrice;
		if (discount1.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(price.multiply(new Percentage(discount1).toDecimal()));
		}
		if (discount2.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(price.multiply(new Percentage(discount2).toDecimal()));
		}
		if (discount3.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(price.multiply(new Percentage(discount3).toDecimal()));
		}
		if (flatRateDiscount.compareTo(Constants.ZERO) != 0) {
			price = price.subtract(flatRateDiscount.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
		}
		return price;
	}

	public BigDecimal getNetPrice() {
		return getDiscountedUnitPrice();
	}
	
	public BigDecimal getOriginalNetAmount() {
		BigDecimal netAmount = getOriginalAmount();
		if (discount1.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount1).toDecimal()));
		}
		if (discount2.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount2).toDecimal()));
		}
		if (discount3.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(netAmount.multiply(new Percentage(discount3).toDecimal()));
		}
		if (flatRateDiscount.compareTo(Constants.ZERO) != 0) {
			netAmount = netAmount.subtract(flatRateDiscount);
		}
		return netAmount;
	}
	
	public BigDecimal getOriginalAmount() {
		return unitPrice.multiply(new BigDecimal(originalQuantity)).setScale(2, RoundingMode.HALF_UP);
	}
	
}