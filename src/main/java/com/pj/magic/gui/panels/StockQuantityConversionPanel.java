package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.gui.tables.StockQuantityConversionItemsTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.StockQuantityConversionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class StockQuantityConversionPanel extends AbstractMagicPanel implements ActionListener {

	private static final String SAVE_REMARKS_ACTION_NAME = "saveRemarks";
	private static final String POST_ACTION_COMMAND = "post";
	
	@Autowired private StockQuantityConversionItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	
	private StockQuantityConversion stockQuantityConversion;
	private JLabel stockQuantityConversionNumberField;
	private JTextField remarksField;
	private JLabel totalItemsField;
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
	
	@Override
	protected void initializeComponents() {
		stockQuantityConversionNumberField = new JLabel();
		remarksField = new MagicTextField();
		
		focusOnComponentWhenThisPanelIsDisplayed(remarksField);
		updateTotalItemsFieldWhenItemsTableChanges();
		initializeRemarksFieldBehavior();
		initializeUnitPricesAndQuantitiesTable();
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToStockQuantityConversionListPanel();
	}
	
	private void updateTotalItemsFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(itemsTable.getTotalNumberOfItems()));
			}
		});
	}

	private void initializeRemarksFieldBehavior() {
		remarksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_REMARKS_ACTION_NAME);
		remarksField.getActionMap().put(SAVE_REMARKS_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRemarks();
			}
		});
	}
	
	protected void saveRemarks() {
		stockQuantityConversion.setRemarks(remarksField.getText());
		stockQuantityConversionService.save(stockQuantityConversion);
		itemsTable.highlight();
	}

	public void updateDisplay(StockQuantityConversion stockQuantityConversion) {
		this.stockQuantityConversion = stockQuantityConversionService.getStockQuantityConversion(stockQuantityConversion.getId());
		stockQuantityConversion = this.stockQuantityConversion;
		
		stockQuantityConversionNumberField.setText(stockQuantityConversion.getStockQuantityConversionNumber().toString());
		remarksField.setText(stockQuantityConversion.getRemarks());
		totalItemsField.setText(String.valueOf(stockQuantityConversion.getTotalNumberOfItems()));
		itemsTable.setStockQuantityConversion(stockQuantityConversion);
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);

		currentRow++; // first row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(50, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "SQC No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		stockQuantityConversionNumberField = ComponentUtil.createLabel(200, "");
		add(stockQuantityConversionNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(100, 1), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(150, 1), c);
		
		currentRow++; // second row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Remarks:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 20));
		add(remarksField, c);
		
		currentRow++; // third row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++; // fourth row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		add(itemsTableScrollPane, c);

		currentRow++; // fifth row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane infoTableScrollPane = new JScrollPane(new UnitPricesAndQuantitiesTable());
		infoTableScrollPane.setPreferredSize(new Dimension(500, 65));
		add(infoTableScrollPane, c);
		
		currentRow++; // sixth row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(150, "");
		add(totalItemsField, c);		
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
		if (product != null && product.isValid()) {
			unitPricesAndQuantitiesTableModel.setProduct(productService.getProduct(product.getId()));
		} else {
			unitPricesAndQuantitiesTableModel.setProduct(null);
		}
	}
	
	private JToolBar createToolBar() {
		JToolBar toolBar = new MagicToolBar();
		
		JButton postButton = new MagicToolBarButton("invoice", "Post");
		postButton.setActionCommand(POST_ACTION_COMMAND);
		postButton.addActionListener(this);
		
		toolBar.add(postButton);
		return toolBar;
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
			return 3;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (rowIndex) {
			case 0:
				if (columnIndex == 0) {
					return Unit.CASE;
				} else if (columnIndex == 3) {
					return Unit.TIE;
				}
				break;
			case 1:
				if (columnIndex == 0) {
					return Unit.CARTON;
				} else if (columnIndex == 3) {
					return Unit.DOZEN;
				}
				break;
			case 2:
				switch (columnIndex) {
				case 0:
					return Unit.PIECES;
				case 3:
				case 4:
				case 5:
					return "";
				}
				break;
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
					return String.valueOf(product.getUnitQuantity(Unit.CASE));
				case 2:
					BigDecimal unitPrice = product.getUnitPrice(Unit.CASE);
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				case 4:
					return String.valueOf(product.getUnitQuantity(Unit.TIE));
				case 5:
					unitPrice = product.getUnitPrice(Unit.TIE);
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				}
			} else if (rowIndex == 1) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity(Unit.CARTON));
				case 2:
					BigDecimal unitPrice = product.getUnitPrice(Unit.CARTON);
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				case 4:
					return String.valueOf(product.getUnitQuantity(Unit.DOZEN));
				case 5:
					unitPrice = product.getUnitPrice(Unit.DOZEN);
					if (unitPrice == null) {
						unitPrice = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitPrice);
				}
			} else if (rowIndex == 2) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity(Unit.PIECES));
				case 2:
					BigDecimal unitPrice = product.getUnitPrice(Unit.PIECES);
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
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
		case POST_ACTION_COMMAND:
			postStockQuantityConversion();
			break;
		}
	}

	private void postStockQuantityConversion() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (!stockQuantityConversion.hasItems()) {
			showErrorMessage("Cannot post a stock quantity conversion with no items");
			itemsTable.requestFocusInWindow();
			return;
		}
		
		int confirm = showConfirmMessage("Do you want to post this stock quantity conversion?");
		if (confirm == JOptionPane.OK_OPTION) {
			try {
				stockQuantityConversionService.post(stockQuantityConversion);
				showMessage("Post successful!");
				getMagicFrame().switchToStockQuantityConversionListPanel();
			} catch (NotEnoughStocksException e) {	
				showErrorMessage("Not enough available stocks!");
				updateDisplay(stockQuantityConversion);
				itemsTable.highlightQuantityColumn(e.getStockQuantityConversionItem());
			}
		}
	}

}
