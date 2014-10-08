package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.AreaInventoryReportItemsTable;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.service.AreaInventoryReportService;
import com.pj.magic.service.InventoryCheckService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class AreaInventoryReportPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(AreaInventoryReportPanel.class);
	
	private static final String FOCUS_NEXT_FIELD_ACTION_NAME = "focusNextField";
	
	@Autowired private AreaInventoryReportItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private AreaInventoryReportService areaInventoryReportService;
	@Autowired private InventoryCheckService inventoryCheckService;
	
	private AreaInventoryReport areaInventoryReport;
	private JLabel inventoryDateField;
	private MagicTextField reportNumberField; // TODO: change to combobox later
	private MagicTextField areaField;
	private MagicTextField checkerField;
	private MagicTextField doubleCheckerField;
	private JLabel totalItemsField;
//	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
	private JButton addItemButton;
	private JButton deleteItemButton;
	
	@Override
	protected void initializeComponents() {
		reportNumberField = new MagicTextField();
		reportNumberField.setMaximumLength(2);
		reportNumberField.setNumbersOnly(true);
		reportNumberField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveReportNumberField();
			}
		});
		
		areaField = new MagicTextField();
		areaField.setMaximumLength(50);
		areaField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveAreaField();
			}
		});
		
		checkerField = new MagicTextField();
		checkerField.setMaximumLength(50);
		checkerField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveCheckerField();
			}
		});
		
		doubleCheckerField = new MagicTextField();
		doubleCheckerField.setMaximumLength(50);
		doubleCheckerField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveDoubleChecker();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(reportNumberField);
//		updateTotalAmountFieldWhenItemsTableChanges();
//		initializeUnitPricesAndQuantitiesTable();
	}

	protected void saveReportNumberField() {
		if (StringUtils.isEmpty(reportNumberField.getText())) {
			showErrorMessage("Report No. must be specified");
			reportNumberField.requestFocusInWindow();
			return;
		}
		
		int reportNumber = Integer.parseInt(reportNumberField.getText());
		if (areaInventoryReport.getId() == null || reportNumber != areaInventoryReport.getReportNumber()) {
			areaInventoryReport.setReportNumber(reportNumber);
			areaInventoryReportService.save(areaInventoryReport);
			updateDisplay(areaInventoryReport);
		}
	}

	protected void saveDoubleChecker() {
		if (!doubleCheckerField.getText().equals(areaInventoryReport.getDoubleChecker())) {
			areaInventoryReport.setDoubleChecker(doubleCheckerField.getText());
			areaInventoryReportService.save(areaInventoryReport);
		}
	}

	protected void saveCheckerField() {
		if (!checkerField.getText().equals(areaInventoryReport.getChecker())) {
			areaInventoryReport.setChecker(checkerField.getText());
			areaInventoryReportService.save(areaInventoryReport);
		}
	}

	protected void saveAreaField() {
		if (!areaField.getText().equals(areaInventoryReport.getArea())) {
			areaInventoryReport.setArea(areaField.getText());
			areaInventoryReportService.save(areaInventoryReport);
		}
	}

	@Override
	protected void registerKeyBindings() {
		setFocusOnNextFieldOnEnterKey(reportNumberField);
		setFocusOnNextFieldOnEnterKey(areaField);
		setFocusOnNextFieldOnEnterKey(checkerField);
		
		doubleCheckerField.getInputMap().put(KeyUtil.getEnterKey(), FOCUS_NEXT_FIELD_ACTION_NAME);
		doubleCheckerField.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToAreaInventoryReportListPanel();
	}
	
	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(reportNumberField);
		focusOrder.add(areaField);
		focusOrder.add(checkerField);
		focusOrder.add(doubleCheckerField);
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
//		itemsTable.getModel().addTableModelListener(new TableModelListener() {
//			
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				totalItemsField.setText(String.valueOf(itemsTable.getTotalNumberOfItems()));
//				totalAmountField.setText(FormatterUtil.formatAmount(itemsTable.getTotalAmount()));
//			}
//		});
	}

	public void updateDisplay(AreaInventoryReport areaInventoryReport) {
		inventoryDateField.setText(FormatterUtil.formatDate(areaInventoryReport.getParent().getInventoryDate()));
		
		if (areaInventoryReport.getId() == null) {
			this.areaInventoryReport = areaInventoryReport;
			clearDisplay();
			return;
		}
		
		this.areaInventoryReport = areaInventoryReportService.getAreaInventoryReport(areaInventoryReport.getId());
		areaInventoryReport = this.areaInventoryReport;
		
		reportNumberField.setText(areaInventoryReport.getReportNumber().toString());
		areaField.setEnabled(true);
		areaField.setText(areaInventoryReport.getArea());
		checkerField.setEnabled(true);
		checkerField.setText(areaInventoryReport.getChecker());
		doubleCheckerField.setEnabled(true);
		doubleCheckerField.setText(areaInventoryReport.getDoubleChecker());
		
		itemsTable.setAreaInventoryReport(areaInventoryReport);
		
		addItemButton.setEnabled(true);
		deleteItemButton.setEnabled(true);
	}

	private void clearDisplay() {
		reportNumberField.setText(null);
		areaField.setEnabled(false);
		areaField.setText(null);
		checkerField.setEnabled(false);
		checkerField.setText(null);
		doubleCheckerField.setEnabled(false);
		doubleCheckerField.setText(null);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Inventory Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		inventoryDateField = ComponentUtil.createLabel(100);
		mainPanel.add(inventoryDateField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 1), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Report No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		reportNumberField.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(reportNumberField, c);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Area:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		areaField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(areaField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Checker:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		checkerField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(checkerField, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Double Checker:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		doubleCheckerField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(doubleCheckerField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

//		currentRow++;
//		
//		c.fill = GridBagConstraints.BOTH;
//		c.weightx = c.weighty = 0.0;
//		c.gridx = 0;
//		c.gridy = currentRow;
//		c.gridwidth = 6;
//		c.anchor = GridBagConstraints.CENTER;
//		JScrollPane infoTableScrollPane = new JScrollPane(new UnitPricesAndQuantitiesTable());
//		infoTableScrollPane.setPreferredSize(new Dimension(500, 65));
//		mainPanel.add(infoTableScrollPane, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(150, "");
		mainPanel.add(totalItemsField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Total Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
//		totalAmountField = ComponentUtil.createLabel(150, "");
//		mainPanel.add(totalAmountField, c);
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.delete();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void initializeUnitPricesAndQuantitiesTable() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() == AreaInventoryReportItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
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
//		if (itemsTable.getSelectedRow() == -1) {
//			unitPricesAndQuantitiesTableModel.setProduct(null);
//			return;
//		}
//		
//		Product product = itemsTable.getCurrentlySelectedRowItem().getProduct();
//		if (product != null) {
//			unitPricesAndQuantitiesTableModel.setProduct(productService.getProduct(product.getId()));
//		} else {
//			unitPricesAndQuantitiesTableModel.setProduct(null);
//		}
	}
	
//	private class UnitPricesAndQuantitiesTable extends JTable {
//		
//		public UnitPricesAndQuantitiesTable() {
//			super(unitPricesAndQuantitiesTableModel);
//			setTableHeader(null);
//			setRowHeight(20);
//			setShowGrid(false);
//			setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//				
//				@Override
//				public java.awt.Component getTableCellRendererComponent(
//						JTable table, Object value, boolean isSelected,
//						boolean hasFocus, int row, int column) {
//					super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
//							row, column);
//					setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
//					return this;
//				}
//				
//			});
//		}
//		
//	}
//	
//	private class UnitPricesAndQuantitiesTableModel extends AbstractTableModel {
//
//		private Product product;
//		
//		public void setProduct(Product product) {
//			this.product = product;
//			fireTableDataChanged();
//		}
//		
//		@Override
//		public int getRowCount() {
//			return 3;
//		}
//
//		@Override
//		public int getColumnCount() {
//			return 6;
//		}
//
//		@Override
//		public Object getValueAt(int rowIndex, int columnIndex) {
//			switch (rowIndex) {
//			case 0:
//				if (columnIndex == 0) {
//					return Unit.CASE;
//				} else if (columnIndex == 3) {
//					return Unit.TIE;
//				}
//				break;
//			case 1:
//				if (columnIndex == 0) {
//					return Unit.CARTON;
//				} else if (columnIndex == 3) {
//					return Unit.DOZEN;
//				}
//				break;
//			case 2:
//				switch (columnIndex) {
//				case 0:
//					return Unit.PIECES;
//				case 3:
//				case 4:
//				case 5:
//					return "";
//				}
//				break;
//			}
//			
//			if (product == null) {
//				switch (columnIndex) {
//				case 1:
//				case 4:
//					return "0";
//				case 2:
//				case 5:
//					return FormatterUtil.formatAmount(BigDecimal.ZERO);
//				}
//			}
//			
//			product = productService.findProductByCode(product.getCode());
//			
//			if (rowIndex == 0) {
//				switch (columnIndex) {
//				case 1:
//					return String.valueOf(product.getUnitQuantity(Unit.CASE));
//				case 2:
//					BigDecimal unitPrice = product.getUnitPrice(Unit.CASE);
//					if (unitPrice == null) {
//						unitPrice = BigDecimal.ZERO;
//					}
//					return FormatterUtil.formatAmount(unitPrice);
//				case 4:
//					return String.valueOf(product.getUnitQuantity(Unit.TIE));
//				case 5:
//					unitPrice = product.getUnitPrice(Unit.TIE);
//					if (unitPrice == null) {
//						unitPrice = BigDecimal.ZERO;
//					}
//					return FormatterUtil.formatAmount(unitPrice);
//				}
//			} else if (rowIndex == 1) {
//				switch (columnIndex) {
//				case 1:
//					return String.valueOf(product.getUnitQuantity(Unit.CARTON));
//				case 2:
//					BigDecimal unitPrice = product.getUnitPrice(Unit.CARTON);
//					if (unitPrice == null) {
//						unitPrice = BigDecimal.ZERO;
//					}
//					return FormatterUtil.formatAmount(unitPrice);
//				case 4:
//					return String.valueOf(product.getUnitQuantity(Unit.DOZEN));
//				case 5:
//					unitPrice = product.getUnitPrice(Unit.DOZEN);
//					if (unitPrice == null) {
//						unitPrice = BigDecimal.ZERO;
//					}
//					return FormatterUtil.formatAmount(unitPrice);
//				}
//			} else if (rowIndex == 2) {
//				switch (columnIndex) {
//				case 1:
//					return String.valueOf(product.getUnitQuantity(Unit.PIECES));
//				case 2:
//					BigDecimal unitPrice = product.getUnitPrice(Unit.PIECES);
//					if (unitPrice == null) {
//						unitPrice = BigDecimal.ZERO;
//					}
//					return FormatterUtil.formatAmount(unitPrice);
//				}
//			}
//			return "";
//		}
//		
//	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
