package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
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

import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.PurchaseOrderItemsTable;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseOrderPanel extends AbstractMagicPanel {

	private static final String OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME = "openSelectSupplierDialog";
	private static final String FOCUS_NEXT_FIELD_ACTION_NAME = "focusNextField";
	
	private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderPanel.class);
	
	@Autowired private PurchaseOrderItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private SupplierService supplierService;
	@Autowired private PaymentTermService paymentTermService;
	@Autowired private PrintService printService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private PurchaseOrder purchaseOrder;
	private JLabel purchaseOrderNumberField;
	private JLabel statusField;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameField;
	private JComboBox<PaymentTerm> paymentTermComboBox;
	private MagicTextField remarksField;
	private MagicTextField referenceNumberField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private UnitCostsAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitCostsAndQuantitiesTableModel();
	private MagicToolBarButton orderButton;
	private MagicToolBarButton postButton;
	private MagicToolBarButton addItemButton;
	private MagicToolBarButton deleteItemButton;
	private MagicToolBarButton printButton;
	private JButton selectSupplierButton;
	
	@Override
	protected void initializeComponents() {
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(15);
		
		paymentTermComboBox = new JComboBox<>();
		paymentTermComboBox.addFocusListener(new FocusAdapter() {
			
			public void focusLost(FocusEvent e) {
				savePaymentTerm();
			}
		});
			
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		referenceNumberField = new MagicTextField();
		referenceNumberField.setMaximumLength(30);
		referenceNumberField.addFocusListener(new FocusAdapter() {
			
			public void focusLost(FocusEvent e) {
				saveReferenceNumber();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
		
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
		
		selectSupplierButton = new EllipsisButton();
		selectSupplierButton.setToolTipText("Select Supplier (F5)");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
	}

	protected void selectSupplier() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier selectedSupplier = selectSupplierDialog.getSelectedSupplier();
		if (selectedSupplier != null) {
			saveSupplier(selectedSupplier);
		}
	}

	private void saveSupplier(Supplier supplier) {
		if (!supplier.equals(purchaseOrder.getSupplier())) {
			purchaseOrder.setSupplier(supplier);
			supplierCodeField.setText(supplier.getCode());
			supplierNameField.setText(supplier.getName());
			
			purchaseOrder.setPaymentTerm(supplier.getPaymentTerm());
			paymentTermComboBox.setEnabled(true);
			paymentTermComboBox.setSelectedItem(supplier.getPaymentTerm());
			
			try {
				purchaseOrderService.save(purchaseOrder);
				updateDisplay(purchaseOrder);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
		}
		
		if (purchaseOrder.isOrdered()) {
			referenceNumberField.requestFocusInWindow();
		} else {
	 		paymentTermComboBox.requestFocusInWindow();
		}
	}
	
	@Override
	protected void registerKeyBindings() {
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
				OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME);
		supplierCodeField.getActionMap().put(OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				FOCUS_NEXT_FIELD_ACTION_NAME);
		supplierCodeField.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
				if (supplier != null) {
					saveSupplier(supplier);
				} else {
					showErrorMessage("No supplier matching code specified");
				}
			}
		});
		
		paymentTermComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				FOCUS_NEXT_FIELD_ACTION_NAME);
		paymentTermComboBox.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				remarksField.requestFocusInWindow();
			}
		});
		
		remarksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), FOCUS_NEXT_FIELD_ACTION_NAME);
		remarksField.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
		
		referenceNumberField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				FOCUS_NEXT_FIELD_ACTION_NAME);
		referenceNumberField.getActionMap().put(FOCUS_NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				paymentTermComboBox.requestFocusInWindow();
			}
		});
	}

	protected void saveReferenceNumber() {
		if (!referenceNumberField.getText().equals(purchaseOrder.getReferenceNumber())) {
			purchaseOrder.setReferenceNumber(referenceNumberField.getText());
			purchaseOrderService.save(purchaseOrder);
		}
	}

	protected void saveRemarks() {
		if (!remarksField.getText().equals(purchaseOrder.getRemarks())) {
			purchaseOrder.setRemarks(remarksField.getText());
			purchaseOrderService.save(purchaseOrder);
		}
	}

	protected void savePaymentTerm() {
		PaymentTerm paymentTerm = (PaymentTerm)paymentTermComboBox.getSelectedItem();
		if ((paymentTerm == null && purchaseOrder.getPaymentTerm() != null)
				|| (paymentTerm != null && !paymentTerm.equals(purchaseOrder.getPaymentTerm()))) {
			purchaseOrder.setPaymentTerm(paymentTerm);
			purchaseOrderService.save(purchaseOrder);
		}
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToPurchaseOrderListPanel();
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

	public void updateDisplay(PurchaseOrder purchaseOrder) {
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		paymentTermComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerms.toArray(new PaymentTerm[paymentTerms.size()])));
		
		if (purchaseOrder.getId() == null) {
			this.purchaseOrder = purchaseOrder;
			clearDisplay();
			return;
		}
		
		this.purchaseOrder = purchaseOrderService.getPurchaseOrder(purchaseOrder.getId());
		purchaseOrder = this.purchaseOrder;
		
		purchaseOrderNumberField.setText(purchaseOrder.getPurchaseOrderNumber().toString());
		statusField.setText(purchaseOrder.getStatus());
		supplierCodeField.setText(purchaseOrder.getSupplier().getCode());
		supplierNameField.setText(purchaseOrder.getSupplier().getName());
		paymentTermComboBox.setEnabled(true);
		paymentTermComboBox.setSelectedItem(purchaseOrder.getPaymentTerm());
		remarksField.setEnabled(true);
		remarksField.setText(purchaseOrder.getRemarks());
		referenceNumberField.setEnabled(purchaseOrder.isOrdered());
		referenceNumberField.setText(purchaseOrder.getReferenceNumber());
		itemsTable.setPurchaseOrder(purchaseOrder);
		
		orderButton.setEnabled(!purchaseOrder.isOrdered());
		postButton.setEnabled(purchaseOrder.isOrdered() && !purchaseOrder.isPosted());
		addItemButton.setEnabled(true);
		deleteItemButton.setEnabled(true);
	}

	private void clearDisplay() {
		purchaseOrderNumberField.setText(null);
		statusField.setText(null);
		supplierCodeField.setText(null);
		supplierNameField.setText(null);
		paymentTermComboBox.setEnabled(false);
		paymentTermComboBox.setSelectedItem(null);
		remarksField.setEnabled(false);
		remarksField.setText(null);
		referenceNumberField.setEnabled(false);
		referenceNumberField.setText(null);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setPurchaseOrder(purchaseOrder);
		
		orderButton.setEnabled(false);
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
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
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);

		currentRow++;
		
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
		add(ComponentUtil.createLabel(120, "PO No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		purchaseOrderNumberField = ComponentUtil.createLabel(200, "");
		add(purchaseOrderNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(50, 1), c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(100, "");
		add(statusField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(createSupplierPanel(), c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Reference No.:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		referenceNumberField.setPreferredSize(new Dimension(100, 20));
		add(referenceNumberField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Payment Terms:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermComboBox.setPreferredSize(new Dimension(100, 20));
		add(paymentTermComboBox, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 20));
		add(remarksField, c);

		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		add(createItemsTableToolBar(), c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		add(itemsTableScrollPane, c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane infoTableScrollPane = new JScrollPane(new UnitPricesAndQuantitiesTable());
		infoTableScrollPane.setPreferredSize(new Dimension(500, 65));
		add(infoTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(150, "");
		panel.add(totalItemsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(150, "");
		panel.add(totalAmountField, c);
		
		return panel;
	}

	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(100, 20));
		panel.add(supplierCodeField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(selectSupplierButton, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameField = ComponentUtil.createLabel(200, "");
		panel.add(supplierNameField, c);
		
		return panel;
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
				if (e.getColumn() == PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
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
		MagicToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		
		orderButton = new MagicToolBarButton("order", "Order");
		orderButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				orderPurchaseOrder();
			}
		});
		toolBar.add(orderButton);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postPurchaseOrder();
			}
		});
		toolBar.add(postButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(purchaseOrder);
			}
		});
		toolBar.add(printButton);
		
		addUsernameFieldAndLogoutButton(toolBar);
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
	
	private class UnitCostsAndQuantitiesTableModel extends AbstractTableModel {

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
					BigDecimal unitCost = product.getGrossCost(Unit.CASE);
					if (unitCost == null) {
						unitCost = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitCost);
				case 4:
					return String.valueOf(product.getUnitQuantity(Unit.TIE));
				case 5:
					unitCost = product.getGrossCost(Unit.TIE);
					if (unitCost == null) {
						unitCost = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitCost);
				}
			} else if (rowIndex == 1) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity(Unit.CARTON));
				case 2:
					BigDecimal unitCost = product.getGrossCost(Unit.CARTON);
					if (unitCost == null) {
						unitCost = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitCost);
				case 4:
					return String.valueOf(product.getUnitQuantity(Unit.DOZEN));
				case 5:
					unitCost = product.getGrossCost(Unit.DOZEN);
					if (unitCost == null) {
						unitCost = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitCost);
				}
			} else if (rowIndex == 2) {
				switch (columnIndex) {
				case 1:
					return String.valueOf(product.getUnitQuantity(Unit.PIECES));
				case 2:
					BigDecimal unitCost = product.getGrossCost(Unit.PIECES);
					if (unitCost == null) {
						unitCost = BigDecimal.ZERO;
					}
					return FormatterUtil.formatAmount(unitCost);
				}
			}
			return "";
		}
		
	}

	private void orderPurchaseOrder() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (!purchaseOrder.hasItems()) {
			showErrorMessage("Cannot order when there are no items");
			return;
		}
		
		if (confirm("Mark PO as ordered?")) {
			purchaseOrderService.order(purchaseOrder);
			updateDisplay(purchaseOrder);
			referenceNumberField.requestFocusInWindow();
		}
	}

	private void postPurchaseOrder() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (confirm("Do you want to post this Purchase Order?")) {
			try {
				ReceivingReceipt receivingReceipt = purchaseOrderService.post(purchaseOrder);
				showMessage("Post successful!");
				getMagicFrame().switchToReceivingReceiptPanel(receivingReceipt);
			} catch (Exception e) {
				e.printStackTrace();
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

}
