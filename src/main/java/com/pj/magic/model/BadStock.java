package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStock implements Comparable<BadStock> {

    private Product product;
    private List<UnitQuantity> unitQuantities = new ArrayList<>();

    public BadStock() {
        // default constructor
    }
    
    public BadStock(Product product) {
        this.product = product;
        for (String unit : product.getUnits()) {
            unitQuantities.add(new UnitQuantity(unit, 0));
        }
    }
    
    public Integer getUnitQuantity(String unit) {
        for (UnitQuantity unitQuantity : unitQuantities) {
            if (unitQuantity.getUnit().equals(unit)) {
                return unitQuantity.getQuantity();
            }
        }
        return null;
    }
    
    public void addUnitQuantity(String unit, Integer quantity) {
        for (UnitQuantity unitQuantity : unitQuantities) {
            if (unitQuantity.getUnit().equals(unit)) {
                unitQuantity.setQuantity(unitQuantity.getQuantity() + quantity);
                return;
            }
        }
        
        unitQuantities.add(new UnitQuantity(unit, quantity));
    }
    
    @Override
    public int compareTo(BadStock o) {
        return product.compareTo(o.getProduct());
    }
    
    public boolean hasAvailableUnitQuantity(String unit, int quantity) {
        return getUnitQuantity(unit) >= quantity;
    }
    
    public boolean hasAvailableUnitQuantity(String unit) {
        return hasAvailableUnitQuantity(unit, 1);
    }
    
    public String getUnitQuantityForDisplay(String unit) {
        Integer quantity = getUnitQuantity(unit);
        if (quantity != null) {
            return String.valueOf(quantity);
        } else {
            return hasUnit(unit) ? String.valueOf("0") : "-";
        }
    }

    public boolean hasUnit(String unit) {
        for (UnitQuantity unitQuantity : unitQuantities) {
            if (unit.equals(unitQuantity.getUnit())) {
                return true;
            }
        }
        return false;
    }

}
