package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.KeyUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class CreatePaymentDialog extends MagicDialog {

	private static final Logger logger = LoggerFactory.getLogger(CreatePaymentDialog.class);
	
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	
	@Autowired private PaymentService paymentService;
	
	private JLabel customerCodeField;
	private JLabel customerNameField;
	private JLabel totalAmountField;
	private MagicTextField amountReceivedField;
	private JButton saveButton;
	private JTable table;
	private CreatePaymentTableModel tableModel;
	private Payment payment;
	
	public CreatePaymentDialog() {
		setSize(600, 400);
		setLocationRelativeTo(null);
		setTitle("Create Payment");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		initializeTable();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeTable() {
		tableModel = new CreatePaymentTableModel();
		table = new MagicListTable(tableModel);
	}

	private void initializeComponents() {
		customerCodeField = new JLabel();
		customerNameField = new JLabel();
		totalAmountField = new JLabel();
		amountReceivedField = new MagicTextField();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePayment();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(amountReceivedField);
	}

	private void savePayment() {
		if (validatePayment()) {
			payment.setAmountReceived(NumberUtil.toBigDecimal(amountReceivedField.getText()));
			if (confirm("Save Payment?")) {
				try {
					paymentService.save(payment);
					showMessage("Payment saved");
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					showErrorMessage("Unexpected error occured on saving");
				}
			}
		};
		
		setVisible(false);
	}

	private boolean validatePayment() {
		return true;
	}

	private void registerKeyBindings() {
		saveButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		saveButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePayment();
			}
		});
		
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createHorizontalFiller(30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Customer Code:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(150, 25));
		add(customerCodeField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Customer Name:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNameField.setPreferredSize(new Dimension(250, 25));
		add(customerNameField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Total Amount:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField.setPreferredSize(new Dimension(150, 25));
		add(totalAmountField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Amount Received:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		amountReceivedField.setPreferredSize(new Dimension(120, 25));
		add(amountReceivedField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createVerticalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(200, 100));
		add(itemsTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createVerticalFiller(10), c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		saveButton.setPreferredSize(new Dimension(100, 25));
		add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(), c);
	}
	
	public void updateDisplay(Customer customer, List<SalesInvoice> salesInvoices) {
		payment = new Payment();
		payment.setCustomer(customer);
		for (SalesInvoice salesInvoice : salesInvoices) {
			PaymentItem item = new PaymentItem();
			item.setParent(payment);
			item.setSalesInvoice(salesInvoice);
			payment.getItems().add(item);
		}
		
		customerCodeField.setText(customer.getCode());
		customerNameField.setText(customer.getName());
		totalAmountField.setText(FormatterUtil.formatAmount(payment.getTotalAmount()));
		tableModel.setSalesInvoices(salesInvoices);
	}
	
	private class CreatePaymentTableModel extends AbstractTableModel {

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
	
	public Payment getPayment() {
		return payment;
	}
	
}
