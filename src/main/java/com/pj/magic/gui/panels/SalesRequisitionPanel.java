package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.NoSellingPriceException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.Unit;
import com.pj.magic.model.User;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesRequisitionPanel extends StandardMagicPanel {

	private static final String SAVE_CUSTOMER_ACTION_NAME = "saveCustomer";
	private static final String SAVE_PRICING_SCHEME_ACTION_NAME = "savePricingScheme";
	private static final String SAVE_MODE_ACTION_NAME = "saveMode";
	private static final String SAVE_REMARKS_ACTION_NAME = "saveRemarks";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = "openSelectCustomerDialog";
	
	@Autowired private SalesRequisitionItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionService salesRequisitionService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private CustomerService customerService;
	@Autowired private PricingSchemeService pricingSchemeService;
	
	private SalesRequisition salesRequisition;
	private JLabel salesRequisitionNumberField;
	private JTextField customerCodeField;
	private JLabel customerNameField;
	private JLabel createDateField;
	private JLabel encoderField;
	private JComboBox<String> modeComboBox;
	private JComboBox<PricingScheme> pricingSchemeComboBox;
	private MagicTextField remarksField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = new UnitPricesAndQuantitiesTableModel();
	private JButton selectCustomerButton;
	private MagicToolBarButton addItemButton;
	private MagicToolBarButton deleteItemButton;
	private MagicToolBarButton postButton;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		pricingSchemeComboBox = new JComboBox<>();
		
		modeComboBox = new JComboBox<>();
		modeComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"DELIVERY", "PICK-UP"}));
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});;
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		updateTotalAmountFieldWhenItemsTableChanges();
		initializeUnitPricesAndQuantitiesTable();
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_CUSTOMER_ACTION_NAME);
		customerCodeField.getActionMap().put(SAVE_CUSTOMER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomerField();
			}
		});
		
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME);
		customerCodeField.getActionMap().put(OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});

		pricingSchemeComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				SAVE_PRICING_SCHEME_ACTION_NAME);
		pricingSchemeComboBox.getActionMap().put(SAVE_PRICING_SCHEME_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				savePricingSchemeField();
			}
		});
		
		modeComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				SAVE_MODE_ACTION_NAME);
		modeComboBox.getActionMap().put(SAVE_MODE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveModeField();
			}
		});
		
		remarksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				SAVE_REMARKS_ACTION_NAME);
		remarksField.getActionMap().put(SAVE_REMARKS_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRemarksField();
			}
		});
		
	}

	protected void saveRemarksField() {
		if (!remarksField.getText().equals(salesRequisition.getRemarks())) {
			salesRequisition.setRemarks(remarksField.getText());
			salesRequisitionService.save(salesRequisition);
		}
		itemsTable.highlight();
	}

	protected void saveModeField() {
		String mode = (String)modeComboBox.getSelectedItem();
		if ((mode == null && salesRequisition.getMode() != null)
				|| (mode != null && !mode.equals(salesRequisition.getMode()))) {
			salesRequisition.setMode(mode);
			salesRequisitionService.save(salesRequisition);
		}
		focusNextField();
	}

	protected void savePricingSchemeField() {
		try {
			validateMandatoryField(pricingSchemeComboBox, "Pricing Scheme");
			if (salesRequisition.getId() == null) {
				salesRequisition.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
				salesRequisition.setCreateDate(new Date());
				salesRequisition.setEncoder(new User(1L)); // TODO: Replace with actual user later
				salesRequisitionService.save(salesRequisition);
				updateDisplay(salesRequisition);
			} else {
				if (!pricingSchemeComboBox.getSelectedItem().equals(salesRequisition.getPricingScheme())) {
					salesRequisition.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
					salesRequisitionService.save(salesRequisition);
					// TODO: update prices in itemsTable
				}
			}
			focusNextField();
		} catch (ValidationException ex) {
			// do nothing
		}
	}

	protected void selectCustomer() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			if (salesRequisition.getCustomer() != null && salesRequisition.getCustomer().equals(customer)) {
				// skip saving sales requisition since there is no change
				focusNextField();
				return;
			}
			
			salesRequisition.setCustomer(customer);
			if (salesRequisition.hasMinimumFieldsFilledUp()) {
				try {
					salesRequisitionService.save(salesRequisition);
				} catch (Exception e) {
					showErrorMessage("Error occurred during saving!");
					return;
				}
			}
			
			customerCodeField.setText(customer.getCode());
			customerNameField.setText(customer.getName());
			pricingSchemeComboBox.setEnabled(true);
			pricingSchemeComboBox.requestFocusInWindow();
		}
	}

	protected void saveCustomerField() {
		if (salesRequisition.getCustomer() != null) {
			if (salesRequisition.getCustomer().getCode().equals(customerCodeField.getText())) {
				// skip saving sales requisition since there is no change in customer
				focusNextField();
				return;
			}
		}
		
		if (StringUtils.isEmpty(customerCodeField.getText())) {
			showErrorMessage("Customer must be specified");
			return;
		}
		
		Customer customer = customerService.findCustomerByCode(customerCodeField.getText());
		if (customer == null) {
			showErrorMessage("No customer matching code specified");
			return;
		} else {
			salesRequisition.setCustomer(customer);
			if (salesRequisition.hasMinimumFieldsFilledUp()) {
				try {
					salesRequisitionService.save(salesRequisition);
				} catch (Exception e) {
					showErrorMessage("Error occurred during saving!");
					return;
				}
			}
			customerNameField.setText(customer.getName());
			pricingSchemeComboBox.setEnabled(true);
			pricingSchemeComboBox.requestFocusInWindow();
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(customerCodeField);
		focusOrder.add(pricingSchemeComboBox);
		focusOrder.add(modeComboBox);
		focusOrder.add(remarksField);
		focusOrder.add(itemsTable);
	}
	
	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToSalesRequisitionsListPanel();
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

	public void updateDisplay(SalesRequisition salesRequisition) {
		List<PricingScheme> pricingSchemes = pricingSchemeService.getAllPricingSchemes();
		pricingSchemeComboBox.setModel(
				new DefaultComboBoxModel<>(pricingSchemes.toArray(new PricingScheme[pricingSchemes.size()])));
		
		if (salesRequisition.getId() == null) {
			this.salesRequisition = salesRequisition;
			clearDisplay();
			return;
		}
		
		this.salesRequisition = salesRequisitionService.getSalesRequisition(salesRequisition.getId());
		salesRequisition = this.salesRequisition;
		
		salesRequisitionNumberField.setText(salesRequisition.getSalesRequisitionNumber().toString());
		if (salesRequisition.getCustomer() == null) {
			customerCodeField.setText("");
			customerNameField.setText("");
		} else {
			customerCodeField.setText(salesRequisition.getCustomer().getCode());
			customerNameField.setText(salesRequisition.getCustomer().getName());
		}
		createDateField.setText(FormatterUtil.formatDate(salesRequisition.getCreateDate()));
		encoderField.setText(salesRequisition.getEncoder().getUsername());
		pricingSchemeComboBox.setEnabled(true);
		pricingSchemeComboBox.setSelectedItem(salesRequisition.getPricingScheme());
		modeComboBox.setEnabled(true);
		modeComboBox.setSelectedItem(salesRequisition.getMode());
		remarksField.setEnabled(true);
		remarksField.setText(salesRequisition.getRemarks());
		totalItemsField.setText(String.valueOf(salesRequisition.getTotalNumberOfItems()));
		totalAmountField.setText(salesRequisition.getTotalAmount().toString());
		itemsTable.setSalesRequisition(salesRequisition);
		
		postButton.setEnabled(!salesRequisition.isPosted());
		addItemButton.setEnabled(!salesRequisition.isPosted());
		deleteItemButton.setEnabled(!salesRequisition.isPosted());
	}

	private void clearDisplay() {
		salesRequisitionNumberField.setText(null);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		createDateField.setText(null);
		encoderField.setText(null);
		pricingSchemeComboBox.setEnabled(false);
		pricingSchemeComboBox.setSelectedItem(null);
		modeComboBox.setEnabled(false);
		modeComboBox.setSelectedItem(null);
		remarksField.setEnabled(false);
		remarksField.setText(null);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setSalesRequisition(salesRequisition);
		
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
	}

	private java.awt.Component createCustomerNamePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(customerCodeField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(selectCustomerButton, c);
		
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
		panel.add(customerNameField, c);
		
		return panel;
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
			
			product = productService.findProductByCodeAndPricingScheme(
					product.getCode(), salesRequisition.getPricingScheme());
			
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

	private void postSalesRequisition() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		int confirm = showConfirmMessage("Do you want to post this sales requisition?");
		if (confirm == JOptionPane.OK_OPTION) {
			if (!salesRequisition.hasItems()) {
				showErrorMessage("Cannot post a sales requisition with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			try {
				SalesInvoice salesInvoice = salesRequisitionService.post(salesRequisition);
				JOptionPane.showMessageDialog(this, "Post successful!");
				getMagicFrame().switchToSalesInvoicePanel(salesInvoice);
			} catch (NotEnoughStocksException e ) {	
				showErrorMessage("Not enough available stocks!");
				updateDisplay(salesRequisition);
				itemsTable.highlightColumn(e.getSalesRequisitionItem(), 
						SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX);
			} catch (NoSellingPriceException e) {
				showErrorMessage("No selling price!");
				updateDisplay(salesRequisition);
				itemsTable.highlightColumn(e.getSalesRequisitionItem(), 
						SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX);
			} catch (Exception e) {
				showErrorMessage("Unexpected error occurred during posting!");
			}
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
		mainPanel.add(ComponentUtil.createLabel(150, "SR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		salesRequisitionNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(salesRequisitionNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Create Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		createDateField = ComponentUtil.createLabel(150, "");
		mainPanel.add(createDateField, c);
		
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
		
		customerCodeField.setPreferredSize(new Dimension(100, 20));
		customerNameField = ComponentUtil.createLabel(190, "");
		
		mainPanel.add(createCustomerNamePanel(), c);
		
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
		encoderField = ComponentUtil.createLabel(180, "");
		mainPanel.add(encoderField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Pricing Scheme:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeComboBox.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(pricingSchemeComboBox, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mode:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		modeComboBox.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(modeComboBox, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Remarks:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(250, 20));
		mainPanel.add(remarksField, c);

		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

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
		infoTableScrollPane.setPreferredSize(new Dimension(500, 65));
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
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postSalesRequisition();
			}
		});
		
		toolBar.add(postButton);
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
				itemsTable.removeCurrentlySelectedRow();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
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
