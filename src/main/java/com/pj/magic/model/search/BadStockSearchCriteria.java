package com.pj.magic.model.search;

import com.pj.magic.model.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockSearchCriteria {

    private Supplier supplier;
    private Boolean empty;
    
}
