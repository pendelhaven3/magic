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
import com.pj.magic.gui.dialog.SearchBadStockAdjustmentOutsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.search.BadStockAdjustmentOutSearchCriteria;
import com.pj.magic.service.BadStockAdjustmentOutService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.RetainCriteriaInfo;
import com.pj.magic.util.StringUtil;

@Component
public class BadStockAdjustmentOutListPanel extends StandardMagicPanel {
	
    @Autowired
    private BadStockAdjustmentOutService badStockAdjustmentOutService;
    
	private MagicListTable table;
	private BadStockAdjustmentOutsTableModel tableModel;
	private SearchBadStockAdjustmentOutsDialog searchBadStockAdjustmentOutsDialog;
	private RetainCriteriaInfo<BadStockAdjustmentOutSearchCriteria> retainCriteriaInfo = new RetainCriteriaInfo<>();
	
	public BadStockAdjustmentOutListPanel() {
	    setTitle("Bad Stock Adjustment Out List");
	}
	
	@Override
	public void initializeComponents() {
	    tableModel = new BadStockAdjustmentOutsTableModel();
	    table = new MagicListTable(tableModel);
        setTableColumnWidths();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

    public void updateDisplay() {
        tableModel.setItems(badStockAdjustmentOutService.getAllUnpostedBadStockAdjustmentOut());
        if (tableModel.hasItems()) {
            table.selectFirstRow();
        }
        
        if (searchBadStockAdjustmentOutsDialog != null) {
            searchBadStockAdjustmentOutsDialog.resetDisplay();
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
	    table.onEnterKeyAndDoubleClick(() -> selectAdjustmentOut());
	    registerEscapeKeyAsBack();
	}
	
	private void selectAdjustmentOut() {
	    BadStockAdjustmentOut selected = tableModel.getItem(table.getSelectedRow());
        getMagicFrame().switchToBadStockAdjustmentOutPanel(selected);
        
        retainCriteriaInfo.setSelectionInfo(table);
    }

    @Override
	protected void doOnBack() {
		getMagicFrame().switchToBadStockMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	    toolBar.add(new MagicToolBarButton("plus", "New", e -> getMagicFrame().switchToNewBadStockAdjustmentOutPanel()));
        toolBar.add(new MagicToolBarButton("search", "Search", e -> searchAdjustmentIns()));
	}

    private void searchAdjustmentIns() {
        if (searchBadStockAdjustmentOutsDialog == null) {
            searchBadStockAdjustmentOutsDialog = new SearchBadStockAdjustmentOutsDialog();
        }
        
        searchBadStockAdjustmentOutsDialog.setVisible(true);
        
        BadStockAdjustmentOutSearchCriteria criteria = searchBadStockAdjustmentOutsDialog.getSearchCriteria();
        if (criteria != null) {
            tableModel.setItems(badStockAdjustmentOutService.search(criteria));
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
        List<BadStockAdjustmentOut> adjustmentOuts;
        if (retainCriteriaInfo.hasCriteria()) {
            adjustmentOuts = badStockAdjustmentOutService.search(retainCriteriaInfo.getCriteria());
        } else {
            adjustmentOuts = badStockAdjustmentOutService.getAllUnpostedBadStockAdjustmentOut();
        }
        tableModel.setItems(adjustmentOuts);
        
        retainCriteriaInfo.adjustSelectionBasedOnTotalRecords(adjustmentOuts.size());
        retainCriteriaInfo.applySelectionInfo(table);
    }
    
    private static final int BAD_STOCK_ADJUSTMENT_OUT_NUMBER_COLUMN_INDEX = 0;
	private static final int REMARKS_COLUMN_INDEX = 1;
	private static final int POSTED_COLUMN_INDEX = 2;
	private static final int POST_DATE_COLUMN_INDEX = 3;
    private static final String[] COLUMN_NAMES = {"BS Adj. Out No.", "Remarks", "Posted", "Post Date"};
	
    private void setTableColumnWidths() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(BAD_STOCK_ADJUSTMENT_OUT_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
        columnModel.getColumn(REMARKS_COLUMN_INDEX).setPreferredWidth(300);
        columnModel.getColumn(POSTED_COLUMN_INDEX).setPreferredWidth(80);
        columnModel.getColumn(POST_DATE_COLUMN_INDEX).setPreferredWidth(120);
    }
    
	private class BadStockAdjustmentOutsTableModel extends ListBackedTableModel<BadStockAdjustmentOut> {

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BadStockAdjustmentOut item = getItem(rowIndex);
            
            switch (columnIndex) {
            case BAD_STOCK_ADJUSTMENT_OUT_NUMBER_COLUMN_INDEX:
                return item.getBadStockAdjustmentOutNumber();
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
