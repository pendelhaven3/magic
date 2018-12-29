package com.pj.magic.util;

import java.awt.Rectangle;

import javax.swing.JTable;

import com.pj.magic.gui.tables.MagicListTable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetainCriteriaInfo<T> {

    private T criteria;
    private Rectangle visibleRect;
    private int selectedRow;

    public void setCriteria(T criteria) {
        this.criteria = criteria;
        visibleRect = null;
        selectedRow = 0;
    }
    
    public boolean hasCriteria() {
        return criteria != null;
    }
    
    public void setSelectionInfo(JTable table) {
        selectedRow = table.getSelectedRow();
        visibleRect = table.getVisibleRect();
    }
    
    public void applySelectionInfo(MagicListTable table) {
        table.selectRow(selectedRow);
        if (visibleRect != null) {
            table.scrollRectToVisible(visibleRect);
        }
    }
    
    public void clear() {
        criteria = null;
        visibleRect = null;
        selectedRow = 0;
    }

    public void adjustSelectionBasedOnTotalRecords(int totalRecords) {
        if (selectedRow > totalRecords - 1) {
            selectedRow = totalRecords - 1;
        }
    }
    
}
