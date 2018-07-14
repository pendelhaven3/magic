package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Transient;

public class ScheduledPriceChange {

    private Long id;
    private Date effectiveDate;
    private Product product;
    private PricingScheme pricingScheme;
    private boolean applied;
    private User createBy;
    private Date createDate;
    
    @Transient
    private boolean selected;
    
    public BigDecimal getUnitPrice(String unit) {
        return product.getUnitPrice(unit);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PricingScheme getPricingScheme() {
        return pricingScheme;
    }

    public void setPricingScheme(PricingScheme pricingScheme) {
        this.pricingScheme = pricingScheme;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
}
