package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SalesComplianceProjectSalesInvoicePrintInvoiceDialog;
import com.pj.magic.gui.tables.SalesComplianceProjectSalesInvoiceItemsTable;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesComplianceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesComplianceProjectSalesInvoicePanel extends StandardMagicPanel {

	@Autowired private SalesComplianceService salesComplianceService;
	@Autowired private PrintService printService;
	@Autowired private SalesComplianceProjectSalesInvoicePrintInvoiceDialog printInvoiceDialog;
	@Autowired private PrintPreviewDialog printPreviewDialog;

	@Autowired private SalesComplianceProjectSalesInvoiceItemsTable itemsTable;
	
	private SalesComplianceProjectSalesInvoice projectSalesInvoice;
	
	private JLabel salesComplianceProjectNumberLabel;
	private JLabel salesComplianceProjectNameLabel;
	private JLabel salesInvoiceNumberLabel;
	private JLabel transactionDateLabel;
	private JLabel customerLabel;
	private JLabel customerAddressLabel;
	private JLabel customerTinLabel;
	private JLabel totalItemsLabel;
	private JLabel totalNetAmountLabel;
	private JLabel totalVatableSalesLabel;
	private JLabel totalSalesVatInclusiveLabel;
	private JLabel lessVatLabel;
	private JLabel amountNetOfVatLabel;
	private JLabel totalAmountDueLabel;
	
	@Override
	protected void initializeComponents() {
		transactionDateLabel = new JLabel();
		customerLabel = new JLabel();
		customerAddressLabel = new JLabel();
		customerTinLabel = new JLabel();
		
		focusOnItemsTableWhenThisPanelIsDisplayed();
		updateTotalAmountFieldWhenItemsTableChanges();
	}

	private void focusOnItemsTableWhenThisPanelIsDisplayed() {
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void registerKeyBindings() { }

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToSalesComplianceProjectPanel(projectSalesInvoice.getSalesComplianceProject());
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalNetAmountLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getTotalNetAmount()));
			}
		});
	}

	public void updateDisplay(SalesComplianceProjectSalesInvoice projectSalesInvoice) {
		this.projectSalesInvoice = projectSalesInvoice = salesComplianceService.getSalesInvoice(projectSalesInvoice.getId());
		
		salesComplianceProjectNumberLabel.setText(projectSalesInvoice.getSalesComplianceProject().getSalesComplianceProjectNumber().toString());
		salesComplianceProjectNameLabel.setText(projectSalesInvoice.getSalesComplianceProject().getName());
		salesInvoiceNumberLabel.setText(projectSalesInvoice.getSalesInvoice().getSalesInvoiceNumber().toString());
		customerLabel.setText(projectSalesInvoice.getSalesInvoice().getCustomer().getName());
		customerAddressLabel.setText(projectSalesInvoice.getSalesInvoice().getCustomer().getBusinessAddress());
		customerTinLabel.setText(projectSalesInvoice.getSalesInvoice().getCustomer().getTin());
		transactionDateLabel.setText(FormatterUtil.formatDate(projectSalesInvoice.getSalesInvoice().getTransactionDate()));
		totalItemsLabel.setText(String.valueOf(projectSalesInvoice.getItems().size()));
		totalNetAmountLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getTotalNetAmount()));
		
		itemsTable.setSalesInvoice(projectSalesInvoice);
		
		totalVatableSalesLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getVatableSales()));
		totalSalesVatInclusiveLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getTotalNetAmount()));
		lessVatLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getVatAmount()));
		amountNetOfVatLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getVatableSales()));
		totalAmountDueLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getTotalNetAmount()));
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Project No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesComplianceProjectNumberLabel = ComponentUtil.createLabel(100, "");
		mainPanel.add(salesComplianceProjectNumberLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Project Name:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesComplianceProjectNameLabel = ComponentUtil.createLabel(100, "");
		mainPanel.add(salesComplianceProjectNameLabel, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 6;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalGlue(), c);
		
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
		salesInvoiceNumberLabel = ComponentUtil.createLabel(100, "");
		mainPanel.add(salesInvoiceNumberLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerLabel.setPreferredSize(new Dimension(300, 20));
		mainPanel.add(customerLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Transaction Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		transactionDateLabel = ComponentUtil.createLabel(150, "");
		mainPanel.add(transactionDateLabel, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "TIN:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerTinLabel.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(customerTinLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Address:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerAddressLabel.setPreferredSize(new Dimension(600, 20));
		mainPanel.add(customerAddressLabel, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.insets.top = 10;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(createItemsPanel(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
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
		totalItemsLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(totalItemsLabel, c);
		
		c = new GridBagConstraints();
		c.insets.left = 100;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(totalNetAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total VATable Sales:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalVatableSalesLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(totalVatableSalesLabel, c);
		
		c = new GridBagConstraints();
		c.insets.left = 100;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(200, "Total Sales (VAT inclusive):"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalSalesVatInclusiveLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(totalSalesVatInclusiveLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.left = 100;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Less: VAT"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		lessVatLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(lessVatLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.left = 100;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Amount Net of VAT"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		amountNetOfVatLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(amountNetOfVatLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.left = 100;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "TOTAL AMOUNT DUE"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountDueLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(totalAmountDueLabel, c);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateText(projectSalesInvoice));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print_bir_form_charge4", "Print BIR form (Charge) 4", e -> printSalesInvoice());
		toolBar.add(printButton);
	}

	private void printSalesInvoice() {
		printInvoiceDialog.updateDisplay(projectSalesInvoice);
		printInvoiceDialog.setVisible(true);
	}
	
	private JPanel createItemsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(itemsTableScrollPane, c);
		
		return panel;
	}
	
}
