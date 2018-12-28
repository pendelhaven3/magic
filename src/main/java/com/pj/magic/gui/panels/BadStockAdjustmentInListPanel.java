package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.service.BadStockAdjustmentInService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.StringUtil;

@Component
public class BadStockAdjustmentInListPanel extends StandardMagicPanel {
	
    @Autowired
    private BadStockAdjustmentInService badStockAdjustmentInService;
    
	private MagicListTable table;
	private BadStockAdjustmentInsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
	    tableModel = new BadStockAdjustmentInsTableModel();
	    table = new MagicListTable(tableModel);
        setTableColumnWidths();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

    public void updateDisplay() {
        List<BadStockAdjustmentIn> adjustmentIns = badStockAdjustmentInService.getAllUnpostedBadStockAdjustmentIn();
        tableModel.setItems(adjustmentIns);
        if (!adjustmentIns.isEmpty()) {
            table.selectFirstRow();
        }
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
	    table.onEnterKeyAndDoubleClick(() -> selectAdjustmentIn());
	}
	
	private void selectAdjustmentIn() {
	    BadStockAdjustmentIn selected = tableModel.getItem(table.getSelectedRow());
        getMagicFrame().switchToBadStockAdjustmentInPanel(selected);
    }

    @Override
	protected void doOnBack() {
		getMagicFrame().switchToBadStockMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
        JButton addButton = new MagicToolBarButton("plus", "New");
        addButton.addActionListener(e -> getMagicFrame().switchToNewBadStockAdjustmentInPanel());
        toolBar.add(addButton);
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
