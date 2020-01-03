package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStock;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.BadStockSearchCriteria;

public interface BadStockService {

    List<BadStock> getAllAvailableBadStocks();

    BadStock getBadStock(Product product);

    List<BadStock> search(BadStockSearchCriteria criteria);
    
}
