package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
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
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.SalesReturnItemsTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;

@Component
public class SalesReturnPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(SalesReturnPanel.class);
	
	@Autowired private SalesReturnItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SalesReturnService salesReturnService;
	
	private SalesReturn salesReturn;
	private JLabel salesReturnNumberField;
	private MagicTextField salesInvoiceNumberField;
	private JLabel customerField;
	private JLabel statusField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	
	@Override
	protected void initializeComponents() {
		salesReturnNumberField = new JLabel();
		
		salesInvoiceNumberField = new MagicTextField();
		salesInvoiceNumberField.setNumbersOnly(true);
		
		customerField = new JLabel();
		statusField = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(salesInvoiceNumberField);
		
//		updateTotalAmountFieldWhenItemsTableChanges();
	}

	@Override
	protected void registerKeyBindings() {
		salesInvoiceNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSalesInvoiceNumber();
			}
		});
	}

	private void saveSalesInvoiceNumber() {
		if (StringUtils.isEmpty(salesInvoiceNumberField.getText())) {
			showErrorMessage("Sales Invoice No. must be specified");
			salesInvoiceNumberField.requestFocusInWindow();
			return;
		}
		
		Long salesInvoiceNumber = Long.valueOf(salesInvoiceNumberField.getText());
		if (salesReturn.getId() != null 
				&& salesReturn.getSalesInvoice().getSalesInvoiceNumber().equals(salesInvoiceNumber)) {
			// no changes
			itemsTable.highlight();
			return;
		}
		
		SalesInvoice salesInvoice = salesInvoiceService.findBySalesInvoiceNumber(salesInvoiceNumber);
		if (!validateSalesInvoice(salesInvoice)) {
			salesInvoiceNumberField.requestFocusInWindow();
			return;
		}
		
		boolean newSalesReturn = (salesReturn.getId() == null);
		salesReturn.setSalesInvoice(salesInvoice);
		try {
			salesReturnService.save(salesReturn);
			updateDisplay(salesReturn);
			itemsTable.highlight();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage(Constants.UNEXPECTED_ERROR_MESSAGE);
			return;
		}
		
		if (!newSalesReturn) {
			// TODO: Delete all existing sales return items
		}
	}

	// TODO: Add checking that no Sales Return has been made yet
	private boolean validateSalesInvoice(SalesInvoice salesInvoice) {
		boolean valid = false;
		if (salesInvoice == null) {
			showErrorMessage("No record matching Sales Invoice No. specified");
		} else if (!salesInvoice.isNew()) {
			showErrorMessage("Sales Invoice is already marked/cancelled");
		} else {
			valid = true;
		}
		return valid;
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesReturnListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
//		itemsTable.getModel().addTableModelListener(new TableModelListener() {
//			
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				totalItemsField.setText(String.valueOf(itemsTable.getTotalNumberOfItems()));
//				totalAmountField.setText(FormatterUtil.formatAmount(itemsTable.getTotalAmount()));
//			}
//		});
	}

	public void updateDisplay(SalesReturn salesReturn) {
		if (salesReturn.getId() == null) {
			this.salesReturn = salesReturn;
			clearDisplay();
			return;
		}
		
		this.salesReturn = salesReturn = salesReturnService.getSalesReturn(salesReturn.getId());
		
		salesReturnNumberField.setText(salesReturn.getSalesReturnNumber().toString());
		salesInvoiceNumberField.setText(salesReturn.getSalesInvoice().getSalesInvoiceNumber().toString());
		statusField.setText(salesReturn.getStatus());
		customerField.setText(salesReturn.getSalesInvoice().getCustomer().getName());
		
		itemsTable.setSalesReturn(salesReturn);
	}

	private void clearDisplay() {
		salesReturnNumberField.setText(null);
		salesInvoiceNumberField.setText(null);
		statusField.setText(null);
		customerField.setText(null);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "SR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesReturnNumberField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(salesReturnNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(100);
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Sales Invoice No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesInvoiceNumberField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(salesInvoiceNumberField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(customerField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createHorizontalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				postAdjustmentIn();
			}
		});
		toolBar.add(postButton);
	}

}
