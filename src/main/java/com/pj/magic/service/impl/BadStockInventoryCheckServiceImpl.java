package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.BadStockDao;
import com.pj.magic.dao.BadStockInventoryCheckDao;
import com.pj.magic.dao.BadStockInventoryCheckItemDao;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.model.BadStockInventoryCheckItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.search.BadStockSearchCriteria;
import com.pj.magic.service.BadStockInventoryCheckService;
import com.pj.magic.service.LoginService;

@Service
public class BadStockInventoryCheckServiceImpl implements BadStockInventoryCheckService {

	@Autowired private BadStockInventoryCheckDao badStockInventoryCheckDao;
	@Autowired private BadStockInventoryCheckItemDao badStockInventoryCheckItemDao;
	@Autowired private BadStockDao badStockDao;
	@Autowired private LoginService loginService;
	
	@Override
	public List<BadStockInventoryCheck> getAllBadStockInventoryChecks() {
		return badStockInventoryCheckDao.getAll();
	}

	@Transactional
	@Override
	public void save(BadStockInventoryCheckItem item) {
		badStockInventoryCheckItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(BadStockInventoryCheckItem item) {
		badStockInventoryCheckItemDao.delete(item);
	}

	@Override
	public BadStockInventoryCheck getBadStockInventoryCheck(Long id) {
		BadStockInventoryCheck inventoryCheck = badStockInventoryCheckDao.get(id);
		inventoryCheck.setItems(badStockInventoryCheckItemDao.findAllByBadStockInventoryCheck(inventoryCheck));
		return inventoryCheck;
	}

	@Transactional
	@Override
	public void save(BadStockInventoryCheck badStockInventoryCheck) {
		badStockInventoryCheckDao.save(badStockInventoryCheck);
	}

	@Transactional
	@Override
	public void post(BadStockInventoryCheck badStockInventoryCheck) {
		BadStockInventoryCheck updated = getBadStockInventoryCheck(badStockInventoryCheck.getId());
		for (BadStockInventoryCheckItem item : updated.getItems()) {
			BadStock badStock = badStockDao.get(item.getProduct().getId());
			int quantityChange = item.getQuantity() - badStock.getUnitQuantity(item.getUnit());
			badStock.addUnitQuantity(item.getUnit(), quantityChange);
			badStockDao.save(badStock);
			
			item.setQuantityChange(quantityChange);
			badStockInventoryCheckItemDao.save(item);
		}
		
		Map<Long, List<UnitQuantity>> toPurge = new HashMap<>();
		for (BadStock badStock : getAllBadStock()) {
			List<UnitQuantity> unitQuantities = new ArrayList<>();
			for (UnitQuantity unitQuantity : badStock.getUnitQuantities()) {
				if (unitQuantity.getQuantity() > 0) {
					unitQuantities.add(unitQuantity);
				}
			}
			toPurge.put(badStock.getProduct().getId(), unitQuantities);
		}
		for (BadStockInventoryCheckItem item : updated.getItems()) {
			if (toPurge.containsKey(item.getProduct().getId())) {
				List<UnitQuantity> unitQuantities = toPurge.get(item.getProduct().getId());
				ListIterator<UnitQuantity> iterator = unitQuantities.listIterator();
				while (iterator.hasNext()) {
					UnitQuantity unitQuantity = iterator.next();
					if (unitQuantity.getUnit().contentEquals(item.getUnit())) {
						iterator.remove();
					}
				}
			}
		}
		for (Long productId : toPurge.keySet()) {
			for (UnitQuantity unitQuantity : toPurge.get(productId)) {
				BadStock badStock = badStockDao.get(productId);
				badStock.addUnitQuantity(unitQuantity.getUnit(), -unitQuantity.getQuantity());
				badStockDao.save(badStock);
				
				BadStockInventoryCheckItem item = new BadStockInventoryCheckItem();
				item.setParent(badStockInventoryCheck);
				item.setProduct(new Product(productId));
				item.setUnit(unitQuantity.getUnit());
				item.setQuantity(0);
				item.setQuantityChange(-unitQuantity.getQuantity());
				badStockInventoryCheckItemDao.save(item);
			}
		}
		
        updated.setPosted(true);
        updated.setPostDate(new Date());
        updated.setPostedBy(loginService.getLoggedInUser());
        badStockInventoryCheckDao.save(updated);
	}

	private List<BadStock> getAllBadStock() {
		BadStockSearchCriteria criteria = new BadStockSearchCriteria();
		return badStockDao.search(criteria);
	}
	
}
