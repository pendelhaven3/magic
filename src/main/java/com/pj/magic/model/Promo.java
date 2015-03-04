package com.pj.magic.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.util.FormatterUtil;

public class Promo {

	private Long id;
	private Long promoNumber;
	private String name;
	private PromoType promoType;
	private boolean active;
	
	private PromoType1Rule promoType1Rule;
	private List<PromoType2Rule> promoType2Rules = new ArrayList<>();
	
	public Promo() {
		// default constructor
	}
	
	public Promo(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPromoNumber() {
		return promoNumber;
	}

	public void setPromoNumber(Long promoNumber) {
		this.promoNumber = promoNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMechanicsDescription() {
		if (promoType.isType1()) {
			String mechanics = "For every P{0} worth of {1} products, get {2} {3} {4}";
			return MessageFormat.format(mechanics,
					FormatterUtil.formatAmount(promoType1Rule.getTargetAmount()),
					promoType1Rule.getManufacturer().getName(),
					promoType1Rule.getQuantity().toString(),
					promoType1Rule.getUnit(),
					promoType1Rule.getProduct().getDescription());
		} else if (promoType.isType2()) {
			StringBuilder sb = new StringBuilder();
			for (PromoType2Rule rule : promoType2Rules) {
				String mechanics = "Buy {0} {1} {2}, get {3} {4} {5} free";
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(MessageFormat.format(mechanics, 
						rule.getPromoQuantity(),
						rule.getPromoUnit(),
						rule.getPromoProduct().getDescription(),
						rule.getFreeQuantity(),
						rule.getFreeUnit(),
						rule.getFreeProduct().getDescription()));
			}
			return sb.toString();
		} else {
			return null;
		}
	}

	public PromoType getPromoType() {
		return promoType;
	}

	public void setPromoType(PromoType promoType) {
		this.promoType = promoType;
	}

	public List<PromoType2Rule> getPromoType2Rules() {
		return promoType2Rules;
	}

	public void setPromoType2Rules(List<PromoType2Rule> promoType2Rules) {
		this.promoType2Rules = promoType2Rules;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<PromoRedemptionReward> evaluate(SalesRequisition salesRequisition) {
		List<PromoRedemptionReward> rewards = new ArrayList<>();
		for (PromoType2Rule rule : promoType2Rules) {
			SalesRequisitionItem item = salesRequisition.findItemByProductAndUnit(
					rule.getPromoProduct(), rule.getPromoUnit());
			if (item != null) {
				PromoRedemptionReward reward = rule.evaluate(item);
				if (reward != null) {
					rewards.add(reward);
				}
			}
		}
		return rewards;
	}

	public List<PromoRedemptionReward> evaluate(SalesInvoice salesInvoice) {
		List<PromoRedemptionReward> rewards = new ArrayList<>();
		for (PromoType2Rule rule : promoType2Rules) {
			SalesInvoiceItem item = salesInvoice.findItemByProductAndUnit(
					rule.getPromoProduct(), rule.getPromoUnit());
			if (item != null) {
				PromoRedemptionReward reward = rule.evaluate(item);
				if (reward != null) {
					rewards.add(reward);
				}
			}
		}
		return rewards;
	}

	public PromoType1Rule getPromoType1Rule() {
		return promoType1Rule;
	}

	public void setPromoType1Rule(PromoType1Rule promoType1Rule) {
		this.promoType1Rule = promoType1Rule;
	}

}