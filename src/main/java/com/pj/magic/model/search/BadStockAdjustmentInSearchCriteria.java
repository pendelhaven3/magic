package com.pj.magic.model.search;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockAdjustmentInSearchCriteria {

    private Boolean posted;
    private Long badStockAdjustmentInNumber;
    private Date postDateFrom;
    private Date postDateTo;

}
