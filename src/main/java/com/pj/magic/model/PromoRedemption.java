package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class PromoRedemption {

	private Long id;
	private Promo promo;
	private Long promoRedemptionNumber;
	private Customer customer;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private List<PromoRedemptionSalesInvoice> salesInvoices = new ArrayList<>();
	private List<PromoRedemptionReward> rewards = new ArrayList<>();
	private Integer prizeQuantity;

	public PromoRedemption() {
		// default constructor
	}
	
	public PromoRedemption(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Promo getPromo() {
		return promo;
	}

	public void setPromo(Promo promo) {
		this.promo = promo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Long getPromoRedemptionNumber() {
		return promoRedemptionNumber;
	}

	public void setPromoRedemptionNumber(Long promoRedemptionNumber) {
		this.promoRedemptionNumber = promoRedemptionNumber;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public List<PromoRedemptionSalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}

	public void setSalesInvoices(List<PromoRedemptionSalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
	}

	public String getStatus() {
		return (posted) ? "Posted" : "New";
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		if (promo.getPromoType().isType1()) {
			PromoType1Rule rule = promo.getPromoType1Rule();
			for (PromoRedemptionSalesInvoice salesInvoice : salesInvoices) {
				total = total.add(salesInvoice.getSalesInvoice().getSalesByManufacturer(
						rule.getManufacturer()));
			}
		}
		return total;
	}

	// TODO: Remove PROMO_REDEMPTION.PRIZE_QUANTITY
	public Integer getPrizeQuantity() {
		if (promo.getPromoType().isType2()) {
			return 0;
		}
		
		if (posted) {
			return prizeQuantity;
		} else {
			PromoType1Rule rule = promo.getPromoType1Rule();
			return getTotalAmount().divideToIntegralValue(rule.getTargetAmount()).intValue()
					* rule.getQuantity();
		}
	}

	public void setPrizeQuantity(Integer prizeQuantity) {
		this.prizeQuantity = prizeQuantity;
	}

	public int getFreeQuantity(PromoType2Rule rule) {
		return getTotalQuantityByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit())
				/ rule.getPromoQuantity() * rule.getFreeQuantity();
	}
	
	public int getTotalQuantityByProductAndUnit(Product product, String unit) {
		int total = 0;
		for (PromoRedemptionSalesInvoice salesInvoice : salesInvoices) {
			SalesInvoiceItem item = salesInvoice.getSalesInvoice().findItemByProductAndUnit(product, unit);
			if (item != null) {
				total += item.getQuantity();
			}
		}
		return total;
	}

	public List<PromoRedemptionReward> getRewards() {
		return rewards;
	}

	public void setRewards(List<PromoRedemptionReward> rewards) {
		this.rewards = rewards;
	}

	public PromoRedemptionReward getRewardByRule(PromoType2Rule rule) {
		for (PromoRedemptionReward reward : rewards) {
			if (reward.getProduct().equals(rule.getFreeProduct()) 
					&& reward.getUnit().equals(rule.getFreeUnit())) {
				return reward;
			}
		}
		return null;
	}

}