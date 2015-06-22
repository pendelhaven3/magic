package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.Constants;

public class PromoType3Rule {

	private Long id;
	private Promo parent;
	private BigDecimal targetAmount;
	private Product freeProduct;
	private String freeUnit;
	private Integer freeQuantity;
	private List<PromoType3RulePromoProduct> promoProducts = new ArrayList<>();
	private PricingScheme pricingScheme;

	public PromoType3Rule() {
		// default constructor
	}
	
	public PromoType3Rule(long id) {
		this.id = id;
	}
	
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

	public Product getFreeProduct() {
		return freeProduct;
	}

	public void setFreeProduct(Product freeProduct) {
		this.freeProduct = freeProduct;
	}

	public String getFreeUnit() {
		return freeUnit;
	}

	public void setFreeUnit(String freeUnit) {
		this.freeUnit = freeUnit;
	}

	public Integer getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(Integer freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	public List<PromoType3RulePromoProduct> getPromoProducts() {
		return promoProducts;
	}

	public void setPromoProducts(List<PromoType3RulePromoProduct> promoProducts) {
		this.promoProducts = promoProducts;
	}

	public boolean isNew() {
		return id == null;
	}

	public boolean hasPromoProduct(Product product) {
		for (PromoType3RulePromoProduct promoProduct : promoProducts) {
			if (promoProduct.getProduct().equals(product)) {
				return true;
			}
		}
		return false;
	}

	public PromoRedemptionReward evaluate(List<SalesInvoice> salesInvoices) {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(getQualifyingAmount(salesInvoice));
		}
		
		if (total.compareTo(targetAmount) == -1) {
			return null;
		} else {
			PromoRedemptionReward reward = new PromoRedemptionReward();
			reward.setProduct(freeProduct);
			reward.setUnit(freeUnit);
			reward.setQuantity(freeQuantity * total.divideToIntegralValue(targetAmount).intValue());
			return reward;
		}
	}

	public BigDecimal getQualifyingAmount(SalesInvoice salesInvoice) {
		BigDecimal total = Constants.ZERO;
		for (PromoType3RulePromoProduct promoProduct : promoProducts) {
			for (SalesInvoiceItem item : salesInvoice.getItems()) {
				if (item.getProduct().getId().equals(promoProduct.getProduct().getId())) {
					total = total.add(item.getAmount());
				}
			}
		}
		return total;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}
	
}