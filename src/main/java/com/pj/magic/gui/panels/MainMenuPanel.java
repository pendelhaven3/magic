package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

@Component
public class MainMenuPanel extends MagicPanel {

	private static final String SELECT_MENU_ITEM_ACTION_NAME = "selectMenuItem";
	
	private JTable menuItemsTable;
	
	@PostConstruct
	public void initialize() {
		layoutComponents();
		focusOnComponentWhenThisPanelIsDisplayed(menuItemsTable);
		registerKeyBindings();
	}

	private void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		
		menuItemsTable = new JTable(new MainMenuTableModel());
		menuItemsTable.setPreferredSize(new Dimension(200, 100));
		menuItemsTable.setBorder(BorderFactory.createEmptyBorder());
		menuItemsTable.setShowGrid(false);
		add(menuItemsTable, c);
	}
	
	private void registerKeyBindings() {
		menuItemsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_MENU_ITEM_ACTION_NAME);
		menuItemsTable.getActionMap().put(SELECT_MENU_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String)menuItemsTable.getValueAt(menuItemsTable.getSelectedRow(), 0)) {
				case "Product List":
					getMagicFrame().switchToProductListPanel();
					break;
				case "Manufacturer List":
					getMagicFrame().switchToManufacturerListPanel();
					break;
				case "Sales Requisition":
					getMagicFrame().switchToSalesRequisitionsListPanel();
					break;
				case "Sales Invoice":
					getMagicFrame().switchToSalesInvoicesListPanel();
					break;
				}
			}
		});
	}
	
	private class MainMenuTableModel extends AbstractTableModel {

		private String[][] data = new String[][] {
				{"Product List"},
				{"Manufacturer List"},
				{"Sales Requisition"}, 
				{"Sales Invoice"}
		};
		
		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}
		
	}

	public void refreshDisplay() {
		menuItemsTable.changeSelection(0, 0, false, false);
	}
	
}
