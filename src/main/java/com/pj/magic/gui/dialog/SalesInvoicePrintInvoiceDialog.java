package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SalesInvoicePrintInvoiceDialog extends MagicDialog {

	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PrintService printService;
	
	private JLabel salesInvoiceNumberLabel;
    private JLabel numberOfPagesLabel;
    private MagicTextField printInvoiceNumberField;
	private JButton saveAndPrintButton;
	private JButton saveButton;
	private JButton printButton;
	
    private SalesInvoice salesInvoice;
    
	public SalesInvoicePrintInvoiceDialog() {
		setSize(500, 250);
		setLocationRelativeTo(null);
		setTitle("Print Invoice");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		salesInvoiceNumberLabel = new JLabel();
		numberOfPagesLabel = new JLabel();
		printInvoiceNumberField = new MagicTextField();
		
		saveAndPrintButton = new JButton("Save and Print");
		saveAndPrintButton.addActionListener(e -> saveAndPrint());
		saveButton = new JButton("Save Only");
		saveButton.addActionListener(e -> save());
		printButton = new JButton("Print Only");
		printButton.addActionListener(e -> print());
	}

//    private void saveUnitCostsAndPrices() {
//		if (table.isEditing()) {
//			if (!table.getCellEditor().stopCellEditing()) {
//				table.getEditorComponent().requestFocusInWindow();
//				return;
//			}
//		}
//		
//		if (!StringUtils.isEmpty(newCompanyListPriceField.getText())) {
//            BigDecimal newCompanyListPrice = newCompanyListPriceField.getTextAsBigDecimal();
//            companyListPriceLabel.setText(FormatterUtil.formatAmount(newCompanyListPrice));
//		}
//		
//		JOptionPane.showMessageDialog(this, "Saved!");
//	}

	private void registerKeyBindings() {
		// none
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Sales Invoice:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesInvoiceNumberLabel.setPreferredSize(new Dimension(150, 25));
		add(salesInvoiceNumberLabel, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "No. of Pages:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		numberOfPagesLabel.setPreferredSize(new Dimension(150, 25));
		add(numberOfPagesLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Print Invoice No.:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		printInvoiceNumberField.setPreferredSize(new Dimension(150, 25));
		add(printInvoiceNumberField, c);
		
        currentRow++;
        
        c = new GridBagConstraints();
        c.insets.top = 20;
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 3;
        saveAndPrintButton.setPreferredSize(new Dimension(140, 25));
        saveButton.setPreferredSize(new Dimension(120, 25));
        printButton.setPreferredSize(new Dimension(120, 25));
        add(ComponentUtil.createGenericPanel(
        		saveAndPrintButton, Box.createHorizontalStrut(10), saveButton, Box.createHorizontalStrut(10), printButton), c);
        
        currentRow++;
        
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	public void updateDisplay(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice = salesInvoiceService.get(salesInvoice.getId());
		
		salesInvoiceNumberLabel.setText(String.valueOf(salesInvoice.getSalesInvoiceNumber()));
		
		List<String> printPages = printService.generateBirChargeForm4Text(salesInvoice);
		numberOfPagesLabel.setText(String.valueOf(printPages.size()));
		
		printInvoiceNumberField.setText(salesInvoice.getPrintInvoiceNumber());
	}

	private void saveAndPrint() {
		try {
			salesInvoiceService.savePrintInvoiceNumber(salesInvoice, printInvoiceNumberField.getText());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			showUnexpectedErrorMessage();
			return;
		}
		
		print();
	}
	
	private void save() {
		try {
			salesInvoiceService.savePrintInvoiceNumber(salesInvoice, printInvoiceNumberField.getText());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			showUnexpectedErrorMessage();
			return;
		}
		
		showMessage("Saved!");
	}
	
	private void print() {
		printService.printBirChargeForm4(salesInvoice);
	}
	
}
