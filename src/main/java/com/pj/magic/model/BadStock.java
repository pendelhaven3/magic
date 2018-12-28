package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

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
    
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<UnitQuantity> getUnitQuantities() {
        return unitQuantities;
    }

    public void setUnitQuantities(List<UnitQuantity> unitQuantities) {
        this.unitQuantities = unitQuantities;
    }

}
