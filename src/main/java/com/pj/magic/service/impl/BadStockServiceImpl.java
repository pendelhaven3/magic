package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadStockDao;
import com.pj.magic.model.BadStock;
import com.pj.magic.service.BadStockService;

@Service
public class BadStockServiceImpl implements BadStockService {

    @Autowired
    private BadStockDao badStockDao;
    
    @Override
    public List<BadStock> getAllBadStocks() {
        return badStockDao.getAll();
    }

}
