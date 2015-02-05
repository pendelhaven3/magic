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
import com.pj.magic.gui.dialog.SupplierPaymentSearchCriteriaDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.search.SupplierPaymentSearchCriteria;
import com.pj.magic.service.SupplierPaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SupplierPaymentListPanel extends StandardMagicPanel {

	private static final int SUPPLIER_PAYMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int STATUS_COLUMN_INDEX = 2;
	private static final int POST_DATE_COLUMN_INDEX = 3;
	
	@Autowired private SupplierPaymentService supplierPaymentService;
	@Autowired private SupplierPaymentSearchCriteriaDialog supplierPaymentSearchCriteriaDialog;
	
	private MagicListTable table;
	private SupplierPaymentsTableModel tableModel;
	
	public void updateDisplay() {
		List<SupplierPayment> supplierPayments = supplierPaymentService.getAllNewSupplierPayments();
		tableModel.setSupplierPayments(supplierPayments);
		if (!supplierPayments.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		supplierPaymentSearchCriteriaDialog.updateDisplay();
	}

	@Override
	protected void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new SupplierPaymentsTableModel();
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
				viewSupplierPaymentDetails();
			}
			
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				viewSupplierPaymentDetails();
			}
			
		});
	}

	private void viewSupplierPaymentDetails() {
		SupplierPayment supplierPayment = tableModel.getSupplierPayment(table.getSelectedRow());
		getMagicFrame().switchToSupplierPaymentPanel(supplierPayment);
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
				switchToNewSupplierPaymentPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchSupplierPayments();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchSupplierPayments() {
		supplierPaymentSearchCriteriaDialog.setVisible(true);
		
		SupplierPaymentSearchCriteria criteria = supplierPaymentSearchCriteriaDialog.getSearchCriteria();
		if (criteria != null) {
			List<SupplierPayment> payments = supplierPaymentService.searchSupplierPayments(criteria);
			tableModel.setSupplierPayments(payments);
			if (!payments.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private void switchToNewSupplierPaymentPanel() {
		getMagicFrame().switchToSupplierPaymentPanel(new SupplierPayment());
	}

	private class SupplierPaymentsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Supplier Payment No.", "Supplier", "Status", "Post Date"};
		
		List<SupplierPayment> supplierPayments = new ArrayList<>();
		
		public void setSupplierPayments(List<SupplierPayment> supplierPayments) {
			this.supplierPayments = supplierPayments;
			fireTableDataChanged();
		}
		
		public SupplierPayment getSupplierPayment(int rowIndex) {
			return supplierPayments.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return supplierPayments.size();
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
			SupplierPayment supplierPayment = supplierPayments.get(rowIndex);
			switch (columnIndex) {
			case SUPPLIER_PAYMENT_NUMBER_COLUMN_INDEX:
				return supplierPayment.getSupplierPaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return supplierPayment.getSupplier().getName();
			case STATUS_COLUMN_INDEX:
				return supplierPayment.getStatus();
			case POST_DATE_COLUMN_INDEX:
				return supplierPayment.isPosted() ? FormatterUtil.formatDate(supplierPayment.getPostDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}