package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.List;

import com.pj.magic.Constants;

public class PromoType1Rule {

	private Long id;
	private Promo parent;
	private BigDecimal targetAmount;
	private Manufacturer manufacturer;
	private Product product;
	private String unit;
	private Integer quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Promo getParent() {
		return parent;
	}

	public void setParent(Promo parent) {
		this.parent = parent;
	}

	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
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

	public boolean isNew() {
		return id == null;
	}

	public PromoRedemptionReward evaluate(List<SalesInvoice> salesInvoices) {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getSalesByManufacturer(manufacturer));
		}
		
		if (total.compareTo(targetAmount) != -1) {
			PromoRedemptionReward reward = new PromoRedemptionReward();
			reward.setProduct(product);
			reward.setUnit(unit);
			reward.setQuantity(total.divideToIntegralValue(targetAmount).intValue() * quantity);
			return reward;
		} else {
			return null;
		}
	}

	public BigDecimal getQualifyingAmount(SalesRequisition salesRequisition) {
		return salesRequisition.getSalesByManufacturer(manufacturer);
	}

}