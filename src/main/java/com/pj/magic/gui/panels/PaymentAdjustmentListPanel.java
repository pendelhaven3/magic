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
import com.pj.magic.gui.dialog.AdjustmentInSearchCriteriaDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.service.PaymentAdjustmentService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaymentAdjustmentListPanel extends StandardMagicPanel {
	
	private static final int PAYMENT_ADJUSTMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int ADJUSTMENT_TYPE_COLUMN_INDEX = 2;
	private static final int AMOUNT_COLUMN_INDEX = 3;
	
	@Autowired private PaymentAdjustmentService paymentAdjustmentService;
	@Autowired private AdjustmentInSearchCriteriaDialog adjustmentInSearchCriteriaDialog;
	
	private MagicListTable table;
	private PaymentAdjustmentsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new PaymentAdjustmentsTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
		tableModel.setPaymentAdjustments(paymentAdjustmentService.getAllPaymentAdjustments());
		adjustmentInSearchCriteriaDialog.updateDisplay();
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
					displayCurrentlySelectedPaymentAdjustment();
				}
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				displayCurrentlySelectedPaymentAdjustment();
			}
		});
	}
	
	private void displayCurrentlySelectedPaymentAdjustment() {
		getMagicFrame().switchToEditPaymentAdjustmentPanel(getCurrentlySelectedPaymentAdjustment());
	}

	private PaymentAdjustment getCurrentlySelectedPaymentAdjustment() {
		return tableModel.getPaymentAdjustments().get(table.getSelectedRow());
	}

	protected void switchToNewPaymentAdjustmentPanel() {
		getMagicFrame().switchToAddNewPaymentAdjustmentPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPaymentAdjustmentPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAdjustmentIns();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchAdjustmentIns() {
//		adjustmentInSearchCriteriaDialog.setVisible(true);
//		
//		AdjustmentInSearchCriteria criteria = adjustmentInSearchCriteriaDialog.getSearchCriteria();
//		if (criteria != null) {
//			List<AdjustmentIn> adjustmentIns = adjustmentInService.search(criteria);
//			table.setAdjustmentIns(adjustmentIns);
//			if (!adjustmentIns.isEmpty()) {
//				table.changeSelection(0, 0, false, false);
//				table.requestFocusInWindow();
//			} else {
//				showMessage("No matching records");
//			}
//		}
//
	}

	private class PaymentAdjustmentsTableModel extends AbstractTableModel {

		private String[] columnNames = {"Payment Adj. No.", "Customer", "Adj. Type", "Amount"};
		
		private List<PaymentAdjustment> paymentAdjustments = new ArrayList<>();
		
		public List<PaymentAdjustment> getPaymentAdjustments() {
			return paymentAdjustments;
		}
		
		@Override
		public int getRowCount() {
			return paymentAdjustments.size();
		}

		public void setPaymentAdjustments(List<PaymentAdjustment> paymentAdjustments) {
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
		public Object getValueAt(int rowIndex, int columnIndex) {
			PaymentAdjustment paymentAdjustment = paymentAdjustments.get(rowIndex);
			switch (columnIndex) {
			case PAYMENT_ADJUSTMENT_NUMBER_COLUMN_INDEX:
				return paymentAdjustment.getPaymentAdjustmentNumber();
			case CUSTOMER_COLUMN_INDEX:
				return paymentAdjustment.getCustomer().getName();
			case ADJUSTMENT_TYPE_COLUMN_INDEX:
				return paymentAdjustment.getAdjustmentType().getCode();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(paymentAdjustment.getAmount());
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}