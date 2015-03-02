package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PromoType {

	private static List<PromoType> promoTypes;
	
	static {
		promoTypes = new ArrayList<>();
		promoTypes.add(new PromoType(1L, "Buy X amount of Manufacturer products"));
		promoTypes.add(new PromoType(2L, "Buy X quantity of product"));
	}
	
	public static List<PromoType> getPromoTypes() {
		return promoTypes;
	}
	
	public static PromoType getPromoType(long id) {
		return promoTypes.get((int)id - 1);
	}
	
	private Long id;
	private String description;

	private PromoType(Long id, String description) {
		this.id = id;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
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
        if (!(obj instanceof PromoType)) {
            return false;
        }
        PromoType other = (PromoType)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	public boolean isType1() {
		return id.longValue() == 1L;
	}
	
	public boolean isType2() {
		return id.longValue() == 2L;
	}
	
}