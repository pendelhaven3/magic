package com.pj.magic.model;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Product {

	private String code;
	private String description;
	private List<Unit> units;

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(code)
			.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof Product)) {
            return false;
        }
        Product other = (Product)obj;		
		return new EqualsBuilder()
			.append(code, other.getCode())
			.isEquals();
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

}
