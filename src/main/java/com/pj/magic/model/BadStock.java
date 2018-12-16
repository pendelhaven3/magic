package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

public class BadStock {

    private Product product;
    private List<UnitQuantity> unitQuantities = new ArrayList<>();

    public Integer getUnitQuantity(String unit) {
        for (UnitQuantity unitQuantity : unitQuantities) {
            if (unitQuantity.getUnit().equals(unit)) {
                return unitQuantity.getQuantity();
            }
        }
        return null;
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
