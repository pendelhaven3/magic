package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadStockDao;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.BadStockSearchCriteria;
import com.pj.magic.service.BadStockService;

@Service
public class BadStockServiceImpl implements BadStockService {

    @Autowired
    private BadStockDao badStockDao;
    
    @Override
    public List<BadStock> getAllAvailableBadStocks() {
        BadStockSearchCriteria criteria = new BadStockSearchCriteria();
        criteria.setEmpty(false);
        
        return badStockDao.search(criteria);
    }

    @Override
    public BadStock getBadStock(Product product) {
        return badStockDao.get(product.getId());
    }

    @Override
    public List<BadStock> search(BadStockSearchCriteria criteria) {
        return badStockDao.search(criteria);
    }

	@Override
	public List<BadStock> searchAllBadStocksBySupplier(Supplier supplier, String codeOrDescription) {
		return badStockDao.searchAllBySupplier(supplier, codeOrDescription);
	}

}
