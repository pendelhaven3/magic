package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.UnpaidSalesInvoicesTable;
import com.pj.magic.gui.tables.models.AccountsReceivableSummary;
import com.pj.magic.gui.tables.models.UnpaidSalesInvoicesTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.AccountsReceivableService;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.ComponentUtil;

@Component
public class CreateAccountsReceivableSummaryPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(CreateAccountsReceivableSummaryPanel.class);
	
	@Autowired private CustomerService customerService;
	@Autowired private PaymentService paymentService;
	@Autowired private AccountsReceivableService accountsReceivableService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	
	private MagicTextField customerCodeField;
	private JLabel customerNameField;
	private JButton showUnpaidSalesInvoicesButton;
	private EllipsisButton selectCustomerButton;
	private JButton createSummaryButton;
	private Customer customer;
	private UnpaidSalesInvoicesTable table;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameField = new JLabel();
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		showUnpaidSalesInvoicesButton = new JButton("Show Unpaid Sales Invoices");
		showUnpaidSalesInvoicesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showUnpaidSalesInvoices();
			}
		});
		
		createSummaryButton = new JButton("Create Summary");
		createSummaryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createSummary();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		UnpaidSalesInvoicesTableModel tableModel = new UnpaidSalesInvoicesTableModel();
		table = new UnpaidSalesInvoicesTable(tableModel); // TODO: investigate why it can't reuse
	}

	private void createSummary() {
		List<SalesInvoice> salesInvoices = table.getSelectedSalesInvoices();
		if (salesInvoices.isEmpty()) {
			showErrorMessage("No Sales Invoice selected");
			return;
		}
		
		AccountsReceivableSummary summary = new AccountsReceivableSummary();
		summary.setCustomer(customer);
		for (SalesInvoice salesInvoice : salesInvoices) {
			summary.add(salesInvoice);
		}
		
		try {
			accountsReceivableService.save(summary);
			showMessage("Accounts Receivable Summary saved");
			getMagicFrame().switchToAccountsReceivableSummaryPanel(summary);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage("Unexpected error occurred");
		}
	}

	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameField.setText(customer.getName());
		}
	}

	private void showUnpaidSalesInvoices() {
		table.clearDisplay();
		
		String customerCode = customerCodeField.getText();
		if (StringUtils.isEmpty(customerCode)) {
			showErrorMessage("Customer Code must be specified");
			return;
		}
		
		customer = customerService.findCustomerByCode(customerCode);
		if (customer == null) {
			showErrorMessage("No customer matching code specified");
			return;
		}
		
		List<SalesInvoice> salesInvoices = paymentService.findAllUnpaidSalesInvoicesByCustomer(customer);
		table.setSalesInvoices(salesInvoices);
		if (salesInvoices.isEmpty()) {
			showErrorMessage("No records found");
		}
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(50, 1), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		showUnpaidSalesInvoicesButton.setPreferredSize(new Dimension(240, 25));
		mainPanel.add(showUnpaidSalesInvoicesButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 150));
		mainPanel.add(itemsTableScrollPane, c);
		
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
		c.anchor = GridBagConstraints.CENTER;
		createSummaryButton.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(createSummaryButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
	}

	private JPanel createCustomerPanel() {
		customerCodeField.setPreferredSize(new Dimension(150, 25));
		customerNameField.setPreferredSize(new Dimension(200, 25));
		
		JPanel panel = new JPanel();
		panel.add(customerCodeField);
		panel.add(selectCustomerButton);
		panel.add(ComponentUtil.createHorizontalFiller(5));
		panel.add(customerNameField);
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
		customerCodeField.setText(null);
		customerNameField.setText(null);
		customer = null;
		table.clearDisplay();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
