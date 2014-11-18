package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.SalesInvoiceItemsTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesInvoicePanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(SalesInvoicePanel.class);
	
	@Autowired private SalesInvoiceItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private PrintService printService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	
	private SalesInvoice salesInvoice;
	private JLabel salesInvoiceNumberField;
	private JLabel customerNameField;
	private JLabel transactionDateField;
	private JLabel encoderField;
	private JLabel pricingSchemeNameField;
	private JLabel modeField;
	private JLabel remarksField;
	private JLabel paymentTermNameField;
	private JLabel statusField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JLabel totalDiscountedAmountField;
	private JLabel totalNetAmountField;
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
	private JButton markButton;
	private JButton cancelButton;
	private JButton showDiscountsButton;
	private boolean showDiscounts;
	
	@Override
	protected void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(itemsTable);
		updateTotalsPanelWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
	}

	public void updateDisplay(SalesInvoice salesInvoice) {
		showDiscounts = false;
		updateDisplay(salesInvoice, showDiscounts);
	}
	
	public void updateDisplay(SalesInvoice salesInvoice, boolean showDiscountDetails) {
		this.salesInvoice = salesInvoice = salesInvoiceService.get(salesInvoice.getId());
		
		salesInvoiceNumberField.setText(salesInvoice.getSalesInvoiceNumber().toString());
		customerNameField.setText(salesInvoice.getCustomer().getCode() + " - " + salesInvoice.getCustomer().getName());
		transactionDateField.setText(FormatterUtil.formatDate(salesInvoice.getTransactionDate()));
		encoderField.setText(salesInvoice.getEncoder().getUsername());
		pricingSchemeNameField.setText(salesInvoice.getPricingScheme().getName());
		modeField.setText(salesInvoice.getMode());
		remarksField.setText(salesInvoice.getRemarks());
		paymentTermNameField.setText(salesInvoice.getPaymentTerm().getName());
		statusField.setText(salesInvoice.getStatus());
		totalItemsField.setText(String.valueOf(salesInvoice.getTotalNumberOfItems()));
		totalAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalAmount()));
		totalDiscountedAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalDiscountedAmount()));
		totalNetAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount()));
		
		itemsTable.setSalesInvoice(salesInvoice, showDiscountDetails);
		if (showDiscountDetails && salesInvoice.isNew()) {
			itemsTable.selectAndEditCellAt(0, SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX);
		} else {
			itemsTable.changeSelection(0, 0, false, false);
		}
		
		markButton.setEnabled(salesInvoice.isNew());
		cancelButton.setEnabled(salesInvoice.isNew());
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesInvoicesListPanel();
	}
	
	private void initializeUnitPricesAndQuantitiesTable() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() == SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
						e.getColumn() == TableModelEvent.ALL_COLUMNS) {
					updateUnitPricesAndQuantitiesTable();
				}
			}

		});
		
		itemsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateUnitPricesAndQuantitiesTable();
			}
		});
	}
	
	private void updateUnitPricesAndQuantitiesTable() {
		if (itemsTable.getSelectedRow() == -1) {
			unitPricesAndQuantitiesTableModel.setProduct(null);
			return;
		}
		
		Product product = itemsTable.getCurrentlySelectedRowItem().getProduct();
		if (product != null) {
			unitPricesAndQuantitiesTableModel.setProduct(product);
		} else {
			unitPricesAndQuantitiesTableModel.setProduct(null);
		}
	}
	
	private class UnitPricesAndQuantitiesTable extends JTable {
		
		public UnitPricesAndQuantitiesTable() {
			super(unitPricesAndQuantitiesTableModel);
			setTableHeader(null);
			setRowHeight(20);
			setShowGrid(false);
			setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
				
				@Override
				public java.awt.Component getTableCellRendererComponent(
						JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
							row, column);
					setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
					return this;
				}
				
			});
		}
		
	}
	
	private class UnitPricesAndQuantitiesTableModel extends AbstractTableModel {

		private Product product;
		
		public void setProduct(Product product) {
			this.product = product;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return 2;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex == 0) {
				if (columnIndex == 0) {
					return "CSE";
				} else if (columnIndex == 3) {
					return "DOZ";
				}
			} else if (rowIndex == 1) {
				if (columnIndex == 0) {
					return "CTN";
				} else if (columnIndex == 3) {
					return "PCS";
				}
			}
			
			if (product == null) {
				switch (columnIndex) {
				case 1:
				case 4:
					return "0";
				case 2:
				case 5:
					return FormatterUtil.formatAmount(BigDecimal.ZERO);
				}
			}
			
			product = productService.findProductByCode(product.getCode());
			
			if (rowIndex == 0) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity("CSE"));
				case 2:
					BigDecimal unitPrice = product.getUnitPrice("CSE");
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				case 4:
					return String.valueOf(product.getUnitQuantity("DOZ"));
				case 5:
					unitPrice = product.getUnitPrice("DOZ");
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				}
			} else if (rowIndex == 1) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity("CTN"));
				case 2:
					BigDecimal unitPrice = product.getUnitPrice("CTN");
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				case 4:
					return String.valueOf(product.getUnitQuantity("PCS"));
				case 5:
					unitPrice = product.getUnitPrice("PCS");
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				}
			}
			return "";
		}
		
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "SI No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesInvoiceNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(salesInvoiceNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Transaction Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		transactionDateField = ComponentUtil.createLabel(150, "");
		mainPanel.add(transactionDateField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Name:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNameField = ComponentUtil.createLabel(300, "");
		mainPanel.add(customerNameField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Encoder:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		encoderField = ComponentUtil.createLabel(150, "");
		mainPanel.add(encoderField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Term:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermNameField = ComponentUtil.createLabel(100);
		mainPanel.add(paymentTermNameField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(150, "");
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Pricing Scheme:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeNameField = ComponentUtil.createLabel(100);
		mainPanel.add(pricingSchemeNameField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mode:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		modeField = ComponentUtil.createLabel(100);
		mainPanel.add(modeField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField = ComponentUtil.createLabel(200);
		mainPanel.add(remarksField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane infoTableScrollPane = new JScrollPane(new UnitPricesAndQuantitiesTable());
		infoTableScrollPane.setPreferredSize(new Dimension(500, 45));
		mainPanel.add(infoTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		showDiscountsButton = new MagicToolBarButton("discount", "Show/Hide Discount Details");
		showDiscountsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showDiscounts = !showDiscounts;
				updateDisplay(salesInvoice, showDiscounts);
			}
		});
		toolBar.add(showDiscountsButton);
		
		markButton = new MagicToolBarButton("post", "Mark");
		markButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markSalesInvoice();
			}
		});
		toolBar.add(markButton);
		
		cancelButton = new MagicToolBarButton("cancel", "Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelSalesInvoice();
			}
		});
		toolBar.add(cancelButton);
		
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateReportAsString(salesInvoice));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(salesInvoice);
			}
		});
		toolBar.add(printButton);
		
		JButton printBirFormButton = 
				new MagicToolBarButton("print_bir_form", "Print BIR form");
		printBirFormButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.printBirForm(salesInvoice);
			}
		});
		toolBar.add(printBirFormButton);
		
		JButton copyButton = new MagicToolBarButton("copy", "Create New Sales Requisition Based On Sales Invoice");
		copyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewSalesRequisitionBasedOnSalesInvoice();
			}
		});
		toolBar.add(copyButton);
	}

	protected void cancelSalesInvoice() {
		if (confirm("Cancel this Sales Invoice?")) {
			try {
				salesInvoiceService.cancel(salesInvoice);
				showMessage("Sales Invoice cancelled");
				updateDisplay(salesInvoice, showDiscounts);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred");
			}
		}
	}

	protected void markSalesInvoice() {
		if (confirm("Mark this Sales Invoice?")) {
			try {
				salesInvoiceService.mark(salesInvoice);
				showMessage("Sales Invoice marked");
				updateDisplay(salesInvoice, showDiscounts);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred");
			}
		}
	}

	private void createNewSalesRequisitionBasedOnSalesInvoice() {
		SalesRequisition salesRequisition = salesInvoiceService.createSalesRequisitionFromSalesInvoice(salesInvoice);
		getMagicFrame().switchToSalesRequisitionPanel(salesRequisition);
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
		totalItemsField = ComponentUtil.createLabel(60, "");
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(totalAmountField, c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(10, 1), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Disc. Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalDiscountedAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(totalDiscountedAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(140, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountField = ComponentUtil.createRightLabel(100, "");
		panel.add(totalNetAmountField, c);
		
		return panel;
	}
	
	private void updateTotalsPanelWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				switch (e.getColumn()) {
				case SalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX:
				case SalesInvoiceItemsTable.DISCOUNT_2_COLUMN_INDEX:
				case SalesInvoiceItemsTable.DISCOUNT_3_COLUMN_INDEX:
				case SalesInvoiceItemsTable.FLAT_RATE_DISCOUNT_COLUMN_INDEX:
					totalDiscountedAmountField.setText(
							FormatterUtil.formatAmount(salesInvoice.getTotalDiscountedAmount()));
					totalNetAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount()));
					break;
				}
			}
		});
	}
	
}
