package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.Constants;

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
	private List<UnitCost> unitCosts = new ArrayList<>();

	public Product() {
	}
	
	public Product(Long id) {
		this.id = id;
	}
	
	// TODO: Remove this
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
			if (unit.equals(unitQuantity.getUnit())) {
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

	public List<UnitCost> getUnitCosts() {
		return unitCosts;
	}

	public void setUnitCosts(List<UnitCost> unitCosts) {
		this.unitCosts = unitCosts;
	}

	public void setGrossCost(String unit, BigDecimal grossCost) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				unitCost.setGrossCost(grossCost);
				return;
			}
		}
		unitCosts.add(new UnitCost(unit, grossCost, BigDecimal.ZERO));
	}

	public BigDecimal getGrossCost(String unit) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				return unitCost.getGrossCost();
			}
		}
		if (hasUnit(unit)) {
			unitCosts.add(new UnitCost(unit));
		}
		return BigDecimal.ZERO;
	}

	public void setFinalCost(String unit, BigDecimal finalCost) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				unitCost.setFinalCost(finalCost);
				return;
			}
		}
		unitCosts.add(new UnitCost(unit, BigDecimal.ZERO, finalCost));
	}

	public BigDecimal getFinalCost(String unit) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				return unitCost.getFinalCost();
			}
		}
		if (hasUnit(unit)) {
			unitCosts.add(new UnitCost(unit));
		}
		return BigDecimal.ZERO;
	}
	
	public boolean isMaxUnit(String unit) {
		String maxUnit = Collections.max(units, new Comparator<String>() {

			@Override
			public int compare(String unit1, String unit2) {
				return Unit.compare(unit1, unit2);
			}
		});
		return maxUnit.equals(unit);
	}
	
	public int getSuggestedOrder(String unit) {
		int currentQuantity = getUnitQuantity(unit);
		if (currentQuantity > maximumStockLevel) {
			return 0;
		} else {
			return maximumStockLevel - currentQuantity;
		}
	}

	public BigDecimal getPercentProfit(String unit) {
		return getFlatProfit(unit)
				.divide(getGrossCost(unit), 4, RoundingMode.HALF_UP).multiply(Constants.ONE_HUNDRED);
	}

	public BigDecimal getFlatProfit(String unit) {
		return getUnitPrice(unit).subtract(getFinalCost(unit));
	}

	public void setPercentProfit(String unit, BigDecimal profit) {
		BigDecimal unitPrice = getGrossCost(unit).multiply(profit.divide(Constants.ONE_HUNDRED, 4, RoundingMode.HALF_UP))
				.setScale(2, RoundingMode.HALF_UP).add(getFinalCost(unit));
		setUnitPrice(unit, unitPrice);
	}

	public void setFlatProfit(String unit, BigDecimal profit) {
		BigDecimal unitPrice = getFinalCost(unit).add(profit);
		setUnitPrice(unit, unitPrice);
	}

	public void autoCalculatePricesOfSmallerUnits() {
		Collections.sort(units, new Comparator<String>() {

			@Override
			public int compare(String unit1, String unit2) {
				return Unit.compare(unit1, unit2) * -1;
			}
		});
		
		String maxUnit = units.get(0);
		BigDecimal priceOfMaxUnit = getUnitPrice(maxUnit);
		int conversionOfMaxUnit = getUnitConversion(maxUnit);
		for (int i = 1; i < units.size(); i++) {
			String unit = units.get(i);
			BigDecimal unitPrice = priceOfMaxUnit.divide(new BigDecimal(conversionOfMaxUnit / getUnitConversion(unit)), 
					2, RoundingMode.HALF_UP);
			setUnitPrice(unit, unitPrice);
		}
	}
	
}
