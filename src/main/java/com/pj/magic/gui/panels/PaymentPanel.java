package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AddSalesInvoicesToPaymentDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.PaymentCashPaymentsTable;
import com.pj.magic.gui.tables.PaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.PaymentSalesInvoicesTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaymentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PaymentPanel.class);
	
	private static final String SAVE_CUSTOMER_ACTION_NAME = "saveCustomer";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = "openSelectCustomerDialog";
	
	@Autowired private PaymentSalesInvoicesTable salesInvoicesTable;
	@Autowired private PaymentService paymentService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private CustomerService customerService;
	@Autowired private AddSalesInvoicesToPaymentDialog addSalesInvoicesToPaymentDialog;
	@Autowired private PaymentCheckPaymentsTable checksTable;
	@Autowired private PaymentCashPaymentsTable cashPaymentsTable;
	
	private Payment payment;
	private JLabel paymentNumberField;
	private MagicTextField customerCodeField;
	private JLabel customerNameField;
	private JLabel postedField;
	private JLabel totalAmountDueField;
	private JLabel totalCashPaymentsField;
	private JLabel totalCheckPaymentsField;
	private JLabel totalPaymentsField;
	private JLabel totalAdjustmentsField;
	private JLabel overOrShortField;
	private JButton selectCustomerButton;
	private MagicToolBarButton addSalesInvoiceButton;
	private MagicToolBarButton deleteSalesInvoiceButton;
	private MagicToolBarButton addCheckPaymentButton;
	private MagicToolBarButton deleteCheckPaymentButton;
	private MagicToolBarButton addCashPaymentButton;
	private MagicToolBarButton deleteCashPaymentButton;
	private MagicToolBarButton deleteButton;
	private MagicToolBarButton postButton;
	
	@Override
	protected void initializeComponents() {
		paymentNumberField = new JLabel();
		
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});;
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		initializeModelListeners();
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_CUSTOMER_ACTION_NAME);
		customerCodeField.getActionMap().put(SAVE_CUSTOMER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
		
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME);
		customerCodeField.getActionMap().put(OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});
	}

	private void saveCustomer() {
		// TODO Auto-generated method stub
		
	}

	protected void selectCustomer() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (payment.getCustomer() != null && payment.getCustomer().equals(customer)) {
				// skip saving sales requisition since there is no change
				return;
			}
			payment.setCustomer(customer);
			paymentService.save(payment);
			updateDisplay(payment);
		}
	}

	@Override
	protected void doOnBack() {
		if (salesInvoicesTable.isEditing()) {
			salesInvoicesTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToPaymentListPanel();
	}
	
	public void updateDisplay(Payment payment) {
		if (payment.getId() == null) {
			this.payment = payment;
			clearDisplay();
			return;
		}
		
		this.payment = payment = paymentService.getPayment(payment.getId());
		
		paymentNumberField.setText(payment.getPaymentNumber().toString());
		customerCodeField.setText(payment.getCustomer().getCode());
		customerNameField.setText(payment.getCustomer().getName());
		postedField.setText(payment.isPosted() ? "Yes" : "No");
		totalAmountDueField.setText(FormatterUtil.formatAmount(payment.getTotalAmountDue()));
		totalCashPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCashPayments()));
		totalCheckPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCheckPayments()));
		totalPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalPayments()));
		totalAdjustmentsField.setText(FormatterUtil.formatAmount(payment.getTotalAdjustments()));
		overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
		
		salesInvoicesTable.setPayment(payment);
		checksTable.setPayment(payment);
		cashPaymentsTable.setPayment(payment);
		
		deleteButton.setEnabled(!payment.isPosted());
		postButton.setEnabled(!payment.isPosted());
	}

	private void clearDisplay() {
		paymentNumberField.setText(null);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		postedField.setText(null);
		
		salesInvoicesTable.clearDisplay();
		checksTable.clearDisplay();
		cashPaymentsTable.clearDisplay();
		
		totalAmountDueField.setText(null);
		totalCashPaymentsField.setText(null);
		totalCheckPaymentsField.setText(null);
		totalPaymentsField.setText(null);
		totalAdjustmentsField.setText(null);
		overOrShortField.setText(null);
		
		deleteButton.setEnabled(false);
		postButton.setEnabled(false);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(customerCodeField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameField.setPreferredSize(new Dimension(300, 20));
		panel.add(customerNameField, c);
		
		return panel;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Payment No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(paymentNumberField, c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		customerCodeField.setPreferredSize(new Dimension(140, 25));
		customerNameField = ComponentUtil.createLabel(190, "");
		
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Posted:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postedField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		
		JTabbedPane tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 280));
		mainPanel.add(tabbedPane, c);
				
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		mainPanel.add(createTotalsPanel(), c);
				
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
	}

	private JPanel createCheckPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addCheckPaymentButton = new MagicToolBarButton("plus_small", "Add Check Payment", true);
		addCheckPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addCheckPayment();
			}
		});
		panel.add(addCheckPaymentButton, BorderLayout.WEST);
		
		deleteCheckPaymentButton = new MagicToolBarButton("minus_small", "Delete Check Payment", true);
		deleteCheckPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				checksTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteCheckPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCheckPayment() {
		checksTable.addNewRow();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("trash", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePayment();
			}
		});
		toolBar.add(deleteButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPayment();
			}
		});
		
		toolBar.add(postButton);
	}

	private void postPayment() {
		// TODO Auto-generated method stub
		
	}

	private void deletePayment() {
		if (confirm("Delete Payment?")) {
			try {
				paymentService.delete(payment);
				showMessage("Payment deleted");
				getMagicFrame().switchToPaymentListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	private JPanel createSalesInvoicesTableToolBar() {
		JPanel panel = new JPanel();
		
		addSalesInvoiceButton = new MagicToolBarButton("plus_small", "Add Item (F10)", true);
		addSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addSalesInvoice();
			}
		});
		panel.add(addSalesInvoiceButton, BorderLayout.WEST);
		
		deleteSalesInvoiceButton = new MagicToolBarButton("minus_small", "Delete Item (Delete)", true);
		deleteSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				salesInvoicesTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteSalesInvoiceButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addSalesInvoice() {
		addSalesInvoicesToPaymentDialog.searchSalesInvoicesForPayment(payment.getCustomer());
		addSalesInvoicesToPaymentDialog.setVisible(true);
		
		List<SalesInvoice> selectedSalesInvoices = addSalesInvoicesToPaymentDialog.getSelectedSalesInvoices();
		if (!selectedSalesInvoices.isEmpty()) {
			for (SalesInvoice salesInvoice : selectedSalesInvoices) {
				PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
				paymentSalesInvoice.setParent(payment);
				paymentSalesInvoice.setSalesInvoice(salesInvoice);
				paymentService.save(paymentSalesInvoice);
			}
			payment.setSalesInvoices(paymentService.findAllPaymentSalesInvoicesByPayment(payment));
			salesInvoicesTable.setPayment(payment);
		}
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Sales Invoices", createSalesInvoicesPanel());
		tabbedPane.addTab("Cash Payments", createCashPaymentsPanel());
		tabbedPane.addTab("Check Payments", createCheckPaymentsPanel());
		tabbedPane.addTab("Adjustments", createAdjustmentsPanel());
		return tabbedPane;
	}

	private JPanel createSalesInvoicesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createSalesInvoicesTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesTableScrollPane = new JScrollPane(salesInvoicesTable);
		salesInvoicesTableScrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(salesInvoicesTableScrollPane, c);
		
		return panel;
	}
	
	private JPanel createCheckPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createCheckPaymentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(checksTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}
	
	private JPanel createTotalsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		currentRow++;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Amount Due:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountDueField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAmountDueField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(160, "Total Check Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCheckPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCheckPaymentsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Payment:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalPaymentsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Cash Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCashPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCashPaymentsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Adjustments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAdjustmentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAdjustmentsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Over/Short:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		overOrShortField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(overOrShortField, c);
		
		return mainPanel;
	}
	
	private JPanel createCashPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createCashPaymentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(cashPaymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}
	
	private JPanel createCashPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addCashPaymentButton = new MagicToolBarButton("plus_small", "Add Cash Payment", true);
		addCashPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addCashPayment();
			}
		});
		panel.add(addCashPaymentButton, BorderLayout.WEST);
		
		deleteCashPaymentButton = new MagicToolBarButton("minus_small", "Delete Cash Payment", true);
		deleteCashPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cashPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteCashPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCashPayment() {
		cashPaymentsTable.addNewRow();
	}

	private JPanel createAdjustmentsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		return mainPanel;
	}

	private void initializeModelListeners() {
		salesInvoicesTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountDueField.setText(FormatterUtil.formatAmount(payment.getTotalAmountDue()));
				overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
			}
		});
		
		checksTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCheckPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCheckPayments()));
				totalPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalPayments()));
				overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
			}
		});

		cashPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				System.out.println("hark!");
				totalCashPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalCashPayments()));
				totalPaymentsField.setText(FormatterUtil.formatAmount(payment.getTotalPayments()));
				overOrShortField.setText(FormatterUtil.formatAmount(payment.getOverOrShort()));
			}
		});
		
	}
	
}