package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchPurchasePaymentsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.search.PurchasePaymentSearchCriteria;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchasePaymentListPanel extends StandardMagicPanel {

	private static final int PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int STATUS_COLUMN_INDEX = 2;
	private static final int POST_DATE_COLUMN_INDEX = 3;
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	@Autowired private SearchPurchasePaymentsDialog searchPurchasePaymentsDialog;
	
	private MagicListTable table;
	private PurchasePaymentsTableModel tableModel;
	
	public void updateDisplay() {
		List<PurchasePayment> purchasePayments = purchasePaymentService.getAllNewPurchasePayments();
		tableModel.setPurchasePayments(purchasePayments);
		if (!purchasePayments.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchPurchasePaymentsDialog.updateDisplay();
	}

	@Override
	protected void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new PurchasePaymentsTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(SUPPLIER_COLUMN_INDEX).setPreferredWidth(300);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewPurchasePaymentDetails();
			}
			
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				viewPurchasePaymentDetails();
			}
			
		});
	}

	private void viewPurchasePaymentDetails() {
		PurchasePayment purchasePayment = tableModel.getPurchasePayment(table.getSelectedRow());
		getMagicFrame().switchToPurchasePaymentPanel(purchasePayment);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentsMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPurchasePaymentPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPurchasePayments();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchPurchasePayments() {
		searchPurchasePaymentsDialog.setVisible(true);
		
		PurchasePaymentSearchCriteria criteria = searchPurchasePaymentsDialog.getSearchCriteria();
		if (criteria != null) {
			List<PurchasePayment> payments = purchasePaymentService.searchPurchasePayments(criteria);
			tableModel.setPurchasePayments(payments);
			if (!payments.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private void switchToNewPurchasePaymentPanel() {
		getMagicFrame().switchToPurchasePaymentPanel(new PurchasePayment());
	}

	private class PurchasePaymentsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Purchase Payment No.", "Supplier", "Status", "Post Date"};
		
		List<PurchasePayment> purchasePayments = new ArrayList<>();
		
		public void setPurchasePayments(List<PurchasePayment> purchasePayments) {
			this.purchasePayments = purchasePayments;
			fireTableDataChanged();
		}
		
		public PurchasePayment getPurchasePayment(int rowIndex) {
			return purchasePayments.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return purchasePayments.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePayment purchasePayment = purchasePayments.get(rowIndex);
			switch (columnIndex) {
			case PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX:
				return purchasePayment.getPurchasePaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return purchasePayment.getSupplier().getName();
			case STATUS_COLUMN_INDEX:
				return purchasePayment.getStatus();
			case POST_DATE_COLUMN_INDEX:
				return purchasePayment.isPosted() ? FormatterUtil.formatDate(purchasePayment.getPostDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}