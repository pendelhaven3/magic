package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStock;
import com.pj.magic.model.search.BadStockSearchCriteria;

public interface BadStockDao {

    List<BadStock> getAll();

    BadStock get(Long id);

    void save(BadStock badStock);

    List<BadStock> search(BadStockSearchCriteria criteria);
    
}
