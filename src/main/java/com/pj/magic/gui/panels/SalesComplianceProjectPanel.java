package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.service.SalesComplianceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SalesComplianceProjectPanel extends StandardMagicPanel {

	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 1;
	private static final int CUSTOMER_COLUMN_INDEX = 2;
	private static final int ORIGINAL_AMOUNT_COLUMN_INDEX = 3;
	private static final int AMOUNT_COLUMN_INDEX = 4;
	private static final int PRINT_INVOICE_NUMBER_COLUMN_INDEX = 5;
	
	@Autowired private SalesComplianceService salesComplianceService;
	
	private SalesComplianceProject salesComplianceProject;
	
	private JLabel salesComplianceProjectNumberLabel;
	private JLabel nameLabel;
	private JLabel startDateLabel;
	private JLabel endDateLabel;
	private JLabel targetAmountLabel;
	private JLabel totalSalesInvoicesLabel;
	private JLabel totalAmountLabel;
	private JLabel totalOriginalAmountLabel;
	private JLabel remainingAmountLabel;
	
	private JButton updateButton;
	private JButton recreateButton;
	private MagicToolBarButton deleteSalesInvoiceButton;
	
	private MagicListTable table;
	private SalesComplianceProjectSalesInvoicesTableModel tableModel = new SalesComplianceProjectSalesInvoicesTableModel();
	
	@Override
	protected void initializeComponents() {
		updateButton = new JButton("Update");
		updateButton.addActionListener(e -> updateSalesComplianceProject());
		
		recreateButton = new JButton("Recreate");
		recreateButton.addActionListener(e -> recreateSalesComplianceProject());
		
		table = new MagicListTable(tableModel);
		initializeTable();
	}

	private void initializeTable() {
		table.getColumnModel().getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(50);
		table.getColumnModel().getColumn(TRANSACTION_DATE_COLUMN_INDEX).setPreferredWidth(50);
		table.getColumnModel().getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(200);
		table.getColumnModel().getColumn(ORIGINAL_AMOUNT_COLUMN_INDEX).setPreferredWidth(50);
		table.getColumnModel().getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(50);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		table.getColumnModel().getColumn(PRINT_INVOICE_NUMBER_COLUMN_INDEX).setCellRenderer(centerRenderer);
	}

	private void updateSalesComplianceProject() {
		getMagicFrame().switchToUpdateSalesComplianceProject(salesComplianceProject);
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
		mainPanel.add(ComponentUtil.createLabel(120, "Project Number: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesComplianceProjectNumberLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(salesComplianceProjectNumberLabel, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(nameLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Start Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		startDateLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(startDateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "End Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		endDateLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(endDateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(targetAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 25;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		updateButton.setPreferredSize(new Dimension(100, 25));
		recreateButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(ComponentUtil.createGenericPanel(updateButton, Box.createHorizontalStrut(5), recreateButton), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		mainPanel.add(createSalesInvoicesTableToolBar(), c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
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
		panel.add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalAmountLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(30), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Sales Invoices:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalSalesInvoicesLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalSalesInvoicesLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Orig. Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalOriginalAmountLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(totalOriginalAmountLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Remaining Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remainingAmountLabel = ComponentUtil.createRightLabel(100, "");
		panel.add(remainingAmountLabel, c);
		
		return panel;
	}
	
	private JPanel createSalesInvoicesTableToolBar() {
		JPanel panel = new JPanel();
		
		deleteSalesInvoiceButton = new MagicToolBarButton("minus_small", "Delete Item (Delete)", true);
		deleteSalesInvoiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteSalesInvoiceButton, BorderLayout.WEST);
		
		return panel;
	}

	protected void removeCurrentlySelectedItem() {
		int selectedRowIndex = table.getSelectedRow();
		if (selectedRowIndex != -1) {
			if (confirm("Do you wish to delete the selected item?")) {
				try {
					tableModel.removeItem(selectedRowIndex);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					showMessageForUnexpectedError();
					return;
				}
				
				showMessage("Sales Invoice removed");
				updateDisplay(salesComplianceProject);
			}
		}
	}

	public void updateDisplay(SalesComplianceProject salesComplianceProject) {
		this.salesComplianceProject = salesComplianceProject = salesComplianceService.getProject(salesComplianceProject.getId());
		
		salesComplianceProjectNumberLabel.setText(salesComplianceProject.getSalesComplianceProjectNumber().toString());
		nameLabel.setText(salesComplianceProject.getName());
		startDateLabel.setText(FormatterUtil.formatDate(salesComplianceProject.getStartDate()));
		endDateLabel.setText(FormatterUtil.formatDate(salesComplianceProject.getEndDate()));
		targetAmountLabel.setText(FormatterUtil.formatAmount(salesComplianceProject.getTargetAmount()));
		totalSalesInvoicesLabel.setText(String.valueOf(salesComplianceProject.getSalesInvoices().size()));
		totalAmountLabel.setText(FormatterUtil.formatAmount(salesComplianceProject.getTotalAmount()));
		totalOriginalAmountLabel.setText(FormatterUtil.formatAmount(salesComplianceProject.getTotalOriginalAmount()));
		remainingAmountLabel.setText(FormatterUtil.formatAmount(salesComplianceProject.getRemainingAmount()));
		
		tableModel.setItems(salesComplianceProject.getSalesInvoices());
		table.selectFirstRowThenFocus();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesComplianceProjectsListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) { }

	@Override
	protected void registerKeyBindings() {
		table.onEnterKeyAndDoubleClick(() -> selectSalesInvoice());
	}
	
	private void selectSalesInvoice() {
		SalesComplianceProjectSalesInvoice salesInvoice = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToSalesComplianceProjectSalesInvoicePanel(salesInvoice);
	}

	private void recreateSalesComplianceProject() {
		if (!confirm("Quantity changes to sales invoices will be discarded. Proceed?")) {
			return;
		}
		
		try {
			salesComplianceService.recreate(salesComplianceProject);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			showMessageForUnexpectedError(e);
			return;
		}
		
		showMessage("Sales Compliance Project recreated");
		updateDisplay(salesComplianceProject);
	}
	
	private class SalesComplianceProjectSalesInvoicesTableModel extends ListBackedTableModel<SalesComplianceProjectSalesInvoice> {

		private final String[] columnNames = {"Sales Invoice No.", "Transaction Date", "Customer", "Orig. Amount", "Amount", "Print Invoice No."};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesComplianceProjectSalesInvoice salesInvoice = getItem(rowIndex);
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoice().getSalesInvoiceNumber().toString();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(salesInvoice.getSalesInvoice().getTransactionDate());
			case CUSTOMER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoice().getCustomer().getName();
			case ORIGINAL_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(salesInvoice.getTotalOriginalNetAmount());
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount());
			case PRINT_INVOICE_NUMBER_COLUMN_INDEX:
				return salesInvoice.getSalesInvoice().getPrintInvoiceNumber();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == ORIGINAL_AMOUNT_COLUMN_INDEX || columnIndex == AMOUNT_COLUMN_INDEX) {
				return BigDecimal.class;
			} else {
				return super.getColumnClass(columnIndex);
			}
		}

		public void removeItem(int rowIndex) {
			salesComplianceService.remove(getItem(rowIndex));
		}

	}

}
