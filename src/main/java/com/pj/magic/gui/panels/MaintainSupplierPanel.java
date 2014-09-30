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
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainSupplierPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainSupplierPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private SupplierService supplierService;
	@Autowired private PaymentTermService paymentTermService;
	
	private Supplier supplier;
	private MagicTextField codeField;
	private MagicTextField nameField;
	private MagicTextField addressField;
	private MagicTextField contactNumberField;
	private MagicTextField contactPersonField;
	private MagicTextField faxNumberField;
	private MagicTextField emailAddressField;
	private MagicTextField tinField;
	private MagicTextField remarksField;
	private MagicTextField discountField;
	private JComboBox<PaymentTerm> paymentTermComboBox; 
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setMaximumLength(15);
		
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		addressField = new MagicTextField();
		addressField.setMaximumLength(200);
		
		contactNumberField = new MagicTextField();
		contactNumberField.setMaximumLength(100);
		
		contactPersonField = new MagicTextField();
		contactPersonField.setMaximumLength(100);
		
		faxNumberField = new MagicTextField();
		faxNumberField.setMaximumLength(100);
		
		emailAddressField = new MagicTextField();
		emailAddressField.setMaximumLength(50);
		
		tinField = new MagicTextField();
		tinField.setMaximumLength(20);
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(200);
		
		discountField = new MagicTextField();
		discountField.setMaximumLength(200);
		
		paymentTermComboBox = new JComboBox<>(); 
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSupplier();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(codeField);
		focusOrder.add(nameField);
		focusOrder.add(addressField);
		focusOrder.add(contactNumberField);
		focusOrder.add(contactPersonField);
		focusOrder.add(faxNumberField);
		focusOrder.add(emailAddressField);
		focusOrder.add(tinField);
		focusOrder.add(paymentTermComboBox); 
		focusOrder.add(remarksField); 
		focusOrder.add(discountField); 
		focusOrder.add(saveButton);
	}
	
	protected void saveSupplier() {
		if (!validateSupplier()) {
			return;
		}
		
		int confirm = showConfirmMessage("Save?");
		if (confirm == JOptionPane.OK_OPTION) {
			supplier.setCode(codeField.getText());
			supplier.setName(nameField.getText());
			supplier.setAddress(addressField.getText());
			supplier.setContactNumber(contactNumberField.getText());
			supplier.setContactPerson(contactPersonField.getText());
			supplier.setFaxNumber(faxNumberField.getText());
			supplier.setEmailAddress(emailAddressField.getText());
			supplier.setTin(tinField.getText());
			supplier.setPaymentTerm((PaymentTerm)paymentTermComboBox.getSelectedItem());
			supplier.setRemarks(remarksField.getText());
			supplier.setDiscount(discountField.getText());
			
			try {
				supplierService.save(supplier);
				showMessage("Saved!");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateSupplier() {
		try {
			validateMandatoryField(codeField, "Code");
			validateMandatoryField(nameField, "Business Name");
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

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Code: "), c);
		
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
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Business Name: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(300, 20));
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
		mainPanel.add(ComponentUtil.createLabel(100, "Contact Number: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		contactNumberField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(contactNumberField, c);
		
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
		mainPanel.add(ComponentUtil.createLabel(100, "Fax Number: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		faxNumberField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(faxNumberField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Email Address: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		emailAddressField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(emailAddressField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "TIN: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		tinField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(tinField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Payment Term: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermComboBox.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(paymentTermComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Discount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		discountField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(discountField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 20));
		mainPanel.add(remarksField, c);
		
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
		
		paymentTermComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		paymentTermComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSupplier();
			}
		});
	}

	public void updateDisplay(Supplier supplier) {
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		paymentTermComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerms.toArray(new PaymentTerm[paymentTerms.size()])));
		paymentTermComboBox.insertItemAt(null, 0);
		
		this.supplier = supplier;
		if (supplier.getId() == null) {
			clearDisplay();
			return;
		}
		
		codeField.setText(supplier.getCode());
		nameField.setText(supplier.getName());
		addressField.setText(supplier.getAddress());
		contactNumberField.setText(supplier.getContactNumber());
		contactPersonField.setText(supplier.getContactPerson());
		faxNumberField.setText(supplier.getFaxNumber());
		emailAddressField.setText(supplier.getEmailAddress());
		tinField.setText(supplier.getTin());
		if (supplier.getPaymentTerm() != null) {
			paymentTermComboBox.setSelectedItem(supplier.getPaymentTerm());
		} else {
			paymentTermComboBox.setSelectedItem(null);
		}
		remarksField.setText(supplier.getRemarks());
		discountField.setText(supplier.getDiscount());
	}

	private void clearDisplay() {
		codeField.setText(null);
		nameField.setText(null);
		addressField.setText(null);
		contactNumberField.setText(null);
		contactPersonField.setText(null);
		faxNumberField.setText(null);
		emailAddressField.setText(null);
		tinField.setText(null);
		paymentTermComboBox.setSelectedItem(null);
		remarksField.setText(null);
		discountField.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSupplierListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
