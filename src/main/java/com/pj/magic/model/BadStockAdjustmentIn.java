package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockAdjustmentIn {

    private Long id;
    private Long badStockAdjustmentInNumber;
    private boolean posted;
    private Date postDate;
    private User postedBy;
    private List<BadStockAdjustmentInItem> items = new ArrayList<>();
    private String remarks;
	private boolean pilferage = true;

    public String getStatus() {
        return posted ? "Posted" : "New";
    }
    
    public int getTotalItems() {
        return items.size();
    }
    
    public boolean hasItems() {
        return !items.isEmpty();
    }
    
    public boolean isNew() {
        return id == null;
    }
    
}
