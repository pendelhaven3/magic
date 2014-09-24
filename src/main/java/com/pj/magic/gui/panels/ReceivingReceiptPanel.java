package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
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
import com.pj.magic.gui.tables.ReceivingReceiptItemsTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Unit;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class ReceivingReceiptPanel extends AbstractMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(ReceivingReceiptPanel.class);
	
	@Autowired private ReceivingReceiptItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private PaymentTermService paymentTermService;
	
	private ReceivingReceipt receivingReceipt;
	private JLabel receivingReceiptNumberField;
	private JLabel relatedPurchaseOrderNumberField;
	private JLabel supplierField;
	private JLabel orderDateField;
	private JLabel paymentTermField;
	private JLabel receivedDateField;
	private JLabel referenceNumberField;
	private JLabel totalAmountField;
	private JLabel totalDiscountedAmountField;
	private JLabel totalNetAmountField;
	private UnitCostsAndQuantitiesTableModel unitCostsAndQuantitiesTableModel = new UnitCostsAndQuantitiesTableModel();
	private MagicToolBarButton postButton;
	
	@Override
	protected void initializeComponents() {
		supplierField = new JLabel();
		paymentTermField = new JLabel();
		referenceNumberField = new JLabel();
		
		focusOnItemsTableWhenThisPanelIsDisplayed();
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitCostsAndQuantitiesTable();
	}

	private void focusOnItemsTableWhenThisPanelIsDisplayed() {
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToReceivingReceiptListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalAmount()));
				totalDiscountedAmountField.setText(
						FormatterUtil.formatAmount(receivingReceipt.getTotalDiscountedAmount()));
				totalNetAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount()));
			}
		});
	}

	public void updateDisplay(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceiptService.getReceivingReceipt(receivingReceipt.getId());
		receivingReceipt = this.receivingReceipt;
		
		receivingReceiptNumberField.setText(receivingReceipt.getReceivingReceiptNumber().toString());
		supplierField.setText(receivingReceipt.getSupplier().getName());
		orderDateField.setText(FormatterUtil.formatDate(receivingReceipt.getOrderDate()));
		paymentTermField.setText(receivingReceipt.getPaymentTerm().getName());
		receivedDateField.setText(FormatterUtil.formatDate(receivingReceipt.getReceivedDate()));
		referenceNumberField.setText(receivingReceipt.getReferenceNumber());
		totalAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalAmount()));
		totalDiscountedAmountField.setText(
				FormatterUtil.formatAmount(receivingReceipt.getTotalDiscountedAmount()));
		totalNetAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount()));
		itemsTable.setReceivingReceipt(receivingReceipt);
		
		postButton.setEnabled(!receivingReceipt.isPosted());
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
		c.gridwidth = 7;
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
		add(ComponentUtil.createLabel(120, "RR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		receivingReceiptNumberField = ComponentUtil.createLabel(200, "");
		add(receivingReceiptNumberField, c);
		
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
		add(ComponentUtil.createLabel(150, "Related PO No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		relatedPurchaseOrderNumberField = ComponentUtil.createLabel(100, "");
		add(relatedPurchaseOrderNumberField, c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 6;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(1, 1), c);
		
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
		add(supplierField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Order Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		orderDateField = ComponentUtil.createLabel(150, "");
		add(orderDateField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Payment Term:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(paymentTermField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Received Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		receivedDateField = ComponentUtil.createLabel(100, "");
		add(receivedDateField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Reference No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		referenceNumberField = ComponentUtil.createLabel(100, "");
		add(referenceNumberField, c);

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
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		add(itemsTableScrollPane, c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
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
		add(ComponentUtil.createLabel(150, "Total Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createRightLabel(70, "");
		add(totalAmountField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Total Disc. Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalDiscountedAmountField = ComponentUtil.createRightLabel(70, "");
		add(totalDiscountedAmountField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Total Net Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountField = ComponentUtil.createRightLabel(70, "");
		add(totalNetAmountField, c);
		
	}
	
	private void initializeUnitCostsAndQuantitiesTable() {
		itemsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateUnitCostsAndQuantitiesTable();
			}
		});
	}
	
	private void updateUnitCostsAndQuantitiesTable() {
		if (itemsTable.getSelectedRow() == -1) {
			unitCostsAndQuantitiesTableModel.setProduct(null);
			return;
		}
		
		Product product = itemsTable.getCurrentlySelectedRowItem().getItem().getProduct();
		if (product != null && product.isValid()) {
			unitCostsAndQuantitiesTableModel.setProduct(productService.getProduct(product.getId()));
		} else {
			unitCostsAndQuantitiesTableModel.setProduct(null);
		}
	}
	
	private JToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postReceivingReceipt();
			}
		});
		toolBar.add(postButton);
		
		addUsernameFieldAndLogoutButton(toolBar);
		return toolBar;
	}
	
	private class UnitPricesAndQuantitiesTable extends JTable {
		
		public UnitPricesAndQuantitiesTable() {
			super(unitCostsAndQuantitiesTableModel);
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
					unitCost = product.getUnitPrice(Unit.TIE);
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
					unitCost = product.getUnitPrice(Unit.DOZEN);
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

	private void postReceivingReceipt() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		if (confirm("Do you want to post this Receiving Receipt?")) {
			try {
				receivingReceiptService.post(receivingReceipt);
				showMessage("Post successful!");
				updateDisplay(receivingReceipt);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
				updateDisplay(receivingReceipt);
			}
		}
	}

}
