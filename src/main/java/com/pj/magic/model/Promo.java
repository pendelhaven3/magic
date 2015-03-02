package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Promo {

	private Long id;
	private Long promoNumber;
	private String name;
	private PromoType promoType;
	
	// TODO: Move promo mechanics to separate class
	private BigDecimal targetAmount;
	private Manufacturer manufacturer;
	private PromoPrize prize;
	
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

	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PromoPrize getPrize() {
		return prize;
	}

	public void setPrize(PromoPrize prize) {
		this.prize = prize;
	}

	public String getMechanicsDescription() {
		return "INSERT PROMO MECHANICS HERE";
		
//		String mechanics = "For every P{0} worth of {1} products, get {2} {3} {4}";
//		return MessageFormat.format(mechanics,
//				FormatterUtil.formatAmount(targetAmount),
//				manufacturer.getName(),
//				prize.getQuantity().toString(),
//				prize.getUnit(),
//				prize.getProduct().getDescription());
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

}