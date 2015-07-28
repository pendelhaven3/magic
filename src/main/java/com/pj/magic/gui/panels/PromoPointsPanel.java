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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.AvailedPromoPointsItem;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

/**
 * Panel class for Availed Promo Points screen
 * 
 * @author PJ Miranda
 *
 */
@Component
public class PromoPointsPanel extends StandardMagicPanel {

	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 1;
	private static final int QUALIFYING_AMOUNT_COLUMN_INDEX = 2;
	private static final int POINTS_COLUMN_INDEX = 3;
	
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private Promo promo;
	private JLabel promoNameLabel;
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private EllipsisButton selectCustomerButton;
	private MagicListTable table;
	private AvailedPromoPointsTableModel tableModel;
	private JLabel totalPointsLabel;
	
	@Override
	protected void initializeComponents() {
		promoNameLabel = new JLabel();
		
		customerCodeField = new MagicTextField();
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});;
		
		initializeTable();
		
		totalPointsLabel = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
	}
	
	private void initializeTable() {
		tableModel = new AvailedPromoPointsTableModel();
		table = new MagicListTable(tableModel);
	}
	
	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
			
			List<AvailedPromoPointsItem> items = promo.evaluateForPoints(searchSalesInvoicesQualifiedForPromo(customer));
			tableModel.setItems(items);
			if (items.isEmpty()) {
				showMessage("No qualified sales invoices");
			}
			totalPointsLabel.setText(String.valueOf(computeTotalPoints(items)));
		}
	}

	private int computeTotalPoints(List<AvailedPromoPointsItem> items) {
		int total = 0;
		for (AvailedPromoPointsItem item : items) {
			total += item.getPoints();
		}
		return total;
	}

	private List<SalesInvoice> searchSalesInvoicesQualifiedForPromo(Customer customer) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setCustomer(customer);
		criteria.setTransactionDateFrom(promo.getStartDate());
		criteria.setTransactionDateTo(promo.getEndDate());
		criteria.setPricingScheme(promo.getPricingScheme());
		
		return salesInvoiceService.search(criteria);
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
		mainPanel.add(ComponentUtil.createLabel(100, "Promo:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		promoNameLabel = ComponentUtil.createLabel(300);
		mainPanel.add(promoNameLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridy = currentRow;
		c.weighty = 1.0;
		c.gridwidth = 3;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridy = currentRow;
		c.gridwidth = 3;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
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
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
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
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
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

	public void updateDisplay(Promo promo) {
		this.promo = promo;
		promoNameLabel.setText(promo.getName());
		
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		tableModel.clear();
		totalPointsLabel.setText(null);
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoRedemptionPromoListPanel();
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Points:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(totalPointsLabel, c);
		
		return panel;
	}
	
	private class AvailedPromoPointsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"SI No.", "Transaction Date", "Qualifying Amount", "Points"};
		
		private List<AvailedPromoPointsItem> items = new ArrayList<>();
		
		public void setItems(List<AvailedPromoPointsItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		public void clear() {
			items.clear();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return items.size();
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
			AvailedPromoPointsItem item = items.get(rowIndex);
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return item.getSalesInvoiceNumber();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(item.getTransactionDate());
			case QUALIFYING_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getNetAmount());
			case POINTS_COLUMN_INDEX:
				return item.getPoints();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case QUALIFYING_AMOUNT_COLUMN_INDEX:
			case POINTS_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
	}
	
}