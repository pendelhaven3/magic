package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PromoRedemption {

	private Long id;
	private Promo promo;
	private Long promoRedemptionNumber;
	private Customer customer;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private List<PromoRedemptionSalesInvoice> salesInvoices = new ArrayList<>();

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

}