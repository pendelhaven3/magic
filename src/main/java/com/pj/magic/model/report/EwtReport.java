package com.pj.magic.model.report;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwtReport {

    private Supplier supplier;
    private List<EwtReportItem> items;
    private Date fromDate;
    private Date toDate;
    
}
