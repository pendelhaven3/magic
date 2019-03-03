package com.pj.magic.model.report;

import static java.util.Calendar.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

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
    
    public Date getForm2307FromDate() {
        Calendar cal = DateUtils.toCalendar(fromDate);
        int month = cal.get(MONTH);
        
        if (month < APRIL) {
            cal.set(MONTH, JANUARY);
        } else if (month < JULY) {
            cal.set(MONTH, APRIL);
        } else if (month < OCTOBER) {
            cal.set(MONTH, JULY);
        } else {
            cal.set(MONTH, OCTOBER);
        }
        
        cal.set(DATE, 1);
        
        return cal.getTime();
    }
    
    public Date getForm2307ToDate() {
        Calendar cal = DateUtils.toCalendar(fromDate);
        int month = cal.get(MONTH);
        
        if (month < APRIL) {
            cal.set(MONTH, MARCH);
            cal.set(DATE, 31);
        } else if (month < JULY) {
            cal.set(MONTH, JUNE);
            cal.set(DATE, 30);
        } else if (month < OCTOBER) {
            cal.set(MONTH, JULY);
            cal.set(DATE, 31);
        } else {
            cal.set(MONTH, DECEMBER);
            cal.set(DATE, 31);
        }
        
        return cal.getTime();
    }
    
}
