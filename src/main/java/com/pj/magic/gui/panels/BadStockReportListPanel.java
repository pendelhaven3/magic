package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchBadStockReportsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.search.BadStockReportSearchCriteria;
import com.pj.magic.service.BadStockReportService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.RetainCriteriaInfo;
import com.pj.magic.util.StringUtil;

public class BadStockReportListPanel extends StandardMagicPanel {
	
    @Autowired
    private BadStockReportService badStockReportService;
    
	private MagicListTable table;
	private BadStockReportsTableModel tableModel;
	private SearchBadStockReportsDialog searchBadStockReportsDialog;
	private RetainCriteriaInfo<BadStockReportSearchCriteria> retainCriteriaInfo = new RetainCriteriaInfo<>();
	
	public BadStockReportListPanel() {
	    setTitle("Inventory Bad Stock Report List");
	}
	
	@Override
	public void initializeComponents() {
	    tableModel = new BadStockReportsTableModel();
	    table = new MagicListTable(tableModel);
        setTableColumnWidths();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

    public void updateDisplay() {
        tableModel.setItems(badStockReportService.getAllUnpostedBadStockReports());
        if (tableModel.hasItems()) {
            table.selectFirstRow();
        }
        
        if (searchBadStockReportsDialog != null) {
            searchBadStockReportsDialog.resetDisplay();
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
	    table.onEnterKeyAndDoubleClick(() -> selectBadStockReport());
	    registerEscapeKeyAsBack();
	}
	
	private void selectBadStockReport() {
	    BadStockReport selected = tableModel.getItem(table.getSelectedRow());
        getMagicFrame().switchToBadStockReportPanel(selected);
        
        retainCriteriaInfo.setSelectionInfo(table);
    }

    @Override
	protected void doOnBack() {
		getMagicFrame().switchToBadStockMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	    toolBar.add(new MagicToolBarButton("plus", "New", 
	    		e -> getMagicFrame().switchToBadStockReportPanel(new BadStockReport())));
        toolBar.add(new MagicToolBarButton("search", "Search", e -> searchBadStockReports()));
	}

    private void searchBadStockReports() {
        if (searchBadStockReportsDialog == null) {
            searchBadStockReportsDialog = new SearchBadStockReportsDialog();
        }
        
        searchBadStockReportsDialog.setVisible(true);
        
        BadStockReportSearchCriteria criteria = searchBadStockReportsDialog.getSearchCriteria();
        if (criteria != null) {
            tableModel.setItems(badStockReportService.searchBadStockReports(criteria));
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
        List<BadStockReport> badStockReports;
        if (retainCriteriaInfo.hasCriteria()) {
        	badStockReports = badStockReportService.searchBadStockReports(retainCriteriaInfo.getCriteria());
        } else {
        	badStockReports = badStockReportService.getAllUnpostedBadStockReports();
        }
        tableModel.setItems(badStockReports);
        
        retainCriteriaInfo.adjustSelectionBasedOnTotalRecords(badStockReports.size());
        retainCriteriaInfo.applySelectionInfo(table);
    }
    
    private static final int BAD_STOCK_REPORT_NUMBER_COLUMN_INDEX = 0;
	private static final int LOCATION_COLUMN_INDEX = 1;
	private static final int RECEIVED_DATE_COLUMN_INDEX = 2;
	private static final int POSTED_COLUMN_INDEX = 3;
	private static final int POST_DATE_COLUMN_INDEX = 4;
    private static final String[] COLUMN_NAMES = {"Inventory BSR No.", "Location", "Received Date", "Posted", "Post Date"};
	
    private void setTableColumnWidths() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(BAD_STOCK_REPORT_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
        columnModel.getColumn(LOCATION_COLUMN_INDEX).setPreferredWidth(100);
        columnModel.getColumn(POSTED_COLUMN_INDEX).setPreferredWidth(80);
        columnModel.getColumn(POST_DATE_COLUMN_INDEX).setPreferredWidth(120);
    }
    
	private class BadStockReportsTableModel extends ListBackedTableModel<BadStockReport> {

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BadStockReport item = getItem(rowIndex);
            
            switch (columnIndex) {
            case BAD_STOCK_REPORT_NUMBER_COLUMN_INDEX:
                return item.getBadStockReportNumber();
            case LOCATION_COLUMN_INDEX:
                return item.getLocation();
            case RECEIVED_DATE_COLUMN_INDEX:
                return item.getReceivedDate() != null ? FormatterUtil.formatDate(item.getReceivedDate()) : null;
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
