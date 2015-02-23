package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.report.UnpaidSalesInvoicesReport;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class UnpaidSalesInvoicesListPanel extends StandardMagicPanel {

	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 1;
	private static final int CUSTOMER_COLUMN_INDEX = 2;
	private static final int NET_AMOUNT_COLUMN_INDEX = 3;
	
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private CustomerService customerService;
	
	private MagicListTable table;
	private SalesInvoicesTableModel tableModel;
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private EllipsisButton selectCustomerButton;
	private JButton searchButton;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchSalesInvoices();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void searchSalesInvoices() {
		UnpaidSalesInvoicesReport report = createUnpaidSalesInvoicesReport();
		tableModel.setSalesInvoices(report.getSalesInvoices());
		if (!report.getSalesInvoices().isEmpty()) {
			table.changeSelection(0, 0);
		}
	}

	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		} else {
			customerNameLabel.setText(null);
		}
	}

	private void initializeTable() {
		tableModel = new SalesInvoicesTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(TRANSACTION_DATE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	public void updateDisplay() {
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		
		searchSalesInvoices();
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 25));
		panel.add(selectCustomerButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameLabel.setPreferredSize(new Dimension(300, 25));
		panel.add(customerNameLabel, c);
		
		return panel;
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		toolBar.add(printButton);
	}

	private void print() {
		UnpaidSalesInvoicesReport report = createUnpaidSalesInvoicesReport();
		printService.print(report);
	}

	private void printPreview() {
		UnpaidSalesInvoicesReport report = createUnpaidSalesInvoicesReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setVisible(true);
	}

	private UnpaidSalesInvoicesReport createUnpaidSalesInvoicesReport() {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setPaid(false);
		criteria.setOrderBy("a.TRANSACTION_DT, a.SALES_INVOICE_NO");
		
		String customerCode = customerCodeField.getText();
		if (!StringUtils.isEmpty(customerCode)) {
			Customer customer = customerService.findCustomerByCode(customerCode);
			if (customer != null) {
				criteria.setCustomer(customer);
				customerCodeField.setText(customer.getCode());
				customerNameLabel.setText(customer.getName());
			} else {
				customerNameLabel.setText(null);
			}
		}
		
		UnpaidSalesInvoicesReport report = new UnpaidSalesInvoicesReport();
		report.setSalesInvoices(salesInvoiceService.search(criteria));
		return report;
	}

	private class SalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"SI No.", "Transaction Date", "Customer", "Net Amount"};
		
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
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(salesInvoice.getTransactionDate());
			case CUSTOMER_COLUMN_INDEX:
				return salesInvoice.getCustomer().getName();
			case NET_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount());
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == NET_AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}