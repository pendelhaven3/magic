package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.LabelUtil;

@Component
public class SalesRequisitionPanel extends MagicPanel {

	private static final String GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME = "goToSalesRequisitionsList";
	
	@Autowired private SalesRequisitionItemsTable itemsTable;
	@Autowired private ProductService productService;
	
	private SalesRequisition salesRequisition;
	private JLabel salesRequisitionNumberField;
	private JTextField customerNameField;
	private JLabel createDateField;
	private JLabel encoderField;
	private JLabel totalAmountField;
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
	
	@PostConstruct
	public void initialize() {
		layoutComponents();
		registerKeyBindings();
		focusOnComponentWhenThisPanelIsDisplayed(customerNameField);
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeCustomerNameFieldBehavior();
		initializeUnitPricesAndQuantitiesTable();
	}

	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountField.setText(LabelUtil.formatAmount(itemsTable.getTotalAmount()));
			}
		});
	}

	private void initializeCustomerNameFieldBehavior() {
		customerNameField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		customerNameField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				salesRequisition.setCustomerName(customerNameField.getText());
				if (!salesRequisition.hasItems()) {
					itemsTable.switchToAddMode();
				} else {
					itemsTable.changeSelection(0, 0, false, false);
				}
				customerNameField.setFocusable(false);
			}
		});
	}
	
	public void refreshDisplay(SalesRequisition salesRequisition) {
		this.salesRequisition = salesRequisition;
		salesRequisitionNumberField.setText(salesRequisition.getSalesRequisitionNumber().toString());
		customerNameField.setText(salesRequisition.getCustomerName());
		createDateField.setText(LabelUtil.formatDate(salesRequisition.getCreateDate()));
		encoderField.setText(salesRequisition.getEncoder());
		totalAmountField.setText(salesRequisition.getTotalAmount().toString());
		itemsTable.setSalesRequisition(salesRequisition);
	}

	private void registerKeyBindings() {
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME);
		getActionMap().put(GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (itemsTable.isEditing()) {
					itemsTable.getCellEditor().cancelCellEditing();
				}
				getMagicFrame().switchToSalesRequisitionsListPanel();
			}
		});
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// first row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createFiller(50, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(150, "SR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		salesRequisitionNumberField = LabelUtil.createLabel(200, "");
		add(salesRequisitionNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(100, "Create Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		createDateField = LabelUtil.createLabel(150, "");
		add(createDateField, c);
		
		// second row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(150, "Customer Name:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		customerNameField = new MagicTextField();
		customerNameField.setPreferredSize(new Dimension(180, 20));
		add(customerNameField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(100, "Encoder:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		encoderField = LabelUtil.createLabel(150, "");
		add(encoderField, c);
		
		// third row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createFiller(50, 10), c);
		
		// fourth row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		add(itemsTableScrollPane, c);

		// fifth row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane infoTableScrollPane = new JScrollPane(new UnitPricesAndQuantitiesTable());
		infoTableScrollPane.setPreferredSize(new Dimension(500, 45));
		add(infoTableScrollPane, c);
		
		// sixth row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(100, "Total Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = 5;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = LabelUtil.createLabel(150, "");
		add(totalAmountField, c);
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
			return;
		}
		
		Product product = itemsTable.getCurrentlySelectedRowItem().getProduct();
		if (product != null && product.isValid()) {
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
				return "";
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
					return LabelUtil.formatAmount(unitPrice);
				case 4:
					return String.valueOf(product.getUnitQuantity("DOZ"));
				case 5:
					unitPrice = product.getUnitPrice("DOZ");
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return LabelUtil.formatAmount(unitPrice);
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
					return LabelUtil.formatAmount(unitPrice);
				case 4:
					return String.valueOf(product.getUnitQuantity("PCS"));
				case 5:
					unitPrice = product.getUnitPrice("PCS");
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return LabelUtil.formatAmount(unitPrice);
				}
			}
			return "";
		}
		
	}
	
}
