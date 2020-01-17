package com.pj.magic.gui.panels.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;

@Component
public class BadStockMenuPanel extends MenuPanel {

    private static final String INVENTORY_LIST = "Bad Stock Inventory List";
    private static final String ADJUSTMENT_IN = "Bad Stock Adjustment In";
    private static final String ADJUSTMENT_OUT = "Bad Stock Adjustment Out";
    private static final String REPORT_LIST = "Bad Stock Report List";
    private static final String STOCK_CARD = "Bad Stock Card Inventory Report";
    
    private MagicListTable table;
	private MainMenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new MainMenuTableModel();
		table = new MagicSubmenuTable(tableModel);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, renderer);

		table.changeSelection(0, 0, false, false);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		table.setTableHeader(null);
		table.setShowGrid(false);
		
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
	    table.onEnterKeyAndDoubleClick(() -> selectMenuItem());
	}

	public void updateDisplay() {
		table.changeSelection(0, 0, false, false);
	}
	
	private void selectMenuItem() {
		switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
		case INVENTORY_LIST:
            getMagicFrame().switchPanel(MagicFrame.BAD_STOCK_INVENTORY_LIST_PANEL);
			break;
        case ADJUSTMENT_IN:
            getMagicFrame().switchPanel(MagicFrame.BAD_STOCK_ADJUSTMENT_IN_LIST_PANEL);
            break;
        case ADJUSTMENT_OUT:
            getMagicFrame().switchPanel(MagicFrame.BAD_STOCK_ADJUSTMENT_OUT_LIST_PANEL);
            break;
        case REPORT_LIST:
            getMagicFrame().switchPanel(MagicFrame.BAD_STOCK_REPORT_LIST_PANEL);
            break;
        case STOCK_CARD:
            getMagicFrame().switchPanel(MagicFrame.BAD_STOCK_CARD_INVENTORY_REPORT_PANEL);
            break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	private class MainMenuTableModel extends AbstractTableModel {

        private final List<String> menuItems = Arrays.asList(
                INVENTORY_LIST,
                ADJUSTMENT_IN,
                ADJUSTMENT_OUT,
                REPORT_LIST,
                STOCK_CARD
        );
        
		@Override
		public int getRowCount() {
			return menuItems.size();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return menuItems.get(rowIndex);
		}
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}