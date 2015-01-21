package com.pj.magic.gui.panels.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;

@Component
public class ReportsPanel extends StandardMagicPanel {

	private static final String INVENTORY_REPORT = "Inventory Report";
	private static final String POSTED_SALES_AND_PROFIT_REPORT = "Posted Sales and Profit Report";
	private static final String REMITTANCE_REPORT = "Remittance Report";
	private static final String CASH_FLOW_REPORT = "Cash Flow Report";
	private static final String UNPAID_SALES_INVOICES_LIST = "Unpaid Sales Invoices List";
	private static final String CUSTOMER_SALES_SUMMARY_REPORT = "Customer Sales Summary Report";

	@Autowired private LoginService loginService;
	
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
		switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
		case UNPAID_SALES_INVOICES_LIST:
			getMagicFrame().switchToUnpaidSalesInvoicesListPanel();
			break;
		case CASH_FLOW_REPORT:
			getMagicFrame().switchToCashFlowReportPanel();
			break;
		case REMITTANCE_REPORT:
			getMagicFrame().switchToRemittanceReportPanel();
			break;
		case POSTED_SALES_AND_PROFIT_REPORT:
			getMagicFrame().switchToPostedSalesAndProfitReportPanel();
			break;
		case INVENTORY_REPORT:
			getMagicFrame().switchToInventoryReportPanel();
			break;
		case CUSTOMER_SALES_SUMMARY_REPORT:
			getMagicFrame().switchToCustomerSalesSummaryReportPanel();
			break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	private class MainMenuTableModel extends AbstractTableModel {

		private final List<String> allMenuItems = Arrays.asList(
				UNPAID_SALES_INVOICES_LIST,
				CASH_FLOW_REPORT,
				REMITTANCE_REPORT,
				POSTED_SALES_AND_PROFIT_REPORT,
				INVENTORY_REPORT,
				CUSTOMER_SALES_SUMMARY_REPORT
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
				menuItems.remove(POSTED_SALES_AND_PROFIT_REPORT);
				menuItems.remove(INVENTORY_REPORT);
				menuItems.remove(CUSTOMER_SALES_SUMMARY_REPORT);
			}
			fireTableDataChanged();
		}
		
		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return allMenuItems.get(rowIndex);
		}
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}