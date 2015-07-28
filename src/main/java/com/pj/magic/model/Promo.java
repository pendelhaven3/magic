package com.pj.magic.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.Constants;
import com.pj.magic.util.FormatterUtil;

public class Promo {

	private Long id;
	private Long promoNumber;
	private String name;
	private PromoType promoType;
	private boolean active;
	private Date startDate;
	private Date endDate;
	
	private PricingScheme pricingScheme;

	private PromoType1Rule promoType1Rule;
	private List<PromoType2Rule> promoType2Rules;
	private PromoType3Rule promoType3Rule;
	private PromoType4Rule promoType4Rule;

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
		String mechanics = null;

		switch (promoType) {
		case PROMO_TYPE_1:
			mechanics = "For every P{0} worth of {1} products, get {2} {3} {4}";
			return MessageFormat.format(mechanics, FormatterUtil
					.formatAmount(promoType1Rule.getTargetAmount()),
					promoType1Rule.getManufacturer().getName(), promoType1Rule
							.getQuantity().toString(),
					promoType1Rule.getUnit(), promoType1Rule.getProduct()
							.getDescription());
		case PROMO_TYPE_2:
			StringBuilder sb = new StringBuilder();
			for (PromoType2Rule rule : promoType2Rules) {
				mechanics = "Buy {0} {1} {2}, get {3} {4} {5} free";
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(MessageFormat.format(mechanics, rule
						.getPromoQuantity(), rule.getPromoUnit(), rule
						.getPromoProduct().getDescription(), rule
						.getFreeQuantity(), rule.getFreeUnit(), rule
						.getFreeProduct().getDescription()));
			}
			return sb.toString();
		case PROMO_TYPE_3:
			mechanics = "Buy {0} worth of participating products, get {1} {2} {3} free";
			return MessageFormat.format(mechanics, FormatterUtil
					.formatAmount(promoType3Rule.getTargetAmount()),
					promoType3Rule.getFreeQuantity().toString(), promoType3Rule
							.getFreeUnit(), promoType3Rule.getFreeProduct()
							.getDescription());
		default:
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

	public List<PromoRedemptionReward> evaluateForRewards(SalesRequisition salesRequisition) {
		List<PromoRedemptionReward> rewards = new ArrayList<>();
		for (PromoType2Rule rule : promoType2Rules) {
			SalesRequisitionItem item = salesRequisition.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit());
			if (item != null) {
				PromoRedemptionReward reward = rule.evaluate(item);
				if (reward != null) {
					rewards.add(reward);
				}
			}
		}
		return rewards;
	}

	public List<PromoRedemptionReward> evaluateForRewards(SalesInvoice salesInvoice) {
		List<PromoRedemptionReward> rewards = new ArrayList<>();
		for (PromoType2Rule rule : promoType2Rules) {
			SalesInvoiceItem item = salesInvoice.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit());
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

	public PromoType3Rule getPromoType3Rule() {
		return promoType3Rule;
	}

	public void setPromoType3Rule(PromoType3Rule promoType3Rule) {
		this.promoType3Rule = promoType3Rule;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	public boolean checkIfEligible(SalesRequisition salesRequisition) {
		if (pricingScheme != null) {
			return pricingScheme.equals(salesRequisition.getPricingScheme());
		}
		return true;
	}

	public boolean checkIfEligible(SalesInvoice salesInvoice) {
		if (pricingScheme != null) {
			return pricingScheme.equals(salesInvoice.getPricingScheme());
		}
		return true;
	}

	public PromoType4Rule getPromoType4Rule() {
		return promoType4Rule;
	}

	public void setPromoType4Rule(PromoType4Rule promoType4Rule) {
		this.promoType4Rule = promoType4Rule;
	}

	public boolean hasStartDate() {
		return startDate != null;
	}

	public boolean hasEndDate() {
		return endDate != null;
	}
	
	public boolean isPromoType4() {
		return promoType.isType4();
	}

	public List<AvailedPromoPointsItem> evaluateForPoints(List<SalesInvoice> salesInvoices) {
		List<AvailedPromoPointsItem> items = new ArrayList<>();
		Collection<Product> promoProducts = Collections2.transform(promoType4Rule.getPromoProducts(),
				new Function<PromoType4RulePromoProduct, Product>() {

					@Override
					public Product apply(PromoType4RulePromoProduct input) {
						return input.getProduct();
					}
				}
		);
		
		for (SalesInvoice salesInvoice : salesInvoices) {
			BigDecimal qualifyingAmount = Constants.ZERO;
			for (SalesInvoiceItem item : salesInvoice.getItems()) {
				if (promoProducts.contains(item.getProduct())) {
					qualifyingAmount = qualifyingAmount.add(item.getNetAmount());
				}
			}
			
			int points = qualifyingAmount.divideToIntegralValue(promoType4Rule.getTargetAmount()).intValue();
			
			AvailedPromoPointsItem item = new AvailedPromoPointsItem();
			item.setPromo(this);
			item.setSalesInvoiceNumber(salesInvoice.getSalesInvoiceNumber());
			item.setTransactionDate(salesInvoice.getTransactionDate());
			item.setNetAmount(qualifyingAmount);
			item.setPoints(points);
			items.add(item);
		}
		
		return items;
	}

	public AvailedPromoPointsItem evaluateForPoints(SalesInvoice salesInvoice) {
		return evaluateForPoints(Arrays.asList(salesInvoice)).get(0);
	}
	
}