package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStock;
import com.pj.magic.model.Product;

public interface BadStockService {

    List<BadStock> getAllBadStocks();

    BadStock getBadStock(Product product);
    
}
