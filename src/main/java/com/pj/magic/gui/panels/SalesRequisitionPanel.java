package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
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
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesRequisitionPanel extends MagicPanel implements ActionListener {

	private static final String GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME = "goToSalesRequisitionsList";
	private static final String PRINT_PREVIEW_ACTION_COMMAND = "printPreview";
	private static final String POST_ACTION_COMMAND = "post";
	
	@Autowired private SalesRequisitionItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionService salesRequisitionService;
	
	private SalesRequisition salesRequisition;
	private JLabel salesRequisitionNumberField;
	private JTextField customerNameField;
	private JLabel createDateField;
	private JLabel encoderField;
	private JLabel totalItemsField;
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
				totalItemsField.setText(String.valueOf(itemsTable.getTotalNumberOfItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(itemsTable.getTotalAmount()));
			}
		});
	}

	private void initializeCustomerNameFieldBehavior() {
		customerNameField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		customerNameField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Customer customer = new Customer(); // TODO: replace with selection dialog
				customer.setName(customerNameField.getText());
				customer.setAddress("DUMMY ADDRESS");
				
				salesRequisition.setCustomer(customer);
				salesRequisitionService.save(salesRequisition);
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
		customerNameField.setText(salesRequisition.getCustomer().getName());
		createDateField.setText(FormatterUtil.formatDate(salesRequisition.getCreateDate()));
		encoderField.setText(salesRequisition.getEncoder());
		totalItemsField.setText(String.valueOf(salesRequisition.getTotalNumberOfItems()));
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
		add(ComponentUtil.createLabel(150, "SR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesRequisitionNumberField = ComponentUtil.createLabel(200, "");
		add(salesRequisitionNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Create Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		createDateField = ComponentUtil.createLabel(150, "");
		add(createDateField, c);
		
		currentRow++; // second row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Customer Name:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		customerNameField = new MagicTextField();
		customerNameField.setPreferredSize(new Dimension(180, 20));
		add(customerNameField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Encoder:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		encoderField = ComponentUtil.createLabel(150, "");
		add(encoderField, c);
		
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
		infoTableScrollPane.setPreferredSize(new Dimension(500, 45));
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
		
		currentRow++; // seventh row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Total Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(150, "");
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
			unitPricesAndQuantitiesTableModel.setProduct(null);
			return;
		}
		
		Product product = itemsTable.getCurrentlySelectedRowItem().getProduct();
		if (product != null && product.isValid()) {
			unitPricesAndQuantitiesTableModel.setProduct(product);
		} else {
			unitPricesAndQuantitiesTableModel.setProduct(null);
		}
	}
	
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.addSeparator();
		
		JButton printPreviewButton = new MagicToolBarButton("printpreview", "Print Preview");
		printPreviewButton.setActionCommand(PRINT_PREVIEW_ACTION_COMMAND);
		printPreviewButton.addActionListener(this);
		
		JButton postButton = new MagicToolBarButton("invoice", "Post");
		postButton.setActionCommand(POST_ACTION_COMMAND);
		postButton.addActionListener(this);
		
//		toolBar.add(printPreviewButton);
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
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
		case POST_ACTION_COMMAND:
			try {
				int confirm = JOptionPane.showConfirmDialog(this, "Do you want to post this sales requisition?",
						"Select an Option", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.OK_OPTION) {
					SalesInvoice salesInvoice = salesRequisitionService.post(salesRequisition);
					JOptionPane.showMessageDialog(this, "Post successful!");
					getMagicFrame().switchToSalesInvoicePanel(salesInvoice);
				}
			} catch (NotEnoughStocksException e1) {
				// TODO: handle this
			}
			break;
		}
	}

}
