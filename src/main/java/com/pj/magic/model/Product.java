package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Product {

	private Long id;
	private String code;
	private String description;
	private List<String> units = new ArrayList<>();
	private List<UnitPrice> unitPrices = new ArrayList<>();
	private List<UnitQuantity> unitQuantities = new ArrayList<>();

	public Product() {
	}
	
	public Product(Long id) {
		this.id = id;
	}
	
	public boolean isValid() {
		return id != null && id.intValue() != 0;
	}
	
	public boolean hasUnit(String unit) {
		return units.contains(unit);
	}
	
	public int getUnitQuantity(String unit) {
		for (UnitQuantity unitQuantity : unitQuantities) {
			if (unit.equals(unitQuantity.getUnit())) {
				return unitQuantity.getQuantity();
			}
		}
		return 0;
	}
	
	public BigDecimal getUnitPrice(String unit) {
		for (UnitPrice unitPrice : unitPrices) {
			if (unit.equals(unitPrice.getUnit())) {
				return unitPrice.getPrice();
			}
		}
		return null;
	}
	
	public boolean hasAvailableUnitQuantity(String unit, int quantity) {
		return getUnitQuantity(unit) >= quantity;
	}
	
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

	public List<String> getUnits() {
		return units;
	}

	public void setUnits(List<String> units) {
		this.units = units;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<UnitPrice> getUnitPrices() {
		return unitPrices;
	}

	public void setUnitPrices(List<UnitPrice> unitPrices) {
		this.unitPrices = unitPrices;
	}

	public List<UnitQuantity> getUnitQuantities() {
		return unitQuantities;
	}
	
	public void setUnitQuantities(List<UnitQuantity> unitQuantities) {
		this.unitQuantities = unitQuantities;
	}
	
}
