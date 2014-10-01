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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.SalesInvoiceItemsTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesInvoicePanel extends StandardMagicPanel {

	@Autowired private SalesInvoiceItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private PrintService printService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private SalesInvoice salesInvoice;
	private JLabel salesInvoiceNumberField;
	private JLabel customerNameField;
	private JLabel postDateField;
	private JLabel encoderField;
	private JLabel pricingSchemeNameField;
	private JLabel modeField;
	private JLabel remarksField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
	
	@Override
	protected void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(itemsTable);
		initializeUnitPricesAndQuantitiesTable();
	}

	public void updateDisplay(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoiceService.get(salesInvoice.getId());
		salesInvoice = this.salesInvoice;
		
		salesInvoiceNumberField.setText(salesInvoice.getSalesInvoiceNumber().toString());
		customerNameField.setText(salesInvoice.getCustomer().getCode() + " - " + salesInvoice.getCustomer().getName());
		postDateField.setText(FormatterUtil.formatDate(salesInvoice.getPostDate()));
		encoderField.setText(salesInvoice.getPostedBy().getUsername());
		pricingSchemeNameField.setText(salesInvoice.getPricingScheme().getName());
		modeField.setText(salesInvoice.getMode());
		remarksField.setText(salesInvoice.getRemarks());
		totalItemsField.setText(String.valueOf(salesInvoice.getTotalNumberOfItems()));
		totalAmountField.setText(FormatterUtil.formatAmount(salesInvoice.getTotalAmount()));
		itemsTable.setSalesInvoice(salesInvoice);
		if (salesInvoice.hasItems()) {
			itemsTable.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void registerKeyBindings() {
		
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
		mainPanel.add(ComponentUtil.createLabel(150, "SI No.:"), c);
		
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
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateField = ComponentUtil.createLabel(150, "");
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Customer Name:"), c);
		
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
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
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
		mainPanel.add(ComponentUtil.createLabel(150, "Pricing Scheme:"), c);
		
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
		mainPanel.add(ComponentUtil.createLabel(150, "Remarks:"), c);
		
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
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(salesInvoice);
			}
		});
		toolBar.add(printButton);
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(120, "");
		panel.add(totalItemsField, c);
		
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
		totalAmountField = ComponentUtil.createLabel(120, "");
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
}
