package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SupplierPaymentAdjustmentSearchCriteriaDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.SupplierPaymentAdjustment;
import com.pj.magic.model.search.SupplierPaymentAdjustmentSearchCriteria;
import com.pj.magic.service.SupplierPaymentAdjustmentService;
import com.pj.magic.util.FormatterUtil;

@Component
public class SupplierPaymentAdjustmentListPanel extends StandardMagicPanel {
	
	private static final int SUPPLIER_PAYMENT_ADJUSTMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int ADJUSTMENT_TYPE_COLUMN_INDEX = 2;
	private static final int AMOUNT_COLUMN_INDEX = 3;
	private static final int STATUS_COLUMN_INDEX = 4;
	private static final int POST_DATE_COLUMN_INDEX = 5;
	
	@Autowired private SupplierPaymentAdjustmentService supplierPaymentAdjustmentService;
	@Autowired private SupplierPaymentAdjustmentSearchCriteriaDialog supplierPaymentAdjustmentSearchCriteriaDialog;
	
	private MagicListTable table;
	private SupplierPaymentAdjustmentsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new SupplierPaymentAdjustmentsTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
		List<SupplierPaymentAdjustment> paymentAdjustments = 
				supplierPaymentAdjustmentService.getAllNewPaymentAdjustments();
		tableModel.setPaymentAdjustments(paymentAdjustments);
		if (!paymentAdjustments.isEmpty()) {
			table.changeSelection(0, 0);
		}
		supplierPaymentAdjustmentSearchCriteriaDialog.updateDisplay();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() != -1) {
					displayCurrentlySelectedSupplierPaymentAdjustment();
				}
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				displayCurrentlySelectedSupplierPaymentAdjustment();
			}
		});
	}
	
	private void displayCurrentlySelectedSupplierPaymentAdjustment() {
		getMagicFrame().switchToEditSupplierPaymentAdjustmentPanel(getCurrentlySelectedPaymentAdjustment());
	}

	private SupplierPaymentAdjustment getCurrentlySelectedPaymentAdjustment() {
		return tableModel.getPaymentAdjustments().get(table.getSelectedRow());
	}

	protected void switchToNewSupplierPaymentAdjustmentPanel() {
		getMagicFrame().switchToAddNewSupplierPaymentAdjustmentPanel();
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
				switchToNewSupplierPaymentAdjustmentPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPaymentAdjustments();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchPaymentAdjustments() {
		supplierPaymentAdjustmentSearchCriteriaDialog.setVisible(true);
		
		SupplierPaymentAdjustmentSearchCriteria criteria = 
				supplierPaymentAdjustmentSearchCriteriaDialog.getSearchCriteria();
		if (criteria != null) {
			List<SupplierPaymentAdjustment> adjustmentIns = supplierPaymentAdjustmentService.search(criteria);
			tableModel.setPaymentAdjustments(adjustmentIns);
			if (!adjustmentIns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}

	}

	private class SupplierPaymentAdjustmentsTableModel extends AbstractTableModel {

		private String[] columnNames = 
			{"Payment Adj. No.", "Supplier", "Adj. Type", "Amount", "Status", "Post Date"};
		
		private List<SupplierPaymentAdjustment> paymentAdjustments = new ArrayList<>();
		
		public List<SupplierPaymentAdjustment> getPaymentAdjustments() {
			return paymentAdjustments;
		}
		
		@Override
		public int getRowCount() {
			return paymentAdjustments.size();
		}

		public void setPaymentAdjustments(List<SupplierPaymentAdjustment> paymentAdjustments) {
			this.paymentAdjustments = paymentAdjustments;
			fireTableDataChanged();
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
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SupplierPaymentAdjustment paymentAdjustment = paymentAdjustments.get(rowIndex);
			switch (columnIndex) {
			case SUPPLIER_PAYMENT_ADJUSTMENT_NUMBER_COLUMN_INDEX:
				return paymentAdjustment.getSupplierPaymentAdjustmentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return paymentAdjustment.getSupplier().getName();
			case ADJUSTMENT_TYPE_COLUMN_INDEX:
				return paymentAdjustment.getAdjustmentType().getCode();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(paymentAdjustment.getAmount());
			case STATUS_COLUMN_INDEX:
				return paymentAdjustment.getStatus();
			case POST_DATE_COLUMN_INDEX:
				return paymentAdjustment.isPosted() ? 
						FormatterUtil.formatDate(paymentAdjustment.getPostDate()) : null;
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}