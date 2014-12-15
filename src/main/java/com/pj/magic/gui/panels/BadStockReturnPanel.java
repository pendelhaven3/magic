package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.BadStockReturnItemsTable;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Customer;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.CustomerService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class BadStockReturnPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(BadStockReturnPanel.class);
	
	private static final String SAVE_CUSTOMER_ACTION_NAME = "SAVE_CUSTOMER_ACTION_NAME";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = 
			"OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME";
	
	@Autowired private BadStockReturnItemsTable itemsTable;
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private CustomerService customerService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	
	private BadStockReturn badStockReturn;
	private JLabel badStockReturnNumberField;
	private JTextField customerCodeField;
	private JLabel customerNameField;
	private JButton selectCustomerButton;
	private JLabel statusField;
	private JLabel postDateField;
	private JLabel postedByField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerNameField = new JLabel();
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});;
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		updateTotalAmountFieldWhenItemsTableChanges();
	}

	private void selectCustomer() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (badStockReturn.getCustomer() != null && badStockReturn.getCustomer().equals(customer)) {
				// skip saving Bad Stock Return since there is no change
				itemsTable.highlight();
				return;
			} else {
				badStockReturn.setCustomer(customer);
				try {
					badStockReturnService.save(badStockReturn);
					updateDisplay(badStockReturn);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					showMessageForUnexpectedError();
				}
			}
		}
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
		if (badStockReturn.getCustomer() != null) {
			if (badStockReturn.getCustomer().getCode().equals(customerCodeField.getText())) {
				// skip saving Bad Stock Return since there is no change in customer
				itemsTable.highlight();
				return;
			}
		}
		
		if (StringUtils.isEmpty(customerCodeField.getText())) {
			showErrorMessage("Customer must be specified");
			return;
		}
		
		Customer customer = customerService.findCustomerByCode(customerCodeField.getText());
		if (customer == null || !customer.isActive()) {
			showErrorMessage("No customer matching code specified");
			return;
		} else {
			badStockReturn.setCustomer(customer);
			try {
				badStockReturnService.save(badStockReturn);
				updateDisplay(badStockReturn);
				itemsTable.highlight();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToBadStockReturnListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(badStockReturn.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(badStockReturn.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(BadStockReturn badStockReturn) {
		if (badStockReturn.getId() == null) {
			this.badStockReturn = badStockReturn;
			clearDisplay();
			return;
		}
		
		this.badStockReturn = badStockReturn = badStockReturnService.getBadStockReturn(badStockReturn.getId());
		
		badStockReturnNumberField.setText(badStockReturn.getBadStockReturnNumber().toString());
		customerCodeField.setText(badStockReturn.getCustomer().getCode());
		customerNameField.setText(badStockReturn.getCustomer().getName());
		statusField.setText(badStockReturn.getStatus());
		if (badStockReturn.getPostDate() != null) {
			postDateField.setText(FormatterUtil.formatDate(badStockReturn.getPostDate()));
		} else {
			postDateField.setText(null);
		}
		if (badStockReturn.getPostedBy() != null) {
			postedByField.setText(badStockReturn.getPostedBy().getUsername());
		} else {
			postedByField.setText(null);
		}
		totalItemsField.setText(String.valueOf(badStockReturn.getTotalItems()));
		totalAmountField.setText(badStockReturn.getTotalAmount().toString());
		postButton.setEnabled(!badStockReturn.isPosted());
		addItemButton.setEnabled(!badStockReturn.isPosted());
		deleteItemButton.setEnabled(!badStockReturn.isPosted());
		
		itemsTable.setBadStockReturn(badStockReturn);
	}

	private void clearDisplay() {
		badStockReturnNumberField.setText(null);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		statusField.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setBadStockReturn(badStockReturn);
		postButton.setEnabled(false);
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
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "BSR No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		badStockReturnNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(badStockReturnNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(100, "");
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
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
	
	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameField.setPreferredSize(new Dimension(200, 20));
		panel.add(customerNameField, c);
		
		return panel;
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
		panel.add(Box.createHorizontalStrut(10), c);
		
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
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void postBadStockReturn() {
		/*
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		int confirm = showConfirmMessage("Do you want to post this Adjustment In?");
		if (confirm == JOptionPane.OK_OPTION) {
			if (!badStockReturn.hasItems()) {
				showErrorMessage("Cannot post a Adjustment In with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			try {
				badStockReturnService.post(badStockReturn);
				JOptionPane.showMessageDialog(this, "Post successful!");
				updateDisplay(badStockReturn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
		*/
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postBadStockReturn();
			}
		});
		toolBar.add(postButton);
	}

}