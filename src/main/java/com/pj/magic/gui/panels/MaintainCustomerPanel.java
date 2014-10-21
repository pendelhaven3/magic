package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainCustomerPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainCustomerPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private CustomerService customerService;
	@Autowired private PaymentTermService paymentTermService;
	
	private Customer customer;
	private MagicTextField codeField;
	private MagicTextField nameField;
	private MagicTextField addressField;
	private MagicTextField contactPersonField;
	private MagicTextField contactNumberField;
	private JComboBox<PaymentTerm> paymentTermComboBox;
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setMaximumLength(12);
		
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		addressField = new MagicTextField();
		addressField.setMaximumLength(50);
		
		contactPersonField = new MagicTextField();
		contactPersonField.setMaximumLength(100);
		
		contactNumberField = new MagicTextField();
		contactNumberField.setMaximumLength(100);
		
		paymentTermComboBox = new JComboBox<>();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(codeField);
		focusOrder.add(nameField);
		focusOrder.add(addressField);
		focusOrder.add(contactPersonField);
		focusOrder.add(contactNumberField);
		focusOrder.add(paymentTermComboBox);
		focusOrder.add(saveButton);
	}
	
	protected void saveCustomer() {
		if (!validateCustomer()) {
			return;
		}
		
		if (confirm("Save?")) {
			customer.setCode(codeField.getText());
			customer.setName(nameField.getText());
			customer.setAddress(addressField.getText());
			customer.setContactPerson(contactPersonField.getText());
			customer.setContactNumber(contactNumberField.getText());
			customer.setPaymentTerm((PaymentTerm)paymentTermComboBox.getSelectedItem());
			
			try {
				customerService.save(customer);
				showMessage("Saved!");
				codeField.requestFocusInWindow();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateCustomer() {
		try {
			validateMandatoryField(codeField, "Code");
			validateMandatoryField(nameField, "Name");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Code: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(codeField, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Name: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(nameField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Address: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		addressField.setPreferredSize(new Dimension(300, 20));
		mainPanel.add(addressField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Contact Person: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		contactPersonField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(contactPersonField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Contact Number: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		contactNumberField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(contactNumberField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Payment Term: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermComboBox.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(paymentTermComboBox, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
	}

	@Override
	protected void registerKeyBindings() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
	}

	public void updateDisplay(Customer customer) {
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		paymentTermComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerms.toArray(new PaymentTerm[paymentTerms.size()])));
		
		this.customer = customer;
		if (customer.getId() == null) {
			clearDisplay();
			return;
		}
		
		codeField.setText(customer.getCode());
		nameField.setText(customer.getName());
		addressField.setText(customer.getAddress());
		contactPersonField.setText(customer.getContactPerson());
		contactNumberField.setText(customer.getContactNumber());
		paymentTermComboBox.setSelectedItem(customer.getPaymentTerm());
	}

	private void clearDisplay() {
		codeField.setText(null);
		nameField.setText(null);
		addressField.setText(null);
		contactPersonField.setText(null);
		contactNumberField.setText(null);
		paymentTermComboBox.setSelectedItem(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCustomerListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
