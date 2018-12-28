package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BadStockAdjustmentIn {

    private Long id;
    private Long badStockAdjustmentInNumber;
    private boolean posted;
    private Date postDate;
    private User postedBy;
    private List<AdjustmentInItem> items = new ArrayList<>();
    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBadStockAdjustmentInNumber() {
        return badStockAdjustmentInNumber;
    }

    public void setBadStockAdjustmentInNumber(Long badStockAdjustmentInNumber) {
        this.badStockAdjustmentInNumber = badStockAdjustmentInNumber;
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

    public List<AdjustmentInItem> getItems() {
        return items;
    }

    public void setItems(List<AdjustmentInItem> items) {
        this.items = items;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
