package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.CustomersTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.service.CustomerService;
import com.pj.magic.util.ComponentUtil;

@Component
public class CustomerListPanel extends AbstractMagicPanel implements ActionListener {

	private static final String EDIT_CUSTOMER_ACTION_NAME = "editCustomer";
	private static final String NEW_CUSTOMER_ACTION_NAME = "newCustomer";
	
	@Autowired private CustomerService customerService;
	@Autowired private CustomersTableModel tableModel;
	
	private JTable table;
	
	public void updateDisplay() {
		List<Customer> customers = customerService.getAllCustomers();
		tableModel.setCustomers(customers);
		if (!customers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		table = new JTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);

		currentRow++; // first row
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(new JScrollPane(table), c);
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new MagicToolBar();
		
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.setActionCommand(NEW_CUSTOMER_ACTION_NAME);
		postButton.addActionListener(this);
		
		toolBar.add(postButton);
		return toolBar;
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_CUSTOMER_ACTION_NAME);
		table.getActionMap().put(EDIT_CUSTOMER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Customer customer = tableModel.getCustomer(table.getSelectedRow());
				getMagicFrame().switchToEditCustomerPanel(customer);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case NEW_CUSTOMER_ACTION_NAME:
			switchToNewCustomerPanel();
			break;
		}
	}

	private void switchToNewCustomerPanel() {
		getMagicFrame().switchToAddNewCustomerPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
}
