package com.pj.magic.model.search;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockAdjustmentOutSearchCriteria {

    private Boolean posted;
    private Long badStockAdjustmentOutNumber;
    private Date postDateFrom;
    private Date postDateTo;

}
