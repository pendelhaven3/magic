package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchBadStockAdjustmentInsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.search.BadStockAdjustmentInSearchCriteria;
import com.pj.magic.service.BadStockAdjustmentInService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.RetainCriteriaInfo;
import com.pj.magic.util.StringUtil;

@Component
public class BadStockAdjustmentInListPanel extends StandardMagicPanel {
	
    @Autowired
    private BadStockAdjustmentInService badStockAdjustmentInService;
    
	private MagicListTable table;
	private BadStockAdjustmentInsTableModel tableModel;
	private SearchBadStockAdjustmentInsDialog searchBadStockAdjustmentInsDialog;
	private RetainCriteriaInfo<BadStockAdjustmentInSearchCriteria> retainCriteriaInfo = new RetainCriteriaInfo<>();
	
	public BadStockAdjustmentInListPanel() {
	    setTitle("Bad Stock Adjustment In List");
	}
	
	@Override
	public void initializeComponents() {
	    tableModel = new BadStockAdjustmentInsTableModel();
	    table = new MagicListTable(tableModel);
        setTableColumnWidths();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

    public void updateDisplay() {
        tableModel.setItems(badStockAdjustmentInService.getAllUnpostedBadStockAdjustmentIn());
        if (tableModel.hasItems()) {
            table.selectFirstRow();
        }
        
        if (searchBadStockAdjustmentInsDialog != null) {
            searchBadStockAdjustmentInsDialog.resetDisplay();
        }
        
        retainCriteriaInfo.clear();
	}

    @Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
	    table.onEnterKeyAndDoubleClick(() -> selectAdjustmentIn());
	    registerEscapeKeyAsBack();
	}
	
	private void selectAdjustmentIn() {
	    BadStockAdjustmentIn selected = tableModel.getItem(table.getSelectedRow());
        getMagicFrame().switchToBadStockAdjustmentInPanel(selected);
        
        retainCriteriaInfo.setSelectionInfo(table);
    }

    @Override
	protected void doOnBack() {
		getMagicFrame().switchToBadStockMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	    toolBar.add(new MagicToolBarButton("plus", "New", e -> getMagicFrame().switchToNewBadStockAdjustmentInPanel()));
        toolBar.add(new MagicToolBarButton("search", "Search", e -> searchAdjustmentIns()));
	}

    private void searchAdjustmentIns() {
        if (searchBadStockAdjustmentInsDialog == null) {
            searchBadStockAdjustmentInsDialog = new SearchBadStockAdjustmentInsDialog();
        }
        
        searchBadStockAdjustmentInsDialog.setVisible(true);
        
        BadStockAdjustmentInSearchCriteria criteria = searchBadStockAdjustmentInsDialog.getSearchCriteria();
        if (criteria != null) {
            tableModel.setItems(badStockAdjustmentInService.search(criteria));
            if (tableModel.hasItems()) {
                table.selectFirstRowThenFocus();
            } else {
                showMessage("No matching records");
            }
            
            retainCriteriaInfo.setCriteria(criteria);
        }
    }

    @Override
    public void updateDisplayOnBack() {
        List<BadStockAdjustmentIn> adjustmentIns;
        if (retainCriteriaInfo.hasCriteria()) {
            adjustmentIns = badStockAdjustmentInService.search(retainCriteriaInfo.getCriteria());
        } else {
            adjustmentIns = badStockAdjustmentInService.getAllUnpostedBadStockAdjustmentIn();
        }
        tableModel.setItems(adjustmentIns);
        
        retainCriteriaInfo.adjustSelectionBasedOnTotalRecords(adjustmentIns.size());
        retainCriteriaInfo.applySelectionInfo(table);
    }
    
    private static final int BAD_STOCK_ADJUSTMENT_IN_NUMBER_COLUMN_INDEX = 0;
	private static final int REMARKS_COLUMN_INDEX = 1;
	private static final int POSTED_COLUMN_INDEX = 2;
	private static final int POST_DATE_COLUMN_INDEX = 3;
    private static final String[] COLUMN_NAMES = {"BS Adj. In No.", "Remarks", "Posted", "Post Date"};
	
    private void setTableColumnWidths() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(BAD_STOCK_ADJUSTMENT_IN_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
        columnModel.getColumn(REMARKS_COLUMN_INDEX).setPreferredWidth(300);
        columnModel.getColumn(POSTED_COLUMN_INDEX).setPreferredWidth(80);
        columnModel.getColumn(POST_DATE_COLUMN_INDEX).setPreferredWidth(120);
    }
    
	private class BadStockAdjustmentInsTableModel extends ListBackedTableModel<BadStockAdjustmentIn> {

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BadStockAdjustmentIn item = getItem(rowIndex);
            
            switch (columnIndex) {
            case BAD_STOCK_ADJUSTMENT_IN_NUMBER_COLUMN_INDEX:
                return item.getBadStockAdjustmentInNumber();
            case REMARKS_COLUMN_INDEX:
                return item.getRemarks();
            case POSTED_COLUMN_INDEX:
                return StringUtil.generateYesNo(item.isPosted());
            case POST_DATE_COLUMN_INDEX:
                return item.isPosted() ? FormatterUtil.formatDate(item.getPostDate()) : null;
            default:
                return null;
            }
        }

        @Override
        protected String[] getColumnNames() {
            return COLUMN_NAMES;
        }
	    
	}
	
}
