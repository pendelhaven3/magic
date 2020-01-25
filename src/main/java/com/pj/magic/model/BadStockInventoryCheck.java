package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockInventoryCheck {

    private Long id;
    private Long badStockInventoryCheckNumber;
    private boolean posted;
    private Date postDate;
    private User postedBy;
    private List<BadStockInventoryCheckItem> items = new ArrayList<>();
    private String remarks;

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
