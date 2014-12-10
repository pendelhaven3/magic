package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class SalesReturn {

	private Long id;
	private Long salesReturnNumber;
	private SalesInvoice salesInvoice;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private List<SalesReturnItem> items = new ArrayList<>();

	public SalesReturn() {
		// default constructor
	}
	
	public SalesReturn(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSalesReturnNumber() {
		return salesReturnNumber;
	}

	public void setSalesReturnNumber(Long salesReturnNumber) {
		this.salesReturnNumber = salesReturnNumber;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	public List<SalesReturnItem> getItems() {
		return items;
	}

	public void setItems(List<SalesReturnItem> items) {
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

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (SalesReturnItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
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
	
	public BigDecimal getTotalNetCost() {
		BigDecimal total = Constants.ZERO;
		for (SalesReturnItem item : items) {
			total = total.add(item.getNetCost());
		}
		return total;
	}
	
	public BigDecimal getTotalNetProfit() {
		return getTotalAmount().subtract(getTotalNetCost());
	}
	
}