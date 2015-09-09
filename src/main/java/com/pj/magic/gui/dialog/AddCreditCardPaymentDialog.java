package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.CreditCardPayment;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class AddCreditCardPaymentDialog extends MagicDialog {

	private static final Logger logger = LoggerFactory.getLogger(AddCreditCardPaymentDialog.class);
	
	@Autowired private CreditCardService creditCardService;
	
	private MagicTextField amountField;
	private UtilCalendarModel paymentDateModel;
	private MagicTextField remarksField;
	private JButton saveButton;
	private JButton cancelButton;
	private CreditCardPayment payment;
	
	public AddCreditCardPaymentDialog() {
		setSize(400, 200);
		setLocationRelativeTo(null);
		setTitle("Add Credit Card Payment");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}
	
	private void initializeComponents() {
		amountField = new MagicTextField();
		
		paymentDateModel = new UtilCalendarModel();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(300);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePayment();
			}
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	private void savePayment() {
		if (!validatePayment()) {
			return;
		}
		
		payment.setAmount(NumberUtil.toBigDecimal(amountField.getText()));
		payment.setPaymentDate(paymentDateModel.getValue().getTime());
		payment.setRemarks(remarksField.getText());
		
		try {
			creditCardService.save(payment);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage("Unexpected error occurred during saving");
			return;
		}
		
		showMessage("Payment saved");
		setVisible(false);
	}

	private boolean validatePayment() {
		if (amountField.getText().isEmpty()) {
			showErrorMessage("Amount must be specified");
			return false;
		}
		
		if (!NumberUtil.isAmount(amountField.getText())) {
			showErrorMessage("Invalid amount");
			return false;
		}
		
		if (paymentDateModel.getValue() == null) {
			showErrorMessage("Payment Date must be specified");
			return false;
		}
		
		return true;
	}

	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Amount:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		amountField.setPreferredSize(new Dimension(100, 25));
		add(amountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Payment Date:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePickerImpl fromDatePicker = new JDatePickerImpl(
				new JDatePanelImpl(paymentDateModel), new DatePickerFormatter());
		add(fromDatePicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Remarks:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(10), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		add(ComponentUtil.createGenericPanel(saveButton, cancelButton), c);
	}

	private void registerKeyBindings() {
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// do nothing
	}
	
	public void updateDisplay(CreditCardPayment payment) {
		this.payment = payment;
		
		if (payment.isNew()) {
			clearDisplay();
			return;
		}
		
		this.payment = payment = creditCardService.getCreditCardPayment(payment.getId());
		
		amountField.setText(FormatterUtil.formatAmount(payment.getAmount()));
		paymentDateModel.setValue(DateUtils.toCalendar(payment.getPaymentDate()));
		remarksField.setText(String.valueOf(payment.getRemarks()));
	}

	private void clearDisplay() {
		amountField.setText(null);
		paymentDateModel.setValue(null);
		remarksField.setText(null);
	}
	
}