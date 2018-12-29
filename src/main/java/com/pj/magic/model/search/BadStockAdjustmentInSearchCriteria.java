package com.pj.magic.model.search;

import java.util.Date;

public class BadStockAdjustmentInSearchCriteria {

    private Boolean posted;
    private Long badStockAdjustmentInNumber;
    private Date postDateFrom;
    private Date postDateTo;

    public Boolean getPosted() {
        return posted;
    }

    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    public Long getBadStockAdjustmentInNumber() {
        return badStockAdjustmentInNumber;
    }

    public void setBadStockAdjustmentInNumber(Long badStockAdjustmentInNumber) {
        this.badStockAdjustmentInNumber = badStockAdjustmentInNumber;
    }

    public Date getPostDateFrom() {
        return postDateFrom;
    }

    public void setPostDateFrom(Date postDateFrom) {
        this.postDateFrom = postDateFrom;
    }

    public Date getPostDateTo() {
        return postDateTo;
    }

    public void setPostDateTo(Date postDateTo) {
        this.postDateTo = postDateTo;
    }
    
}
