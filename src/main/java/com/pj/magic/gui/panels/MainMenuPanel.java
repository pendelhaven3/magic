package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
		menuItemsTable.setPreferredSize(new Dimension(200, 50));
		menuItemsTable.setBorder(BorderFactory.createEmptyBorder());
		menuItemsTable.setShowGrid(false);
		add(menuItemsTable, c);
	}
	
	private void registerKeyBindings() {
		final JPanel panel = this;
		
		menuItemsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_MENU_ITEM_ACTION_NAME);
		menuItemsTable.getActionMap().put(SELECT_MENU_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (menuItemsTable.getSelectedRow()) {
				case 0:
					getMagicFrame().switchToSalesRequisitionsListPanel();
					break;
				case 1:
					JOptionPane.showMessageDialog(panel, "Coming soon!");
					break;
				}
			}
		});
	}
	
	private class MainMenuTableModel extends AbstractTableModel {

		private String[][] data = new String[][] {{"Sales Requisition"}, {"Sales Invoice"}};
		
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
