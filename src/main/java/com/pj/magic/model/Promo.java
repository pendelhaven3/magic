package com.pj.magic.model;

import java.math.BigDecimal;

public class Promo {

	private Long id;
	private Long promoNumber;
	private String name;
	
	// TODO: Move promo mechanics to separate class
	private BigDecimal targetAmount;
	private Manufacturer manufacturer;
	private PromoPrize prize;

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

}