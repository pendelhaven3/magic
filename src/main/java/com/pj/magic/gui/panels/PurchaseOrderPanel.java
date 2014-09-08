package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.PurchaseOrderItemsTable;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseOrderPanel extends AbstractMagicPanel implements ActionListener {

	private static final String SAVE_SUPPLIER_ACTION_NAME = "saveSupplier";
	private static final String SAVE_PAYMENT_TERM_ACTION_NAME = "savePaymentTerm";
	private static final String SAVE_REMARKS_ACTION_NAME = "saveRemarks";
	private static final String SAVE_REFERENCE_NUMBER_ACTION_NAME = "saveReferenceNumber";
	private static final String ORDER_ACTION_COMMAND = "order";
	private static final String POST_ACTION_COMMAND = "post";
	
	@Autowired private PurchaseOrderItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private SupplierService supplierService;
	@Autowired private PaymentTermService paymentTermService;
	
	private PurchaseOrder purchaseOrder;
	private JLabel purchaseOrderNumberField;
	private JLabel statusField;
	private JComboBox<Supplier> supplierComboBox;
	private JComboBox<PaymentTerm> paymentTermComboBox;
	private MagicTextField remarksField;
	private MagicTextField referenceNumberField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
	private MagicToolBarButton orderButton;
	private MagicToolBarButton postButton;
	
	@Override
	protected void initializeComponents() {
		supplierComboBox = new JComboBox<>();
		paymentTermComboBox = new JComboBox<>();

		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		
		referenceNumberField = new MagicTextField();
		referenceNumberField.setMaximumLength(30);
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierComboBox);
		
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
	}

	@Override
	protected void registerKeyBindings() {
		supplierComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_SUPPLIER_ACTION_NAME);
		supplierComboBox.getActionMap().put(SAVE_SUPPLIER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSupplier();
			}
		});
		
		paymentTermComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				SAVE_PAYMENT_TERM_ACTION_NAME);
		paymentTermComboBox.getActionMap().put(SAVE_PAYMENT_TERM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentTerm();
			}
		});
		
		remarksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_REMARKS_ACTION_NAME);
		remarksField.getActionMap().put(SAVE_REMARKS_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRemarks();
			}
		});
		
		referenceNumberField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				SAVE_REFERENCE_NUMBER_ACTION_NAME);
		referenceNumberField.getActionMap().put(SAVE_REFERENCE_NUMBER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveReferenceNumber();
			}
		});
	}

	protected void saveReferenceNumber() {
		if (!referenceNumberField.getText().equals(purchaseOrder.getReferenceNumber())) {
			purchaseOrder.setReferenceNumber(referenceNumberField.getText());
			purchaseOrderService.save(purchaseOrder);
		}
		focusNextField();
	}

	protected void saveRemarks() {
		if (!remarksField.getText().equals(purchaseOrder.getRemarks())) {
			purchaseOrder.setRemarks(remarksField.getText());
			purchaseOrderService.save(purchaseOrder);
		}
		itemsTable.highlight();
	}

	protected void savePaymentTerm() {
		PaymentTerm paymentTerm = (PaymentTerm)paymentTermComboBox.getSelectedItem();
		if ((paymentTerm == null && purchaseOrder.getPaymentTerm() != null)
				|| (paymentTerm != null && !paymentTerm.equals(purchaseOrder.getPaymentTerm()))) {
			purchaseOrder.setPaymentTerm(paymentTerm);
			purchaseOrderService.save(purchaseOrder);
		}
		focusNextField();
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(supplierComboBox);
		focusOrder.add(referenceNumberField);
		focusOrder.add(paymentTermComboBox);
		focusOrder.add(remarksField);
	}
	
	protected void saveSupplier() {
		try {
			validateMandatoryField(supplierComboBox, "Supplier");
		} catch (ValidationException e) {
			return;
		}
		
		Supplier supplier = (Supplier)supplierComboBox.getSelectedItem();
		if (!supplier.equals(purchaseOrder.getSupplier())) {
			purchaseOrder.setSupplier(supplier);
			try {
				purchaseOrderService.save(purchaseOrder);
				updateDisplay(purchaseOrder);
			} catch (Exception e) {
				showErrorMessage("Error occurred during saving!");
				return;
			}
		}
		
		focusNextField();
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
//				totalAmountField.setText(FormatterUtil.formatAmount(itemsTable.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(PurchaseOrder purchaseOrder) {
		List<Supplier> suppliers = supplierService.getAllSuppliers();
		supplierComboBox.setModel(
				new DefaultComboBoxModel<>(suppliers.toArray(new Supplier[suppliers.size()])));
		
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
		supplierComboBox.setSelectedItem(purchaseOrder.getSupplier());
		paymentTermComboBox.setEnabled(true);
		paymentTermComboBox.setSelectedItem(purchaseOrder.getPaymentTerm());
		remarksField.setEnabled(true);
		remarksField.setText(purchaseOrder.getRemarks());
		referenceNumberField.setEnabled(true);
		referenceNumberField.setText(purchaseOrder.getReferenceNumber());
//		totalItemsField.setText(String.valueOf(purchaseOrder.getTotalNumberOfItems()));
//		totalAmountField.setText(purchaseOrder.getTotalAmount().toString());
		itemsTable.setPurchaseOrder(purchaseOrder);
		
		orderButton.setEnabled(!purchaseOrder.isOrdered());
		postButton.setEnabled(purchaseOrder.isOrdered() && !purchaseOrder.isPosted());
	}

	private void clearDisplay() {
		purchaseOrderNumberField.setText(null);
		statusField.setText(null);
		supplierComboBox.setSelectedItem(null);
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
		
		supplierComboBox.setPreferredSize(new Dimension(300, 20));
		add(supplierComboBox, c);
		
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
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(150, "");
		add(totalItemsField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Total Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(150, "");
		add(totalAmountField, c);
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
		JToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		
		orderButton = new MagicToolBarButton("order", "Order");
		orderButton.setActionCommand(ORDER_ACTION_COMMAND);
		orderButton.addActionListener(this);
		toolBar.add(orderButton);
		
		postButton = new MagicToolBarButton("post", "Post");
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
		case ORDER_ACTION_COMMAND:
			orderPurchaseOrder();
			break;
		case POST_ACTION_COMMAND:
			postPurchaseOrder();
			break;
		}
	}
	
	private void orderPurchaseOrder() {
		if (confirm("Mark PO as ordered?")) {
			purchaseOrderService.order(purchaseOrder);
			updateDisplay(purchaseOrder);
			itemsTable.highlight();
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
				updateDisplay(purchaseOrder);
//				getMagicFrame().switchToReceivingReceiptPanel(receivingReceipt);
//			} catch (NotEnoughStocksException e ) {	
//				showErrorMessage("Not enough available stocks!");
//				updateDisplay(purchaseOrder);
//				itemsTable.highlightColumn(e.getPurchaseOrderItem(), 
//						PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX);
//			} catch (NoSellingPriceException e) {
//				showErrorMessage("No selling price!");
//				updateDisplay(purchaseOrder);
//				itemsTable.highlightColumn(e.getPurchaseOrderItem(), 
//						PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX);
			} catch (Exception e) {
				showErrorMessage("Unexpected error occurred during posting!");
			}
		}
	}

}
