package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class ViewPaymentPanel extends StandardMagicPanel {

	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	
	@Autowired private PaymentService paymentService;
	
	private JLabel customerCodeField;
	private JLabel customerNameField;
	private JLabel paymentDateField;
	private JLabel receivedByField;
	private JLabel paymentTerminalField;
	private JLabel totalAmountField;
	private JLabel amountReceivedField;
	private JLabel changeField;
	private Payment payment;
	private JTable table;
	private PaymentSalesInvoicesTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new JLabel();
		customerNameField = new JLabel();
		paymentDateField = new JLabel();
		receivedByField = new JLabel();
		paymentTerminalField = new JLabel();
		totalAmountField = new JLabel();
		amountReceivedField = new JLabel();
		changeField = new JLabel();
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new PaymentSalesInvoicesTableModel();
		table = new MagicListTable(tableModel);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(30, 1), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(150, 20));
		mainPanel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNameField.setPreferredSize(new Dimension(300, 20));
		mainPanel.add(customerNameField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentDateField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(paymentDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Received By:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		receivedByField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(receivedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Payment Terminal:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTerminalField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(paymentTerminalField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Total Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField.setPreferredSize(new Dimension(150, 20));
		mainPanel.add(totalAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Amount Received: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		amountReceivedField.setPreferredSize(new Dimension(150, 20));
		mainPanel.add(amountReceivedField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Change: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		changeField.setPreferredSize(new Dimension(150, 20));
		mainPanel.add(changeField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(600, 150));
		mainPanel.add(tableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@SuppressWarnings("unchecked")
	public void updateDisplay(Payment payment) {
		this.payment = payment = paymentService.getPayment(payment.getId());
		
		customerCodeField.setText(payment.getCustomer().getCode());
		customerNameField.setText(payment.getCustomer().getName());
		paymentDateField.setText(FormatterUtil.formatDate(payment.getPaymentDate()));
		receivedByField.setText(payment.getReceivedBy().getUsername());
		paymentTerminalField.setText(payment.getPaymentTerminal().getName());
		totalAmountField.setText(FormatterUtil.formatAmount(payment.getTotalAmount()));
		amountReceivedField.setText(FormatterUtil.formatAmount(payment.getAmountReceived()));
		changeField.setText(FormatterUtil.formatAmount(payment.getChange()));
		
		// TODO: Resolve unchecked warning
		tableModel.setSalesInvoices((List<SalesInvoice>)CollectionUtils.collect(payment.getItems(), 
				new Transformer() {
			
			@Override
			public SalesInvoice transform(Object item) {
				return ((PaymentItem)item).getSalesInvoice();
			}
		}));
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private class PaymentSalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"SI No.", "Amount"};
		
		private List<SalesInvoice> salesInvoices = new ArrayList<>();
		
		public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
			this.salesInvoices = salesInvoices;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return salesInvoices.size();
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
			SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case AMOUNT_COLUMN_INDEX:
				return salesInvoice.getTotalNetAmount();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}
