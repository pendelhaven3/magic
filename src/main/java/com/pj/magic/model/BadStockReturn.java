package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class BadStockReturn {

	private Long id;
	private Long badStockReturnNumber;
	private Customer customer;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private List<BadStockReturnItem> items = new ArrayList<>();

	public BadStockReturn() {
		// default constructor
	}
	
	public BadStockReturn(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBadStockReturnNumber() {
		return badStockReturnNumber;
	}

	public void setBadStockReturnNumber(Long badStockReturnNumber) {
		this.badStockReturnNumber = badStockReturnNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<BadStockReturnItem> getItems() {
		return items;
	}

	public void setItems(List<BadStockReturnItem> items) {
		this.items = items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		return posted ? "Posted" : "New";
	}

	public int getTotalItems() {
		return items.size();
	}

	public boolean hasItems() {
		return !items.isEmpty();
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

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (BadStockReturnItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}
	
}