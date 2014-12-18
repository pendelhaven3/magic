package com.pj.magic.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicTable;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;

@Component
public class MainMenuPanel extends StandardMagicPanel {

	private static final Map<String, String> MENU_ITEM_IMAGE_MAP = new HashMap<>();
	
	static {
		MENU_ITEM_IMAGE_MAP.put("Admin", "admin");
		MENU_ITEM_IMAGE_MAP.put("Payment", "money_large");
		MENU_ITEM_IMAGE_MAP.put("Stock Movement", "stock_movement");
		MENU_ITEM_IMAGE_MAP.put("Reports", "reports");
		MENU_ITEM_IMAGE_MAP.put("Inventory Check", "inventory_check");
		MENU_ITEM_IMAGE_MAP.put("Records Maintenance", "records");
		MENU_ITEM_IMAGE_MAP.put("Backup/Restore Data", "database_backup");
		MENU_ITEM_IMAGE_MAP.put("Change Password", "change_password");
	}
	
	@Autowired private LoginService loginService;
	
	private MagicTable table;
	private MainMenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		initializeTable();

		table.changeSelection(0, 0, false, false);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new MainMenuTableModel();
		table = new MagicTable(tableModel);
		table.setRowHeight(80);
		table.setShowGrid(false);
		table.setRowSelectionAllowed(false);
		
		table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			
			@Override
			public JPanel getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				JPanel panel = new JPanel(new GridBagLayout());
				
				if (hasFocus) {
					panel.setBackground(new Color(184, 207, 229));
				}
				
				String menuItem = (String)table.getValueAt(row, column);
				if (menuItem == null) {
					return panel;
				}

				JLabel menuLabel = new JLabel();
				menuLabel.setFont(new FontUIResource("Arial", Font.BOLD, 24));
				menuLabel.setText((String)table.getValueAt(row, column));
				
				String imageName = MENU_ITEM_IMAGE_MAP.get(menuItem);
				if (imageName == null) {
					imageName = "buy";
				}
				
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				
				JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource(
						"/images/" + imageName + ".png")));
				iconLabel.setPreferredSize(new Dimension(64, 64));
				iconLabel.setVerticalAlignment(JLabel.CENTER);
				iconLabel.setHorizontalAlignment(JLabel.CENTER);
				
				panel.add(iconLabel, c);
				
				c = new GridBagConstraints();
				c.gridx = 1;
				c.weightx = 1.0;
				panel.add(menuLabel, c);
				
				return panel;
			}
		});
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(table, c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectMenuItem();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectMenuItem();
			}
		});
	}

	public void updateDisplay() {
		tableModel.setUser(loginService.getLoggedInUser());
		table.changeSelection(0, 0, false, false);
	}
	
	private void selectMenuItem() {
		String menuItem = (String)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
		if (StringUtils.isEmpty(menuItem)) {
			return;
		}
		
		switch (menuItem) {
		case "Sales":
			getMagicFrame().switchToSalesMenuPanel();
			break;
		case "Purchases":
			getMagicFrame().switchToPurchasesMenuPanel();
			break;
		case "<html>Product Maintenance<br>and Pricing Schemes</html>":
			getMagicFrame().switchToInventoryMenuPanel();
			break;
		case "Payment":
			getMagicFrame().switchToPaymentListPanel();
			break;
		case "Stock Movement":
			getMagicFrame().switchToStockMovementMenuPanel();
			break;
		case "Reports":
			getMagicFrame().switchToReportsMenuPanel();
			break;
		case "Inventory Check":
			getMagicFrame().switchToInventoryCheckMenuPanel();
			break;
		case "Records Maintenance":
			getMagicFrame().switchToRecordsMaintenanceMenuPanel();
			break;
		case "Backup/Restore Data":
			getMagicFrame().switchToBackupDataPanel();
			break;
		case "Admin":
			getMagicFrame().switchToAdminMenuPanel();
			break;
		case "Change Password":
			getMagicFrame().switchToChangePasswordPanel();
			break;
		}
	}

	@Override
	protected void doOnBack() {
		// do nothing
	}

	private class MainMenuTableModel extends AbstractTableModel {

		private final List<String> allMenuItems = Arrays.asList(
				"Sales",
				"Purchases",
				"<html>Product Maintenance<br>and Pricing Schemes</html>",
				"Payment",
				"Stock Movement",
				"Reports",
				"Inventory Check",
				"Records Maintenance",
				"Backup/Restore Data",
				"Admin",
				"Change Password"
		);
		
		private List<String> menuItems = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			int itemCount = menuItems.size();
			return itemCount / 2 + itemCount % 2;
		}

		public void setUser(User user) {
			menuItems.clear();
			menuItems.addAll(allMenuItems);
			if (!user.isSupervisor()) {
				menuItems.remove("Admin");
			}
			fireTableDataChanged();
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			int index = rowIndex + columnIndex * getRowCount(); // * rowCount
			if (index < menuItems.size()) {
				return menuItems.get(index);
			} else {
				return null;
			}
		}
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		toolBar.removeAll();
	}

}