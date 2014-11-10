package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.CustomersTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.service.CustomerService;

@Component
public class SelectCustomerDialog extends MagicDialog {

	private static final String SELECT_CUSTOMER_ACTION_NAME = "selectCustomer";
	
	@Autowired private CustomerService customerService;
	
	private Customer selectedCustomer;
	private JTable customersTable;
	private CustomersTableModel customersTableModel = new CustomersTableModel();
	
	public SelectCustomerDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Customer");
		addContents();
	}

	private void addContents() {
		customersTable = new MagicListTable(customersTableModel);
		
		customersTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_CUSTOMER_ACTION_NAME);
		customersTable.getActionMap().put(SELECT_CUSTOMER_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});
		
		customersTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectCustomer();
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(customersTable);
		add(scrollPane);	
	}

	protected void selectCustomer() {
		selectedCustomer = customersTableModel.getCustomer(customersTable.getSelectedRow());
		setVisible(false);
	}

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedCustomer = null;
	}
	
	public void searchCustomers(String customerCode) {
		List<Customer> customers = customerService.getAllCustomers();
		customersTableModel.setCustomers(customers);
		
		int selectedRow = 0;
		if (!StringUtils.isEmpty(customerCode)) {
			Customer selectedCustomer = customerService.findFirstCustomerWithCodeLike(customerCode);
			if (selectedCustomer != null) {
				selectedRow = customers.indexOf(selectedCustomer);
			}
		}
		customersTable.changeSelection(selectedRow, 0, false, false);
	}
	
}
