package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromoType6RulePromoProduct {

	private Long id;
	private PromoType6Rule parent;
	private Product product;
	private String unit;

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PromoType6RulePromoProduct)) {
            return false;
        }
        PromoType6RulePromoProduct other = (PromoType6RulePromoProduct)obj;     
        return new EqualsBuilder()
            .append(id, other.getId())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .toHashCode();
    }
	
}