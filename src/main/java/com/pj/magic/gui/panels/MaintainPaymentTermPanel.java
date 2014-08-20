package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainPaymentTermPanel extends AbstractMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainPaymentTermPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private PaymentTermService paymentTermService;
	
	private PaymentTerm paymentTerm;
	private MagicTextField nameField;
	private MagicTextField numberOfDaysField;
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		nameField = new MagicTextField();
		nameField.setMaximumLength(30);
		
		numberOfDaysField = new MagicTextField();
		numberOfDaysField.setMaximumLength(3);
		numberOfDaysField.setNumbersOnly(true);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentTerm();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(nameField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(numberOfDaysField);
		focusOrder.add(saveButton);
	}
	
	protected void savePaymentTerm() {
		if (!validatePaymentTerm()) {
			return;
		}
		
		int confirm = showConfirmMessage("Save?");
		if (confirm == JOptionPane.OK_OPTION) {
			paymentTerm.setName(nameField.getText());
			
			try {
				paymentTermService.save(paymentTerm);
				showMessage("Saved!");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validatePaymentTerm() {
		try {
			validateMandatoryField(nameField, "Name");
			validateMandatoryField(numberOfDaysField, "Number of Days");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Name: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(200, 20));
		add(nameField, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 1), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Number of Days: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		numberOfDaysField.setPreferredSize(new Dimension(50, 20));
		add(numberOfDaysField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 20));
		add(saveButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 1), c);
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
				savePaymentTerm();
			}
		});
	}

	public void updateDisplay(PaymentTerm category) {
		this.paymentTerm = category;
		if (category.getId() == null) {
			clearDisplay();
			return;
		}
		
		nameField.setText(category.getName());
	}

	private void clearDisplay() {
		nameField.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPaymentTermListPanel();
	}

}
