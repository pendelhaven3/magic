package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.SalesReturnItemsTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesReturnPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(SalesReturnPanel.class);
	
	@Autowired private SalesReturnItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalService paymentTerminalService;	
	
	private SalesReturn salesReturn;
	private JLabel salesReturnNumberField;
	private MagicTextField salesInvoiceNumberField;
	private JLabel customerField;
	private JLabel statusField;
	private JLabel postDateField;
	private JLabel postedByField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton markAsPaidButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton printButton;
	private JButton printPreviewButton;
	
	@Override
	protected void initializeComponents() {
		salesReturnNumberField = new JLabel();
		
		salesInvoiceNumberField = new MagicTextField();
		salesInvoiceNumberField.setNumbersOnly(true);
		
		customerField = new JLabel();
		statusField = new JLabel();
		postDateField = new JLabel();
		postedByField = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(salesInvoiceNumberField);
		
		updateTotalFieldsWhenItemsTableChanges();
	}

	@Override
	protected void registerKeyBindings() {
		salesInvoiceNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSalesInvoiceNumber();
			}
		});
	}

	private void saveSalesInvoiceNumber() {
		if (StringUtils.isEmpty(salesInvoiceNumberField.getText())) {
			showErrorMessage("Sales Invoice No. must be specified");
			salesInvoiceNumberField.requestFocusInWindow();
			return;
		}
		
		Long salesInvoiceNumber = Long.valueOf(salesInvoiceNumberField.getText());
		if (salesReturn.getId() != null 
				&& salesReturn.getSalesInvoice().getSalesInvoiceNumber().equals(salesInvoiceNumber)) {
			// no changes
			itemsTable.highlight();
			return;
		}
		
		SalesInvoice salesInvoice = salesInvoiceService.findBySalesInvoiceNumber(salesInvoiceNumber);
		if (!validateSalesInvoice(salesInvoice)) {
			salesInvoiceNumberField.requestFocusInWindow();
			return;
		}
		
		salesReturn.setSalesInvoice(salesInvoice);
		try {
			salesReturnService.save(salesReturn);
			updateDisplay(salesReturn);
			itemsTable.highlight();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage(Constants.UNEXPECTED_ERROR_MESSAGE);
			return;
		}
	}

	private boolean validateSalesInvoice(SalesInvoice salesInvoice) {
		boolean valid = false;
		if (salesInvoice == null) {
			showErrorMessage("No record matching Sales Invoice No. specified");
		} else if (salesInvoice.isCancelled()) {
			showErrorMessage("Sales Invoice is already cancelled");
		} else {
			valid = true;
		}
		return valid;
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesReturnListPanel();
	}
	
	private void updateTotalFieldsWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(salesReturn.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(salesReturn.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(SalesReturn salesReturn) {
		if (salesReturn.getId() == null) {
			this.salesReturn = salesReturn;
			clearDisplay();
			return;
		}
		
		this.salesReturn = salesReturn = salesReturnService.getSalesReturn(salesReturn.getId());
		
		salesReturnNumberField.setText(salesReturn.getSalesReturnNumber().toString());
		salesInvoiceNumberField.setEnabled(!salesReturn.isPosted());
		salesInvoiceNumberField.setText(salesReturn.getSalesInvoice().getSalesInvoiceNumber().toString());
		statusField.setText(salesReturn.getStatus());
		
		Customer customer = salesReturn.getSalesInvoice().getCustomer();
		customerField.setText(customer.getCode() + " - " + customer.getName());
		
		postDateField.setText(salesReturn.isPosted() ? FormatterUtil.formatDate(salesReturn.getPostDate()) : null);
		postedByField.setText(salesReturn.isPosted() ? salesReturn.getPostedBy().getUsername() : null);
		
		itemsTable.setSalesReturn(salesReturn);
		
		postButton.setEnabled(!salesReturn.isPosted());
		markAsPaidButton.setEnabled(salesReturn.isPosted() && !salesReturn.isPaid());
		addItemButton.setEnabled(!salesReturn.isPosted());
		deleteItemButton.setEnabled(!salesReturn.isPosted());
		printButton.setEnabled(true);
		printPreviewButton.setEnabled(true);
	}

	private void clearDisplay() {
		salesReturnNumberField.setText(null);
		salesInvoiceNumberField.setEnabled(true);
		salesInvoiceNumberField.setText(null);
		statusField.setText(null);
		customerField.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		itemsTable.setSalesReturn(salesReturn);
		postButton.setEnabled(false);
		markAsPaidButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
		printButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
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
		mainPanel.add(ComponentUtil.createLabel(100, "SR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesReturnNumberField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(salesReturnNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(100);
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Sales Invoice No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesInvoiceNumberField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(salesInvoiceNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateField = ComponentUtil.createLabel(100);
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(customerField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField = ComponentUtil.createLabel(100);
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createHorizontalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postSalesReturn();
			}
		});
		toolBar.add(postButton);
		
		markAsPaidButton = new MagicToolBarButton("coins", "Mark As Paid");
		markAsPaidButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markSalesReturnAsPaid();
			}
		});
		toolBar.add(markAsPaidButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateReportAsString(salesReturn));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(salesReturn);
			}
		});
		toolBar.add(printButton);
	}

	private void markSalesReturnAsPaid() {
		if (!isUserAssignedToPaymentTerminal()) {
			showErrorMessage("User is not assigned to a payment terminal");
			return;
		}
		
		if (confirm("Mark Sales Return as paid?")) {
			try {
				salesReturnService.markAsPaid(salesReturn);
				JOptionPane.showMessageDialog(this, "Sales Return marked as paid");
				updateDisplay(salesReturn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	private boolean isUserAssignedToPaymentTerminal() {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment assignment = paymentTerminalService.findPaymentTerminalAssignment(user);
		return assignment != null;
	}

	private void postSalesReturn() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (!salesReturn.hasItems()) {
			showErrorMessage("Cannot post a Sales Return with no items");
			itemsTable.requestFocusInWindow();
			return;
		}
		
		if (confirm("Do you want to post this Sales Return?")) {
			try {
				salesReturnService.post(salesReturn);
				JOptionPane.showMessageDialog(this, "Sales Return posted");
				updateDisplay(salesReturn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item (F10)", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item (Delete)", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}
	
}
