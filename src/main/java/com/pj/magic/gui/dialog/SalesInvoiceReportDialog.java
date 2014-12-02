package com.pj.magic.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.util.ComponentUtil;

@Component
public class SalesInvoiceReportDialog extends MagicDialog {

	private UtilCalendarModel transactionDateModel;
	private JButton generateButton;
	private JButton generateWithCostAndProfitButton;
	private boolean includeCostAndProfit;
	
	public SalesInvoiceReportDialog() {
		setSize(420, 190);
		setLocationRelativeTo(null);
		setTitle("Generate Sales Invoice Report");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		transactionDateModel = new UtilCalendarModel();
		
		generateButton = new JButton("Generate Sales Invoice Report");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				includeCostAndProfit = false;
				setVisible(false);
			}
		});
		
		generateWithCostAndProfitButton = new JButton("Generate Cost and Profit Report");
		generateWithCostAndProfitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				includeCostAndProfit = true;
				setVisible(false);
			}
		});
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Transaction Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(transactionDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		add(datePicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets = new Insets(20, 0, 5, 0);
		c.gridwidth = 2;
		add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets = new Insets(5, 0, 5, 0);
		c.gridwidth = 2;
		add(generateWithCostAndProfitButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(), c);
	}
	
	public Date getReportDate() {
		if (transactionDateModel.getValue() != null) {
			return transactionDateModel.getValue().getTime();
		} else {
			return null;
		}
	}
	
	public void updateDisplay() {
		transactionDateModel.setValue(null);
		includeCostAndProfit = false;
	}
	
	public boolean isIncludeCostAndProfit() {
		return includeCostAndProfit;
	}
	
}