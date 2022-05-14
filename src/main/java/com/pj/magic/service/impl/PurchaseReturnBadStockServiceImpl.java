package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.Constants;
import com.pj.magic.dao.BadStockDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PurchaseReturnBadStockDao;
import com.pj.magic.dao.PurchaseReturnBadStockItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.model.search.BadStockSearchCriteria;
import com.pj.magic.model.search.PurchaseReturnBadStockSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PurchaseReturnBadStockService;
import com.pj.magic.service.ReceivingReceiptService;

@Service
public class PurchaseReturnBadStockServiceImpl implements PurchaseReturnBadStockService {

	@Autowired private PurchaseReturnBadStockDao purchaseReturnBadStockDao;
	@Autowired private PurchaseReturnBadStockItemDao purchaseReturnBadStockItemDao;
	@Autowired private LoginService loginService;
	@Autowired private ProductDao productDao;
	@Autowired private SystemDao systemDao;
	
	@Autowired
	private BadStockDao badStockDao;
	
	@Autowired
	private ReceivingReceiptService receivingReceiptService;
	
	@Transactional
	@Override
	public void save(PurchaseReturnBadStock purchaseReturnBadStock) {
		if (purchaseReturnBadStock.getId() == null && purchaseReturnBadStock.getReceivedDate() == null) {
			purchaseReturnBadStock.setReceivedDate(new Date());
		}
		purchaseReturnBadStockDao.save(purchaseReturnBadStock);
	}

	@Override
	public PurchaseReturnBadStock getPurchaseReturnBadStock(long id) {
		PurchaseReturnBadStock purchaseReturnBadStock = purchaseReturnBadStockDao.get(id);
		if (purchaseReturnBadStock != null) {
			purchaseReturnBadStock.setItems(purchaseReturnBadStockItemDao.findAllByPurchaseReturnBadStock(purchaseReturnBadStock));
			for (PurchaseReturnBadStockItem item : purchaseReturnBadStock.getItems()) {
				item.setProduct(productDao.get(item.getProduct().getId()));
			}
		}
		return purchaseReturnBadStock;
	}

	@Override
	public List<PurchaseReturnBadStock> getAllUnpaidPurchaseReturnBadStocks() {
		PurchaseReturnBadStockSearchCriteria criteria = new PurchaseReturnBadStockSearchCriteria();
		criteria.setPaid(false);
		
		return search(criteria);
	}

	@Override
	public List<PurchaseReturnBadStock> search(PurchaseReturnBadStockSearchCriteria criteria) {
		List<PurchaseReturnBadStock> purchaseReturnBadStocks = purchaseReturnBadStockDao.search(criteria);
		for (PurchaseReturnBadStock purchaseReturnBadStock : purchaseReturnBadStocks) {
			purchaseReturnBadStock.setItems(purchaseReturnBadStockItemDao.findAllByPurchaseReturnBadStock(purchaseReturnBadStock));
		}
		return purchaseReturnBadStocks;
	}

	@Transactional
	@Override
	public void save(PurchaseReturnBadStockItem item) {
		purchaseReturnBadStockItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(PurchaseReturnBadStockItem item) {
		purchaseReturnBadStockItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(PurchaseReturnBadStock purchaseReturnBadStock) {
		PurchaseReturnBadStock updated = getPurchaseReturnBadStock(purchaseReturnBadStock.getId());
		
		if (updated.isPosted()) {
		    throw new AlreadyPostedException("PRBS No. " + updated.getPurchaseReturnBadStockNumber().toString() + " is already posted");
		}
		
		for (PurchaseReturnBadStockItem item : purchaseReturnBadStockItemDao.findAllByPurchaseReturnBadStock(purchaseReturnBadStock)) {
            BadStock badStock = badStockDao.get(item.getProduct().getId());
            if (badStock.getUnitQuantity(item.getUnit()) < item.getQuantity()) {
                throw new NotEnoughStocksException("Not enough bad stock: " + item.getProduct().getDisplayCodeAndDescription());
            }
            badStock.addUnitQuantity(item.getUnit(), -1 * item.getQuantity());
            badStockDao.save(badStock);
		}
		
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		purchaseReturnBadStockDao.save(updated);
	}

	@Override
	public PurchaseReturnBadStock findPurchaseReturnBadStockByPurchaseReturnBadStockNumber(
			long purchaseReturnBadStockNumber) {
		PurchaseReturnBadStock purchaseReturnBadStock = 
				purchaseReturnBadStockDao.findByPurchaseReturnBadStockNumber(purchaseReturnBadStockNumber);
		if (purchaseReturnBadStock != null) {
			purchaseReturnBadStock.setItems(purchaseReturnBadStockItemDao.findAllByPurchaseReturnBadStock(
					purchaseReturnBadStock));
		}
		return purchaseReturnBadStock;
	}

	@Transactional
    @Override
    public void addAllBadStockForSupplier(PurchaseReturnBadStock purchaseReturnBadStock) {
        for (BadStock badStock : findAllNonEmptyBadStockBySupplier(purchaseReturnBadStock.getSupplier())) {
            for (String unit : Unit.values()) {
                if (badStock.hasUnit(unit)) {
                    Integer quantity = badStock.getUnitQuantity(unit);
                    if (quantity > 0) {
                        PurchaseReturnBadStockItem item = purchaseReturnBadStock.findItemByProductAndUnit(badStock.getProduct(), unit);
                        if (item == null) {
                            item = new PurchaseReturnBadStockItem();
                            item.setParent(purchaseReturnBadStock);
                            item.setProduct(badStock.getProduct());
                            item.setUnit(unit);
                            item.setQuantity(quantity);
                            item.setUnitCost(getDefaultUnitCost(badStock.getProduct(), unit, purchaseReturnBadStock.getSupplier()));
                            purchaseReturnBadStockItemDao.save(item);
                        } else if (item.getQuantity() != quantity) {
                            item.setQuantity(quantity);
                            purchaseReturnBadStockItemDao.save(item);
                        }
                        // if (item.getQuantity() == quantity) then no update is needed
                    }
                }
            }
        }
    }

    private List<BadStock> findAllNonEmptyBadStockBySupplier(Supplier supplier) {
        BadStockSearchCriteria criteria = new BadStockSearchCriteria();
        criteria.setSupplier(supplier);
        criteria.setEmpty(false);
        
        return badStockDao.search(criteria);
    }
    
    private BigDecimal getDefaultUnitCost(Product product, String unit, Supplier supplier) {
        ReceivingReceiptItem receivingReceiptItem =
                receivingReceiptService.findMostRecentReceivingReceiptItem(supplier, product);
        if (receivingReceiptItem != null) {
            return receivingReceiptItem.getProduct().getFinalCost(unit);
        } else {
            return Constants.ZERO;
        }
    }
    
    @Transactional
    @Override
    public void deleteAllItems(PurchaseReturnBadStock purchaseReturnBadStock) {
        purchaseReturnBadStockItemDao.deleteAllByPurchaseReturnBadStock(purchaseReturnBadStock);
    }

    @Transactional
	@Override
	public void markAsPaid(PurchaseReturnBadStock purchaseReturnBadStock) {
		PurchaseReturnBadStock updated = getPurchaseReturnBadStock(purchaseReturnBadStock.getId());
		
		if (updated.isPaid()) {
		    throw new AlreadyPostedException("PRBS No. " + updated.getPurchaseReturnBadStockNumber().toString() + " is already paid");
		}
		
		updated.setPaid(true);
		updated.setPaidDate(systemDao.getCurrentDateTime());
		updated.setPaidBy(loginService.getLoggedInUser());
		purchaseReturnBadStockDao.save(updated);
	}

}