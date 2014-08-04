package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.CustomersTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.service.CustomerService;

@Component
public class SelectCustomerDialog extends MagicDialog {

	private static final String SELECT_CUSTOMER_ACTION_NAME = "selectCustomer";
	
	@Autowired private CustomerService customerService;
	
	private Customer selectedCustomer;
	private JTable table;
	private CustomersTableModel customersTableModel = new CustomersTableModel();
	
	public SelectCustomerDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Customer");
		addContents();
	}

	private void addContents() {
		table = new JTable(customersTableModel);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_CUSTOMER_ACTION_NAME);
		table.getActionMap().put(SELECT_CUSTOMER_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedCustomer = customersTableModel.getCustomer(table.getSelectedRow());
				setVisible(false);
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedCustomer = null;
	}
	
	public void updateAndMakeVisible() {
		customersTableModel.setCustomers(customerService.getAllCustomers());
		table.setRowSelectionInterval(0, 0);
		setVisible(true);
	}
	
}
