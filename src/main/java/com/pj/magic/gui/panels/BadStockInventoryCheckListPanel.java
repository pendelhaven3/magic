package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.service.BadStockInventoryCheckService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.StringUtil;

public class BadStockInventoryCheckListPanel extends StandardMagicPanel {
	
    @Autowired
    private BadStockInventoryCheckService badStockInventoryCheckService;
    
	private MagicListTable table;
	private BadStockInventoryChecksTableModel tableModel;
	
	public BadStockInventoryCheckListPanel() {
	    setTitle("Bad Stock Inventory Check List");
	}
	
	@Override
	public void initializeComponents() {
	    tableModel = new BadStockInventoryChecksTableModel();
	    table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

    public void updateDisplay() {
        tableModel.setItems(badStockInventoryCheckService.getAllBadStockInventoryChecks());
        if (tableModel.hasItems()) {
            table.selectFirstRow();
        }
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
	    table.onEnterKeyAndDoubleClick(() -> selectBadStockInventoryCheck());
	    registerEscapeKeyAsBack();
	}
	
	private void selectBadStockInventoryCheck() {
	    BadStockInventoryCheck selected = tableModel.getItem(table.getSelectedRow());
        getMagicFrame().switchToBadStockInventoryCheckPanel(selected);
    }

    @Override
	protected void doOnBack() {
		getMagicFrame().switchToBadStockMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	    toolBar.add(new MagicToolBarButton("plus", "New", 
	    		e -> getMagicFrame().switchToBadStockInventoryCheckPanel(new BadStockInventoryCheck())));
	}

    @Override
    public void updateDisplayOnBack() {
    	updateDisplay();
    }
    
    private static final int BAD_STOCK_INVENTORY_CHECK_NUMBER_COLUMN_INDEX = 0;
	private static final int REMARKS_COLUMN_INDEX = 1;
	private static final int POSTED_COLUMN_INDEX = 2;
	private static final int POST_DATE_COLUMN_INDEX = 3;
    private static final String[] COLUMN_NAMES = {"Bad Stock Inventory Check No.", "Remarks", "Posted", "Post Date"};
	
	private class BadStockInventoryChecksTableModel extends ListBackedTableModel<BadStockInventoryCheck> {

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        	BadStockInventoryCheck item = getItem(rowIndex);
            
            switch (columnIndex) {
            case BAD_STOCK_INVENTORY_CHECK_NUMBER_COLUMN_INDEX:
                return item.getBadStockInventoryCheckNumber();
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
