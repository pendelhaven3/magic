package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.AccountsReceivableSummary;
import com.pj.magic.model.AccountsReceivableSummaryItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.AccountsReceivableService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AccountsReceivableSummaryPanel extends StandardMagicPanel {

	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	
	@Autowired private AccountsReceivableService accountsReceivableService;
	
	private JLabel accountsReceivableSummaryNumberField;
	private JLabel customerCodeField;
	private JLabel customerNameField;
	private JLabel totalAmountField;
	private AccountsReceivableSummary summary;
	private JTable table;
	private AccountsReceivableSummaryItemsTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		accountsReceivableSummaryNumberField = new JLabel();
		customerCodeField = new JLabel();
		customerNameField = new JLabel();
		totalAmountField = new JLabel();
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new AccountsReceivableSummaryItemsTableModel();
		table = new MagicListTable(tableModel);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(30, 1), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "AR Summary No.: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		accountsReceivableSummaryNumberField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(accountsReceivableSummaryNumberField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(150, 20));
		mainPanel.add(customerCodeField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNameField.setPreferredSize(new Dimension(300, 20));
		mainPanel.add(customerNameField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Total Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField.setPreferredSize(new Dimension(150, 20));
		mainPanel.add(totalAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(600, 150));
		mainPanel.add(tableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@SuppressWarnings("unchecked")
	public void updateDisplay(AccountsReceivableSummary summary) {
		this.summary = summary = accountsReceivableService.getAccountsReceivableSummary(summary.getId());
		
		accountsReceivableSummaryNumberField.setText(summary.getAccountsReceivableSummaryNumber().toString());
		customerCodeField.setText(summary.getCustomer().getCode());
		customerNameField.setText(summary.getCustomer().getName());
		totalAmountField.setText(FormatterUtil.formatAmount(summary.getTotalAmount()));
		
		// TODO: Resolve unchecked warning
		tableModel.setSalesInvoices((List<SalesInvoice>)CollectionUtils.collect(summary.getItems(), 
				new Transformer() {
			
			@Override
			public SalesInvoice transform(Object item) {
				return ((AccountsReceivableSummaryItem)item).getSalesInvoice();
			}
		}));
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private class AccountsReceivableSummaryItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"SI No.", "Amount"};
		
		private List<SalesInvoice> salesInvoices = new ArrayList<>();
		
		public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
			this.salesInvoices = salesInvoices;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return salesInvoices.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case AMOUNT_COLUMN_INDEX:
				return salesInvoice.getTotalNetAmount();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}
