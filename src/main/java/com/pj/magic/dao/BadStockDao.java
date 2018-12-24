package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStock;

public interface BadStockDao {

    List<BadStock> getAll();

    BadStock get(Long id);

    void save(BadStock badStock);
    
}
