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

	@Autowired private SalesComplianceProjectSalesInvoiceItemsTable itemsTable;
	
	private SalesComplianceProjectSalesInvoice projectSalesInvoice;
	
	private JLabel salesComplianceProjectNumberLabel;
	private JLabel salesComplianceProjectNameLabel;
	private JLabel salesInvoiceNumberLabel;
	private JLabel transactionDateLabel;
	private JLabel customerLabel;
	private JLabel totalNetAmountLabel;
	
	@Override
	protected void initializeComponents() {
		transactionDateLabel = new JLabel();
		customerLabel = new JLabel();
		
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
		transactionDateLabel.setText(FormatterUtil.formatDate(projectSalesInvoice.getSalesInvoice().getTransactionDate()));
		totalNetAmountLabel.setText(FormatterUtil.formatAmount(projectSalesInvoice.getTotalNetAmount()));
		
		itemsTable.setSalesInvoice(projectSalesInvoice);
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
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerLabel.setPreferredSize(new Dimension(200, 20));
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

		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

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
		panel.add(ComponentUtil.createLabel(150, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountLabel = ComponentUtil.createRightLabel(120, "");
		panel.add(totalNetAmountLabel, c);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printButton = new MagicToolBarButton("print_bir_form_charge", "Print BIR form (Charge)", e -> printSalesInvoice());
		toolBar.add(printButton);
	}

	private void printSalesInvoice() {
		printService.print(projectSalesInvoice);
	}
	
}
