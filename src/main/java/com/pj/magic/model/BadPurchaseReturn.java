package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

/**
 * Purchase Return - Bad Stock
 * 
 * @author PJ Miranda
 *
 */
public class BadPurchaseReturn {

	private Long id;
	
	/**
	 * PRBS No. (Purchase Return Bad Stock No.)
	 */
	private Long badPurchaseReturnNumber;
	
	private Supplier supplier;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private String remarks;
	
	private List<BadPurchaseReturnItem> items = new ArrayList<>();

	public BadPurchaseReturn() {
		// default constructor
	}
	
	public BadPurchaseReturn(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBadPurchaseReturnNumber() {
		return badPurchaseReturnNumber;
	}

	public void setBadPurchaseReturnNumber(Long badPurchaseReturnNumber) {
		this.badPurchaseReturnNumber = badPurchaseReturnNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<BadPurchaseReturnItem> getItems() {
		return items;
	}

	public void setItems(List<BadPurchaseReturnItem> items) {
		this.items = items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		return (posted) ? "Posted" : "New";
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
		for (BadPurchaseReturnItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}