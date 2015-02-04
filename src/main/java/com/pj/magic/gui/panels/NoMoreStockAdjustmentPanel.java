package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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
import com.pj.magic.gui.tables.NoMoreStockAdjustmentItemsTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class NoMoreStockAdjustmentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(NoMoreStockAdjustmentPanel.class);
	
	@Autowired private NoMoreStockAdjustmentItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private NoMoreStockAdjustmentService noMoreStockAdjustmentService;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalService paymentTerminalService;	
	
	private NoMoreStockAdjustment noMoreStockAdjustment;
	private JLabel noMoreStockAdjustmentNumberField;
	private MagicTextField salesInvoiceNumberField;
	private JLabel customerField;
	private JLabel statusField;
	private JLabel postDateField;
	private JLabel postedByField;
	private MagicTextField remarksField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton markAsPaidButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	
	@Override
	protected void initializeComponents() {
		noMoreStockAdjustmentNumberField = new JLabel();
		
		salesInvoiceNumberField = new MagicTextField();
		salesInvoiceNumberField.setNumbersOnly(true);
		
		customerField = new JLabel();
		statusField = new JLabel();
		postDateField = new JLabel();
		postedByField = new JLabel();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(salesInvoiceNumberField);
		
		updateTotalFieldsWhenItemsTableChanges();
	}

	private void saveRemarks() {
		if (!remarksField.getText().equals(noMoreStockAdjustment.getRemarks())) {
			noMoreStockAdjustment.setRemarks(remarksField.getText());
			noMoreStockAdjustmentService.save(noMoreStockAdjustment);
		}
	}

	@Override
	protected void registerKeyBindings() {
		salesInvoiceNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSalesInvoiceNumber();
			}
		});
		
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
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
		if (noMoreStockAdjustment.getId() != null 
				&& noMoreStockAdjustment.getSalesInvoice().getSalesInvoiceNumber().equals(salesInvoiceNumber)) {
			// no changes
			remarksField.requestFocusInWindow();
			return;
		}
		
		SalesInvoice salesInvoice = salesInvoiceService.findBySalesInvoiceNumber(salesInvoiceNumber);
		if (!validateSalesInvoice(salesInvoice)) {
			salesInvoiceNumberField.requestFocusInWindow();
			return;
		}
		
		noMoreStockAdjustment.setSalesInvoice(salesInvoice);
		try {
			noMoreStockAdjustmentService.save(noMoreStockAdjustment);
			updateDisplay(noMoreStockAdjustment);
			remarksField.requestFocusInWindow();
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
		getMagicFrame().switchToNoMoreStockAdjustmentListPanel();
	}
	
	private void updateTotalFieldsWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(noMoreStockAdjustment.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(noMoreStockAdjustment.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(NoMoreStockAdjustment noMoreStockAdjustment) {
		if (noMoreStockAdjustment.getId() == null) {
			this.noMoreStockAdjustment = noMoreStockAdjustment;
			clearDisplay();
			return;
		}
		
		this.noMoreStockAdjustment = noMoreStockAdjustment = noMoreStockAdjustmentService.getNoMoreStockAdjustment(noMoreStockAdjustment.getId());
		
		noMoreStockAdjustmentNumberField.setText(noMoreStockAdjustment.getNoMoreStockAdjustmentNumber().toString());
		salesInvoiceNumberField.setEnabled(!noMoreStockAdjustment.isPosted());
		salesInvoiceNumberField.setText(noMoreStockAdjustment.getSalesInvoice().getSalesInvoiceNumber().toString());
		statusField.setText(noMoreStockAdjustment.getStatus());
		
		Customer customer = noMoreStockAdjustment.getSalesInvoice().getCustomer();
		customerField.setText(customer.getCode() + " - " + customer.getName());
		
		postDateField.setText(noMoreStockAdjustment.isPosted() ? FormatterUtil.formatDate(noMoreStockAdjustment.getPostDate()) : null);
		postedByField.setText(noMoreStockAdjustment.isPosted() ? noMoreStockAdjustment.getPostedBy().getUsername() : null);
		remarksField.setText(noMoreStockAdjustment.getRemarks());
		remarksField.setEnabled(!noMoreStockAdjustment.isPosted());
		
		itemsTable.setNoMoreStockAdjustment(noMoreStockAdjustment);
		
		postButton.setEnabled(!noMoreStockAdjustment.isPosted());
		markAsPaidButton.setEnabled(noMoreStockAdjustment.isPosted() && !noMoreStockAdjustment.isPaid());
		addItemButton.setEnabled(!noMoreStockAdjustment.isPosted());
		deleteItemButton.setEnabled(!noMoreStockAdjustment.isPosted());
	}

	private void clearDisplay() {
		noMoreStockAdjustmentNumberField.setText(null);
		salesInvoiceNumberField.setEnabled(true);
		salesInvoiceNumberField.setText(null);
		statusField.setText(null);
		customerField.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		remarksField.setText(null);
		remarksField.setEnabled(false);
		itemsTable.setNoMoreStockAdjustment(noMoreStockAdjustment);
		postButton.setEnabled(false);
		markAsPaidButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
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
		mainPanel.add(ComponentUtil.createLabel(100, "NMS No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		noMoreStockAdjustmentNumberField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(noMoreStockAdjustmentNumberField, c);
		
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
		statusField = ComponentUtil.createLabel(150);
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
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(remarksField, c);
		
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
				postNoMoreStockAdjustment();
			}
		});
		toolBar.add(postButton);
		
		markAsPaidButton = new MagicToolBarButton("coins", "Mark As Paid");
		markAsPaidButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markNoMoreStockAdjustmentAsPaid();
			}
		});
		toolBar.add(markAsPaidButton);
	}

	private void markNoMoreStockAdjustmentAsPaid() {
		if (!isUserAssignedToPaymentTerminal()) {
			showErrorMessage("User is not assigned to a payment terminal");
			return;
		}
		
		if (confirm("Mark No More Stock Adjustment as paid?")) {
			try {
				noMoreStockAdjustmentService.markAsPaid(noMoreStockAdjustment);
				JOptionPane.showMessageDialog(this, "No More Stock Adjustment marked as paid");
				updateDisplay(noMoreStockAdjustment);
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

	private void postNoMoreStockAdjustment() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (!noMoreStockAdjustment.hasItems()) {
			showErrorMessage("Cannot post a No More Stock Adjustment with no items");
			itemsTable.requestFocusInWindow();
			return;
		}
		
		if (confirm("Do you want to post this No More Stock Adjustment?")) {
			try {
				noMoreStockAdjustmentService.post(noMoreStockAdjustment);
				JOptionPane.showMessageDialog(this, "No More Stock Adjustment posted");
				updateDisplay(noMoreStockAdjustment);
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