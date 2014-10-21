package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;

@Component
public class MainMenuPanel extends AbstractMagicPanel {

	private static final String SELECT_MENU_ITEM_ACTION_NAME = "selectMenuItem";
	
	@Autowired private LoginService loginService;
	
	private JTable table;
	private MainMenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		table = new JTable(new MainMenuTableModel());
		tableModel = (MainMenuTableModel)table.getModel();

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
		
		table.setPreferredSize(new Dimension(200, 300));
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
		case "Mark Sales Invoice":
			getMagicFrame().switchToMarkSalesInvoicesPanel();
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
		case "Purchase Order":
			getMagicFrame().switchToPurchaseOrderListPanel();
			break;
		case "Receiving Receipt":
			getMagicFrame().switchToReceivingReceiptListPanel();
			break;
		case "Adjustment In":
			getMagicFrame().switchToAdjustmentInListPanel();
			break;
		case "Adjustment Out":
			getMagicFrame().switchToAdjustmentOutListPanel();
			break;
		case "Inventory Check":
			getMagicFrame().switchToInventoryCheckListPanel();
			break;
		case "Area Inventory Report":
			getMagicFrame().switchToAreaInventoryReportListPanel();
			break;
		case "User List":
			getMagicFrame().switchToUserListPanel();
			break;
		}
	}

	@Override
	protected void doOnBack() {
		// do nothing
	}

	private class MainMenuTableModel extends AbstractTableModel {

		private final List<String> allMenuItems = Arrays.asList(
				"Product List",
				"Pricing Schemes",
				"Sales Requisition", 
				"Sales Invoice",
				"Mark Sales Invoice",
				"Stock Quantity Conversion",
				"Purchase Order",
				"Receiving Receipt",
				"Adjustment In",
				"Adjustment Out",
				"Manufacturer List",
				"Supplier List",
				"Product Category List",
				"Customer List",
				"Payment Terms List",
				"Inventory Check",
				"Area Inventory Report",
				"User List"
		);
		
		private List<String> menuItems = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return menuItems.size();
		}

		public void setUser(User user) {
			menuItems.clear();
			menuItems.addAll(allMenuItems);
			if (!user.isSupervisor()) {
				menuItems.remove("Pricing Schemes");
				menuItems.remove("Inventory Check");
				menuItems.remove("User List");
			}
			fireTableDataChanged();
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

}
