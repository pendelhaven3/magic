package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

@Component
public class MainMenuPanel extends AbstractMagicPanel {

	private static final String SELECT_MENU_ITEM_ACTION_NAME = "selectMenuItem";
	
	private JTable table;
	
	@Override
	protected void initializeComponents() {
		table = new JTable(new MainMenuTableModel());
		table.changeSelection(0, 0, false, false);
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		
		table.setPreferredSize(new Dimension(200, 200));
		table.setBorder(BorderFactory.createEmptyBorder());
		table.setShowGrid(false);
		add(table, c);
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_MENU_ITEM_ACTION_NAME);
		table.getActionMap().put(SELECT_MENU_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
				case "Product List":
					getMagicFrame().switchToProductListPanel();
					break;
				case "Manufacturer List":
					getMagicFrame().switchToManufacturerListPanel();
					break;
				case "Supplier List":
					getMagicFrame().switchToSupplierListPanel();
					break;
				case "Product Category List":
					getMagicFrame().switchToProductCategoryListPanel();
					break;
				case "Sales Requisition":
					getMagicFrame().switchToSalesRequisitionsListPanel();
					break;
				case "Sales Invoice":
					getMagicFrame().switchToSalesInvoicesListPanel();
					break;
				case "Customer List":
					getMagicFrame().switchToCustomerListPanel();
					break;
				case "Payment Terms List":
					getMagicFrame().switchToPaymentTermListPanel();
					break;
				case "Pricing Schemes":
					getMagicFrame().switchToPricingSchemeListPanel();
					break;
				case "Stock Quantity Conversion":
					getMagicFrame().switchToStockQuantityConversionListPanel();
					break;
				}
			}
		});
	}

	@Override
	protected void doOnBack() {
		// do nothing
	}

	private class MainMenuTableModel extends AbstractTableModel {

		private String[][] data = new String[][] {
				{"Product List"},
				{"Pricing Schemes"},
				{"Sales Requisition"}, 
				{"Sales Invoice"},
				{"Stock Quantity Conversion"},
				{"Manufacturer List"},
				{"Supplier List"},
				{"Product Category List"},
				{"Customer List"},
				{"Payment Terms List"}
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

}
