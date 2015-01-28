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
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AddReceivingReceiptsToSupplierPaymentDialog;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.PaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.SupplierPaymentCashPaymentsTable;
import com.pj.magic.gui.tables.SupplierPaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.SupplierPaymentCreditCardPaymentsTable;
import com.pj.magic.gui.tables.SupplierPaymentReceivingReceiptsTable;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentReceivingReceipt;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.service.SupplierPaymentService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SupplierPaymentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(SupplierPaymentPanel.class);
	
	private static final String SAVE_SUPPLIER_ACTION_NAME = "saveSupplier";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = "openSelectCustomerDialog";
	
	@Autowired private SupplierPaymentReceivingReceiptsTable receivingReceiptsTable;
	@Autowired private SupplierPaymentService supplierPaymentService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private SupplierService supplierService;
	@Autowired private AddReceivingReceiptsToSupplierPaymentDialog addReceivingReceiptsToSupplierPaymentDialog;
	@Autowired private SupplierPaymentCashPaymentsTable cashPaymentsTable;
	@Autowired private SupplierPaymentCreditCardPaymentsTable creditCardPaymentsTable;
	@Autowired private SupplierPaymentCheckPaymentsTable checkPaymentsTable;
	@Autowired private PaymentPaymentAdjustmentsTable adjustmentsTable;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	@Autowired private SalesReturnService salesReturnService;
	
	private SupplierPayment supplierPayment;
	private JLabel supplierPaymentNumberField;
	private JLabel statusLabel;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameLabel;
	private JLabel totalAmountLabel;
	private JLabel totalCashPaymentsLabel;
	private JLabel totalCreditCardPaymentsLabel;
	private JLabel totalCheckPaymentsLabel;
	private JLabel totalPaymentsLabel;
	private JLabel totalAdjustmentsField;
	private JLabel overOrShortLabel;
	private JButton selectSupplierButton;
	private MagicToolBarButton addReceivingReceiptButton;
	private MagicToolBarButton removeReceivingReceiptButton;
	private MagicToolBarButton addCashPaymentButton;
	private MagicToolBarButton removeCashPaymentButton;
	private MagicToolBarButton addCreditCardPaymentButton;
	private MagicToolBarButton removeCreditCardPaymentButton;
	private MagicToolBarButton addCheckPaymentButton;
	private MagicToolBarButton deleteCheckPaymentButton;
	private MagicToolBarButton addAdjustmentButton;
	private MagicToolBarButton deleteAdjustmentButton;
	private MagicToolBarButton cancelButton;
	private MagicToolBarButton postButton;
	private JButton printPreviewButton;
	private JButton printButton;
	private JTabbedPane tabbedPane;
	
	@Override
	protected void initializeComponents() {
		supplierPaymentNumberField = new JLabel();
		statusLabel = new JLabel();
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		selectSupplierButton = new EllipsisButton("Select Customer");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});;
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
		
		initializeModelListeners();
	}

	@Override
	protected void registerKeyBindings() {
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_SUPPLIER_ACTION_NAME);
		supplierCodeField.getActionMap().put(SAVE_SUPPLIER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSupplier();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME);
		supplierCodeField.getActionMap().put(OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
	}

	private void saveSupplier() {
		if (supplierPayment.getSupplier() != null) {
			if (supplierPayment.getSupplier().getCode().equals(supplierCodeField.getText())) {
				// skip saving since there is no change in supplier
				return;
			}
		}
		
		if (StringUtils.isEmpty(supplierCodeField.getText())) {
			showErrorMessage("Supplier must be specified");
			return;
		}
		
		Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
		if (supplier == null) {
			showErrorMessage("No supplier matching code specified");
			return;
		} else {
			supplierPayment.setSupplier(supplier);
			supplierPaymentService.save(supplierPayment);
			updateDisplay(supplierPayment);
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					tabbedPane.requestFocusInWindow();
					tabbedPane.setSelectedIndex(0);
				}
			});
		}
	}

	protected void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			if (supplierPayment.getSupplier() != null && supplierPayment.getSupplier().equals(supplier)) {
				// skip saving since there is no change
				return;
			}
			supplierPayment.setSupplier(supplier);
			supplierPaymentService.save(supplierPayment);
			updateDisplay(supplierPayment);
		}
	}

	@Override
	protected void doOnBack() {
		if (receivingReceiptsTable.isEditing()) {
			receivingReceiptsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToSupplierPaymentListPanel();
	}
	
	public void updateDisplay(SupplierPayment supplierPayment) {
		tabbedPane.setSelectedIndex(0);
		
		if (supplierPayment.getId() == null) {
			this.supplierPayment = supplierPayment;
			clearDisplay();
			return;
		}
		
		this.supplierPayment = supplierPayment = supplierPaymentService.getSupplierPayment(supplierPayment.getId());
		
		supplierPaymentNumberField.setText(supplierPayment.getSupplierPaymentNumber().toString());
		statusLabel.setText(supplierPayment.getStatus());
		supplierCodeField.setText(supplierPayment.getSupplier().getCode());
		supplierCodeField.setEnabled(!supplierPayment.isPosted());
		supplierNameLabel.setText(supplierPayment.getSupplier().getName());
		totalAmountLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalAmount()));
		totalCashPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalCashPayments()));
		totalCreditCardPaymentsLabel.setText(
				FormatterUtil.formatAmount(supplierPayment.getTotalCreditCardPayments()));
		totalCheckPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalCheckPayments()));
		totalPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalPayments()));
		totalAdjustmentsField.setText(FormatterUtil.formatAmount(supplierPayment.getTotalAdjustments()));
		overOrShortLabel.setText(FormatterUtil.formatAmount(supplierPayment.getOverOrShort()));
		
		receivingReceiptsTable.setSupplierPayment(supplierPayment);
		cashPaymentsTable.setSupplierPayment(supplierPayment);
		creditCardPaymentsTable.setSupplierPayment(supplierPayment);
		checkPaymentsTable.setPayment(supplierPayment);
//		adjustmentsTable.setPayment(supplierPayment);
		
		boolean newPayment = !supplierPayment.isPosted();
		selectSupplierButton.setEnabled(newPayment);
		cancelButton.setEnabled(newPayment);
		postButton.setEnabled(newPayment);
		addReceivingReceiptButton.setEnabled(newPayment);
		removeReceivingReceiptButton.setEnabled(newPayment);
		addCashPaymentButton.setEnabled(newPayment);
		removeCashPaymentButton.setEnabled(newPayment);
		addCreditCardPaymentButton.setEnabled(newPayment);
		removeCreditCardPaymentButton.setEnabled(newPayment);
		addCheckPaymentButton.setEnabled(newPayment);
		deleteCheckPaymentButton.setEnabled(newPayment);
		addAdjustmentButton.setEnabled(newPayment);
		deleteAdjustmentButton.setEnabled(newPayment);
		
		printPreviewButton.setEnabled(!supplierPayment.isCancelled());
		printButton.setEnabled(!supplierPayment.isCancelled());
	}

	private void clearDisplay() {
		supplierPaymentNumberField.setText(null);
		supplierCodeField.setEnabled(true);
		supplierCodeField.setText(null);
		supplierNameLabel.setText(null);
		selectSupplierButton.setEnabled(true);
		
		receivingReceiptsTable.clearDisplay();
		cashPaymentsTable.clearDisplay();
		creditCardPaymentsTable.clearDisplay();
		checkPaymentsTable.clearDisplay();
		adjustmentsTable.clearDisplay();
		
		totalAmountLabel.setText(null);
		totalCashPaymentsLabel.setText(null);
		totalCreditCardPaymentsLabel.setText(null);
		totalCheckPaymentsLabel.setText(null);
		totalPaymentsLabel.setText(null);
		totalAdjustmentsField.setText(null);
		overOrShortLabel.setText(null);
		
		addReceivingReceiptButton.setEnabled(false);
		removeReceivingReceiptButton.setEnabled(false);
		addCashPaymentButton.setEnabled(false);
		removeCashPaymentButton.setEnabled(false);
		addCreditCardPaymentButton.setEnabled(false);
		removeCreditCardPaymentButton.setEnabled(false);
		addCheckPaymentButton.setEnabled(false);
		deleteCheckPaymentButton.setEnabled(false);
		addAdjustmentButton.setEnabled(false);
		deleteAdjustmentButton.setEnabled(false);
		
		cancelButton.setEnabled(false);
		postButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		printButton.setEnabled(false);
	}

	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectSupplierButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameLabel.setPreferredSize(new Dimension(300, 20));
		panel.add(supplierNameLabel, c);
		
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
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Supplier Payment No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		supplierPaymentNumberField = ComponentUtil.createLabel(150, "");
		mainPanel.add(supplierPaymentNumberField, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusLabel = ComponentUtil.createLabel(150, "");
		mainPanel.add(statusLabel, c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 3;
		
		supplierCodeField.setPreferredSize(new Dimension(140, 25));
		supplierNameLabel = ComponentUtil.createLabel(190, "");
		
		mainPanel.add(createSupplierPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 8;
		
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 250));
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
		c.gridwidth = 8;
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
				checkPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteCheckPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCheckPayment() {
		checkPaymentsTable.addNewRow();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		cancelButton = new MagicToolBarButton("cancel", "Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelPayment();
			}
		});
		toolBar.add(cancelButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPayment();
			}
		});
		
		toolBar.add(postButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPaymentSummary();
			}
		});	
		toolBar.add(printButton);
	}

	private void printPaymentSummary() {
//		printService.print(supplierPayment);
	}

	private void printPreview() {
//		cancelEditing();
//		printPreviewDialog.updateDisplay(printService.generateReportAsString(supplierPayment));
//		printPreviewDialog.setUseCondensedFontForPrinting(true);
//		printPreviewDialog.setVisible(true);
	}

	private void cancelEditing() {
		if (receivingReceiptsTable.isEditing()) {
			receivingReceiptsTable.getCellEditor().cancelCellEditing();
		}
		if (cashPaymentsTable.isEditing()) {
			cashPaymentsTable.getCellEditor().cancelCellEditing();
		}
		if (checkPaymentsTable.isEditing()) {
			checkPaymentsTable.getCellEditor().cancelCellEditing();
		}
		if (adjustmentsTable.isEditing()) {
			adjustmentsTable.getCellEditor().cancelCellEditing();
		}
	}

	private void postPayment() {
		cancelEditing();
		
//		if (supplierPayment.getTotalPayments().equals(Constants.ZERO)) {
//			showErrorMessage("Cannot post with no cash or check payments");
//			return;
//		}

		if (confirm(getPostConfirmMessage())) {
			try {
				supplierPaymentService.post(supplierPayment);
				showMessage("Payment posted");
				updateDisplay(supplierPayment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	private String getPostConfirmMessage() {
		String message = "Do you want to post this Payment?";
//		BigDecimal overOrShort = supplierPayment.getOverOrShort();
//		if (overOrShort.compareTo(Constants.ZERO) != 0) {
//			message = "This payment is over/short by {0}.\nAre you sure you want to post this payment?";
//			message = MessageFormat.format(message, FormatterUtil.formatAmount(overOrShort));
//		}
		return message;
	}
	
	private void cancelPayment() {
//		if (confirm("Cancel Payment?")) {
//			try {
//				supplierPaymentService.cancel(supplierPayment);
//				showMessage("Payment cancelled");
//				getMagicFrame().switchToSupplierPaymentPanel(supplierPayment);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//				showMessageForUnexpectedError();
//			}
//		}
	}

	private JPanel createReceivingReceiptsTableToolBar() {
		JPanel panel = new JPanel();
		
		addReceivingReceiptButton = new MagicToolBarButton("plus_small", "Add Item (F10)", true);
		addReceivingReceiptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addReceivingReceipt();
			}
		});
		panel.add(addReceivingReceiptButton, BorderLayout.WEST);
		
		removeReceivingReceiptButton = new MagicToolBarButton("minus_small", "Delete Item (Delete)", true);
		removeReceivingReceiptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				receivingReceiptsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(removeReceivingReceiptButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addReceivingReceipt() {
		addReceivingReceiptsToSupplierPaymentDialog.searchReceivingReceiptsForPayment(supplierPayment.getSupplier());
		addReceivingReceiptsToSupplierPaymentDialog.setVisible(true);
		
		List<ReceivingReceipt> receivingReceipts = 
				addReceivingReceiptsToSupplierPaymentDialog.getSelectedReceivingReceipts();
		if (!receivingReceipts.isEmpty()) {
			for (ReceivingReceipt receivingReceipt : receivingReceipts) {
				SupplierPaymentReceivingReceipt paymentReceivingReceipt = new SupplierPaymentReceivingReceipt();
				paymentReceivingReceipt.setParent(supplierPayment);
				paymentReceivingReceipt.setReceivingReceipt(receivingReceipt);
				supplierPaymentService.save(paymentReceivingReceipt);
				supplierPayment.getReceivingReceipts().add(paymentReceivingReceipt);
			}
			receivingReceiptsTable.setSupplierPayment(supplierPayment);
		}
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Receiving Receipts", createReceivingReceiptsPanel());
		tabbedPane.addTab("Cash Payments", createCashPaymentsPanel());
		tabbedPane.addTab("Credit Card Payments", createCreditCardPaymentsPanel());
		tabbedPane.addTab("Check Payments", createCheckPaymentsPanel());
		tabbedPane.addTab("Adjustments", createAdjustmentsPanel());
		return tabbedPane;
	}

	private JPanel createReceivingReceiptsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createReceivingReceiptsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane receivingReceiptsTableScrollPane = new JScrollPane(receivingReceiptsTable);
		receivingReceiptsTableScrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(receivingReceiptsTableScrollPane, c);
		
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
		JScrollPane scrollPane = new JScrollPane(checkPaymentsTable);
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
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Cash Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCashPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCashPaymentsLabel, c);
		
		c = new GridBagConstraints();
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
		totalAmountLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(200, "Total Credit Card Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCreditCardPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCreditCardPaymentsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Payment:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalPaymentsLabel, c);
		
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
		totalCheckPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCheckPaymentsLabel, c);
		
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
		overOrShortLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(overOrShortLabel, c);
		
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
	
	private JPanel createCreditCardPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createCreditCardPaymentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(creditCardPaymentsTable);
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
		
		removeCashPaymentButton = new MagicToolBarButton("minus_small", "Delete Cash Payment", true);
		removeCashPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cashPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(removeCashPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private JPanel createCreditCardPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addCreditCardPaymentButton = new MagicToolBarButton("plus_small", "Add Credit Card Payment", true);
		addCreditCardPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addCreditCardPayment();
			}
		});
		panel.add(addCreditCardPaymentButton, BorderLayout.WEST);
		
		removeCreditCardPaymentButton = new MagicToolBarButton("minus_small", "Remove Credit Card Payment", true);
		removeCreditCardPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				creditCardPaymentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(removeCreditCardPaymentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addCreditCardPayment() {
		creditCardPaymentsTable.addNewRow();
	}

	private void addCashPayment() {
		cashPaymentsTable.addNewRow();
	}

	private JPanel createAdjustmentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createAdjustmentsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(adjustmentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createAdjustmentsTableToolBar() {
		JPanel panel = new JPanel();
		
		addAdjustmentButton = new MagicToolBarButton("plus_small", "Add Adjustment", true);
		addAdjustmentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAdjustment();
			}
		});
		panel.add(addAdjustmentButton, BorderLayout.WEST);
		
		deleteAdjustmentButton = new MagicToolBarButton("minus_small", "Delete Adjustment", true);
		deleteAdjustmentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				adjustmentsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteAdjustmentButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addAdjustment() {
		adjustmentsTable.addNewRow();
	}

	private void initializeModelListeners() {
		receivingReceiptsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalAmount()));
			}
		});
		
		cashPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCashPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalCashPayments()));
				totalPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalPayments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(supplierPayment.getOverOrShort()));
			}
		});

		creditCardPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCreditCardPaymentsLabel.setText(
						FormatterUtil.formatAmount(supplierPayment.getTotalCreditCardPayments()));
				totalPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalPayments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(supplierPayment.getOverOrShort()));
			}
		});
		
		checkPaymentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalCheckPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalCheckPayments()));
				totalPaymentsLabel.setText(FormatterUtil.formatAmount(supplierPayment.getTotalPayments()));
				overOrShortLabel.setText(FormatterUtil.formatAmount(supplierPayment.getOverOrShort()));
			}
		});

		/*
		adjustmentsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAdjustmentsField.setText(FormatterUtil.formatAmount(supplierPayment.getTotalAdjustments()));
				overOrShortField.setText(FormatterUtil.formatAmount(supplierPayment.getOverOrShort()));
			}
		});
		*/
	}
	
}