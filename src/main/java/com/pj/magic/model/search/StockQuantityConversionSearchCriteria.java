package com.pj.magic.model.search;

import java.util.Date;


public class StockQuantityConversionSearchCriteria {

	private Long stockQuantityConversionNumber;
	private Boolean posted;
	private Date postDate;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Long getStockQuantityConversionNumber() {
		return stockQuantityConversionNumber;
	}

	public void setStockQuantityConversionNumber(Long stockQuantityConversionNumber) {
		this.stockQuantityConversionNumber = stockQuantityConversionNumber;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

}