package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Supplier;

public class BadPurchaseReturnSearchCriteria {

	private Long badPurchaseReturnNumber;
	private Boolean posted;
	private Supplier supplier;
	private Date postDate;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
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

}