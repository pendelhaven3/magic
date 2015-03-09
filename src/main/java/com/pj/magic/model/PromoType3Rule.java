package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PromoType3Rule {

	private Long id;
	private Promo parent;
	private BigDecimal targetAmount;
	private Product freeProduct;
	private String freeUnit;
	private Integer freeQuantity;
	private List<PromoType3RulePromoProduct> promoProducts = new ArrayList<>();

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

}