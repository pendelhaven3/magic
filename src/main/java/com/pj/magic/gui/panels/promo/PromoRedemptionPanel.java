package com.pj.magic.gui.panels.promo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NothingToRedeemException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.dialog.SelectSalesInvoicesForPromoRedemptionDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PromoPrize;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PromoRedemptionPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PromoRedemptionPanel.class);
	
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	private static final int ITEM_DESCRIPTION_COLUMN_INDEX = 0;
	private static final int UNIT_COLUMN_INDEX = 1;
	private static final int QUANTITY_COLUMN_INDEX = 2;
	
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private PromoRedemptionService promoRedemptionService;
	@Autowired private SelectSalesInvoicesForPromoRedemptionDialog selectSalesInvoicesForPromoRedemptionDialog;
	
	private PromoRedemption promoRedemption;
	private JLabel promoRedemptionNumberLabel;
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private EllipsisButton selectCustomerButton;
	private MagicListTable salesInvoicesTable;
	private MagicListTable prizesTable;
	private SalesInvoicesTableModel salesInvoicesTableModel;
	private PromoRedemptionPrizesTableModel prizeTableModel;
	private JLabel totalAmountLabel;
	private MagicToolBarButton addSalesInvoiceButton;
	private MagicToolBarButton removeSalesInvoiceButton;
	private MagicToolBarButton postButton;
	
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
		
		initializeTables();
		initializeModelListener();
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
	}

	private void initializeModelListener() {
		salesInvoicesTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (!promoRedemption.isPosted()) {
					totalAmountLabel.setText(FormatterUtil.formatAmount(promoRedemption.getTotalAmount()));
					prizeTableModel.fireTableRowsUpdated(0, 0);
				}
			}
			
		});
	}

	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (promoRedemption.getCustomer() != null && promoRedemption.getCustomer().equals(customer)) {
				// skip saving since there is no change
				return;
			}
			
			promoRedemption.setCustomer(customer);
			
			try {
				promoRedemptionService.save(promoRedemption);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error during saving");
				return;
			}
			
			updateDisplay(promoRedemption);
		}
	}

	private void initializeTables() {
		salesInvoicesTableModel = new SalesInvoicesTableModel();
		salesInvoicesTable = new MagicListTable(salesInvoicesTableModel);
		
		prizeTableModel = new PromoRedemptionPrizesTableModel();
		prizesTable = new MagicListTable(prizeTableModel);
		
		prizesTable.getColumnModel().getColumn(ITEM_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		prizesTable.getColumnModel().getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
		prizesTable.getColumnModel().getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
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
		mainPanel.add(ComponentUtil.createLabel(200, "P&G - Jollibee GC Promo"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mechanics:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(new JLabel("For every 1,500 worth of P&G products, get a P50 Jollibee gift certificate"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Promo Redemption No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		promoRedemptionNumberLabel = ComponentUtil.createLabel(100);
		mainPanel.add(promoRedemptionNumberLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(createTablesPanel(), c);
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
		customerNameLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	private JPanel createTablesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.3;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		
		JTabbedPane salesInvoicesTabbedPane = new JTabbedPane();
		salesInvoicesTabbedPane.addTab("Sales Invoices", createSalesInvoicesPanel());
		panel.add(salesInvoicesTabbedPane, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.7;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		
		JTabbedPane prizesTabbedPane = new JTabbedPane();
		prizesTabbedPane.addTab("Prizes", createPrizesPanel());
		panel.add(prizesTabbedPane, c);
		
		return panel;
	}

	private JPanel createPrizesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(38), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesScrollPane = new JScrollPane(prizesTable);
		salesInvoicesScrollPane.setPreferredSize(new Dimension(200, 150));
		panel.add(salesInvoicesScrollPane, c);
		
		return panel;
	}

	private JPanel createSalesInvoicesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createSalesInvoicesTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesScrollPane = new JScrollPane(salesInvoicesTable);
		salesInvoicesScrollPane.setPreferredSize(new Dimension(200, 150));
		panel.add(salesInvoicesScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 2;
		totalAmountLabel = ComponentUtil.createRightLabel(100);
		panel.add(ComponentUtil.createGenericPanel(
				new JLabel("Total Amount: "), totalAmountLabel, Box.createHorizontalStrut(10)), c);
		
		return panel;
	}

	private JPanel createSalesInvoicesTableToolBar() {
		JPanel panel = new JPanel();
		
		addSalesInvoiceButton = new MagicToolBarButton("plus_small", "Add Sales Invoice (F10)", true);
		addSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSalesInvoicesForPromoRedemptionDialog();
			}
		});
		panel.add(addSalesInvoiceButton, BorderLayout.WEST);
		
		removeSalesInvoiceButton = new MagicToolBarButton("minus_small", "Remove Sales Invoice (Delete)", true);
		removeSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedSalesInvoice();
			}
		});
		panel.add(removeSalesInvoiceButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removeCurrentlySelectedSalesInvoice() {
		int selectedRow = salesInvoicesTable.getSelectedRow();
		if (selectedRow != -1) {
			if (confirm("Do you wish to delete the selected item?")) {
				PromoRedemptionSalesInvoice salesInvoice = 
						salesInvoicesTableModel.getPromoRedemptionSalesInvoice(selectedRow);
				salesInvoicesTable.clearSelection();
				promoRedemption.getSalesInvoices().remove(salesInvoice);
				salesInvoicesTableModel.removeItem(salesInvoice);
				
				if (!salesInvoicesTableModel.getSalesInvoices().isEmpty()) {
					if (selectedRow == salesInvoicesTableModel.getRowCount()) {
						salesInvoicesTable.changeSelection(selectedRow - 1, 0);
					} else {
						salesInvoicesTable.changeSelection(selectedRow, 0);
					}
				}
			}
		}
	}

	private void openSelectSalesInvoicesForPromoRedemptionDialog() {
		selectSalesInvoicesForPromoRedemptionDialog.searchSalesInvoicesForPromoRedemption(promoRedemption);
		selectSalesInvoicesForPromoRedemptionDialog.setVisible(true);
		
		List<SalesInvoice> selectedSalesInvoices = 
				selectSalesInvoicesForPromoRedemptionDialog.getSelectedSalesInvoices();
		if (!selectedSalesInvoices.isEmpty()) {
			for (SalesInvoice salesInvoice : selectedSalesInvoices) {
				PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
				redemptionSalesInvoice.setParent(promoRedemption);
				redemptionSalesInvoice.setSalesInvoice(salesInvoice);
				promoRedemptionService.save(redemptionSalesInvoice);
				promoRedemption.getSalesInvoices().add(redemptionSalesInvoice);
			}
			salesInvoicesTableModel.setPromoRedemption(promoRedemption);
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPromoRedemption();
			}
		});
		toolBar.add(postButton);
	}

	private void postPromoRedemption() {
		if (promoRedemption.getPrizeQuantity() == 0) {
			showErrorMessage("Not enough Sales Invoice amount to be able redeem anything");
			return;
		}
		
		if (confirm("Do you want to post this Promo Redemption?")) {
			try {
				promoRedemptionService.post(promoRedemption);
				JOptionPane.showMessageDialog(this, "Promo Redemption posted");
				updateDisplay(promoRedemption);
			} catch (NothingToRedeemException e) {
				showErrorMessage("Not enough Sales Invoice amount to be able redeem anything");
				updateDisplay(promoRedemption);
			} catch (AlreadyPostedException e) {
				showErrorMessage("Promo Redemption is already posted");
				updateDisplay(promoRedemption);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoRedemptionListPanel(promoRedemption.getPromo());
	}

	public void updateDisplay(PromoRedemption promoRedemption) {
		this.promoRedemption = promoRedemption;
		
		if (promoRedemption.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.promoRedemption = promoRedemption = 
				promoRedemptionService.getPromoRedemption(promoRedemption.getId());
		
		promoRedemptionNumberLabel.setText(promoRedemption.getPromoRedemptionNumber().toString());
		customerCodeField.setText(promoRedemption.getCustomer().getCode());
		customerNameLabel.setText(promoRedemption.getCustomer().getName());
		totalAmountLabel.setText(FormatterUtil.formatAmount(promoRedemption.getTotalAmount()));
		
		salesInvoicesTableModel.setPromoRedemption(promoRedemption);
		
		boolean isNew = !promoRedemption.isPosted();
		addSalesInvoiceButton.setEnabled(isNew);
		removeSalesInvoiceButton.setEnabled(isNew);
		postButton.setEnabled(isNew);
	}
	
	private void clearDisplay() {
		promoRedemptionNumberLabel.setText(null);
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		totalAmountLabel.setText(null);
		
		salesInvoicesTableModel.clear();
		
		addSalesInvoiceButton.setEnabled(false);
		removeSalesInvoiceButton.setEnabled(false);
		postButton.setEnabled(false);
	}

	private class SalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"SI No.", "Amount"};
		
		private List<PromoRedemptionSalesInvoice> salesInvoices = new ArrayList<>();
		
		public void setPromoRedemption(PromoRedemption promoRedemption) {
			this.salesInvoices = promoRedemption.getSalesInvoices();
			fireTableDataChanged();
		}

		public void clear() {
			salesInvoices.clear();
			fireTableDataChanged();
		}

		public List<PromoRedemptionSalesInvoice> getSalesInvoices() {
			return salesInvoices;
		}
		
		public PromoRedemptionSalesInvoice getPromoRedemptionSalesInvoice(int rowIndex) {
			return salesInvoices.get(rowIndex);
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
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex == AMOUNT_COLUMN_INDEX) ? Number.class : String.class;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesInvoice salesInvoice = salesInvoices.get(rowIndex).getSalesInvoice();
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoiceNumber();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(salesInvoice.getSalesByManufacturer(
						promoRedemption.getPromo().getManufacturer()));
			default:
				return null;
			}
		}
		
		public void removeItem(PromoRedemptionSalesInvoice salesInvoice) {
			salesInvoices.remove(salesInvoice);
			promoRedemptionService.delete(salesInvoice);
			fireTableDataChanged();
		}
		
	}
	
	private class PromoRedemptionPrizesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Item Description", "Unit", "Quantity"};
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public int getRowCount() {
			return 1;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex == QUANTITY_COLUMN_INDEX) ? Number.class : String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PromoPrize prize = promoRedemption.getPromo().getPrize();
			switch (columnIndex) {
			case ITEM_DESCRIPTION_COLUMN_INDEX:
				return prize.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return prize.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return promoRedemption.getPrizeQuantity().toString();
			default:
				return null;
			}
		}
		
	}
	
}