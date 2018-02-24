package com.pj.magic.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PromoType6Rule {

	private Long id;
	private Promo parent;
	private Integer targetQuantity;
    private Product product;
    private String unit;
    private Integer quantity;
	private List<PromoType6RulePromoProduct> promoProducts = new ArrayList<>();

	public PromoType6Rule() {
	}
	
	public PromoType6Rule(long id) {
		this.id = id;
	}

    public boolean isNew() {
        return id == null;
    }
    
    public boolean hasPromoProduct(Product product) {
        return promoProducts.stream().anyMatch(p -> p.getId().equals(product.getId()));
    }
    
    public PromoRedemptionReward evaluate(SalesInvoice salesInvoice) {
        int promoQuantity = 0;
        for (PromoType6RulePromoProduct promoProduct : promoProducts) {
            SalesInvoiceItem item = salesInvoice.findItemByProductAndUnit(promoProduct.getProduct(), Unit.CASE);
            if (item != null) {
                promoQuantity += item.getQuantity();
            }
        }
        return generatePromoRedemptionReward(promoQuantity);
    }
    
    public PromoRedemptionReward evaluate(SalesRequisition salesRequisition) {
        int promoQuantity = 0;
        for (PromoType6RulePromoProduct promoProduct : promoProducts) {
            SalesRequisitionItem item = salesRequisition.findItemByProductAndUnit(promoProduct.getProduct(), Unit.CASE);
            if (item != null) {
                promoQuantity += item.getQuantity();
            }
        }
        return generatePromoRedemptionReward(promoQuantity);
    }
    
    private PromoRedemptionReward generatePromoRedemptionReward(int promoQuantity) {
        if (promoQuantity >= targetQuantity) {
            PromoRedemptionReward reward = new PromoRedemptionReward();
            reward.setParent(new PromoRedemption());
            reward.getParent().setPromo(parent);
            reward.setProduct(product);
            reward.setUnit(unit);
            reward.setQuantity(promoQuantity / targetQuantity);
            return reward;
        } else {
            return null;
        }
    }

    private static final String MECHANICS_DESCRIPTION = "Buy {0} cases of selected products, get {1} {2} {3} free";
    
    public String getMechanicsDescription() {
        return MessageFormat.format(MECHANICS_DESCRIPTION, targetQuantity, quantity, unit, product.getDescription());
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

    public Integer getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Integer targetQuantity) {
        this.targetQuantity = targetQuantity;
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

    public List<PromoType6RulePromoProduct> getPromoProducts() {
		return promoProducts;
	}

	public void setPromoProducts(List<PromoType6RulePromoProduct> promoProducts) {
		this.promoProducts = promoProducts;
	}

}