package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Product implements Comparable<Product> {

	private Long id;
	private String code;
	private String description;
	private List<String> units = new ArrayList<>();
	private List<UnitPrice> unitPrices = new ArrayList<>();
	private List<UnitQuantity> unitQuantities = new ArrayList<>();
	private int minimumStockLevel = 0;
	private int maximumStockLevel = 0;
	private boolean active;
	private Manufacturer manufacturer;
	private ProductCategory category;
	private List<UnitConversion> unitConversions = new ArrayList<>();

	public Product() {
	}
	
	public Product(Long id) {
		this.id = id;
	}
	
	public boolean isValid() {
		return id != null;
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
		if (hasUnit(unit)) {
			unitPrices.add(new UnitPrice(unit, BigDecimal.ZERO));
		}
		return BigDecimal.ZERO;
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

	public void subtractUnitQuantity(String unit, Integer quantity) {
		for (UnitQuantity unitQuantity : unitQuantities) {
			if (unit.equals(unitQuantity.getUnit())) {
				unitQuantity.setQuantity(unitQuantity.getQuantity() - quantity);
			}
		}
	}
	
	public void addUnitQuantity(String unit, int quantity) {
		for (UnitQuantity unitQuantity : unitQuantities) {
			if (unit.equals(unitQuantity)) {
				unitQuantity.setQuantity(unitQuantity.getQuantity() + quantity);
				return;
			}
		}
		unitQuantities.add(new UnitQuantity(unit, quantity));
	}

	public void setUnitPrice(String unit, BigDecimal price) {
		for (UnitPrice unitPrice : unitPrices) {
			if (unit.equals(unitPrice.getUnit())) {
				unitPrice.setPrice(price);
				return;
			}
		}
		unitPrices.add(new UnitPrice(unit, price));
	}

	@Override
	public int compareTo(Product o) {
		return description.compareTo(o.getDescription());
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public int getMinimumStockLevel() {
		return minimumStockLevel;
	}

	public void setMinimumStockLevel(int minimumStockLevel) {
		this.minimumStockLevel = minimumStockLevel;
	}

	public int getMaximumStockLevel() {
		return maximumStockLevel;
	}

	public void setMaximumStockLevel(int maximumStockLevel) {
		this.maximumStockLevel = maximumStockLevel;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	
	public void addUnit(String unit) {
		if (!units.contains(unit)) {
			units.add(unit);
		}
	}

	public List<UnitConversion> getUnitConversions() {
		return unitConversions;
	}

	public void setUnitConversions(List<UnitConversion> unitConversions) {
		this.unitConversions = unitConversions;
	}

	public int getUnitConversion(String unit) {
		for (UnitConversion unitConversion : unitConversions) {
			if (unit.equals(unitConversion.getUnit())) {
				return unitConversion.getQuantity();
			}
		}
		return 0;
	}

	public void setUnitConversion(String unit, int quantity) {
		for (UnitConversion unitConversion : unitConversions) {
			if (unit.equals(unitConversion.getUnit())) {
				unitConversion.setQuantity(quantity);
				return;
			}
		}
		unitConversions.add(new UnitConversion(unit, quantity));
	}
	
}
