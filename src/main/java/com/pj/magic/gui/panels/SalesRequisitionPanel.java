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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.NoSellingPriceException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.Unit;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SalesRequisitionPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(SalesRequisitionPanel.class);
	
	private static final String SAVE_CUSTOMER_ACTION_NAME = "saveCustomer";
	private static final String OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME = "openSelectCustomerDialog";
	
	@Autowired private SalesRequisitionItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionService salesRequisitionService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private CustomerService customerService;
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private PaymentTermService paymentTermService;
	
	private SalesRequisition salesRequisition;
	private JLabel salesRequisitionNumberField;
	private JTextField customerCodeField;
	private JLabel customerNameField;
	private JComboBox<PaymentTerm> paymentTermComboBox;
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
	
	// boolean flag to prevent setting combobox values from triggering db save during updateDisplay() method
	private boolean updatingDisplay;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		paymentTermComboBox = new MagicComboBox<>();
		paymentTermComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePaymentTerm();
			}
		});
		pricingSchemeComboBox = new MagicComboBox<>();
		pricingSchemeComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePricingScheme();
			}
		});
		
		modeComboBox = new MagicComboBox<>();
		modeComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"DELIVERY", "PICK-UP"}));
		modeComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveMode();
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

	protected void saveMode() {
		if (salesRequisition.getId() == null) {
			saveNewSalesRequisition();
		} else {
			if (!salesRequisition.getMode().equals(modeComboBox.getSelectedItem())) {
				salesRequisition.setMode((String)modeComboBox.getSelectedItem());
				salesRequisitionService.save(salesRequisition);
			}
		}
	}

	protected void savePricingScheme() {
		if (salesRequisition.getId() == null) {
			saveNewSalesRequisition();
		} else {
			if (!salesRequisition.getPricingScheme().equals(pricingSchemeComboBox.getSelectedItem())) {
				salesRequisition.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
				salesRequisitionService.save(salesRequisition);
				updateDisplay(salesRequisition);
			}
		}
	}

	protected void savePaymentTerm() {
		if (salesRequisition.getId() == null) {
			saveNewSalesRequisition();
		} else {
			if (!salesRequisition.getPaymentTerm().equals(paymentTermComboBox.getSelectedItem())) {
				salesRequisition.setPaymentTerm((PaymentTerm)paymentTermComboBox.getSelectedItem());
				salesRequisitionService.save(salesRequisition);
			}
		}
	}

	protected void saveRemarks() {
		if (!remarksField.getText().equals(salesRequisition.getRemarks())) {
			salesRequisition.setRemarks(remarksField.getText());
			salesRequisitionService.save(salesRequisition);
		}
	}

	protected void saveNewSalesRequisition() {
		if (updatingDisplay) {
			return;
		}
		
		if (canSaveNewSalesRequisition()) {
			try {
				salesRequisition.setPaymentTerm((PaymentTerm)paymentTermComboBox.getSelectedItem());
				salesRequisition.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
				salesRequisition.setMode((String)modeComboBox.getSelectedItem());
				salesRequisitionService.save(salesRequisition);
				updateDisplay(salesRequisition);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during saving");
			}
		}
	}

	private boolean canSaveNewSalesRequisition() {
		return paymentTermComboBox.getSelectedItem() != null  
				&& pricingSchemeComboBox.getSelectedItem() != null
				&& modeComboBox.getSelectedItem() != null;
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_CUSTOMER_ACTION_NAME);
		customerCodeField.getActionMap().put(SAVE_CUSTOMER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
		
		customerCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME);
		customerCodeField.getActionMap().put(OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});

		remarksField.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		remarksField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	// TODO: Review this again
	// TODO: Can combine selectCustomer() and saveCustomer()?
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
			customerCodeField.setText(customer.getCode());
			customerNameField.setText(customer.getName());
			pricingSchemeComboBox.setEnabled(true);
			paymentTermComboBox.setEnabled(true);
			paymentTermComboBox.setSelectedItem(customer.getPaymentTerm());
			modeComboBox.setEnabled(true);
			saveNewSalesRequisition();
			paymentTermComboBox.requestFocusInWindow();
		}
	}

	protected void saveCustomer() {
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
			paymentTermComboBox.setEnabled(true);
			paymentTermComboBox.setSelectedItem(customer.getPaymentTerm());
			modeComboBox.setEnabled(true);
			pricingSchemeComboBox.setEnabled(true);
			paymentTermComboBox.requestFocusInWindow();
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(customerCodeField);
		focusOrder.add(paymentTermComboBox);
		focusOrder.add(pricingSchemeComboBox);
		focusOrder.add(modeComboBox);
		focusOrder.add(remarksField);
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
		updatingDisplay = true;
		
		List<PricingScheme> pricingSchemes = pricingSchemeService.getAllPricingSchemes();
		pricingSchemeComboBox.setModel(
				new DefaultComboBoxModel<>(pricingSchemes.toArray(new PricingScheme[pricingSchemes.size()])));
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		paymentTermComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerms.toArray(new PaymentTerm[paymentTerms.size()])));
		
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
		paymentTermComboBox.setEnabled(true);
		paymentTermComboBox.setSelectedItem(salesRequisition.getPaymentTerm());
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
		
		updatingDisplay = false;
	}

	private void clearDisplay() {
		salesRequisitionNumberField.setText(null);
		customerCodeField.setText(null);
		customerNameField.setText(null);
		createDateField.setText(null);
		encoderField.setText(null);
		pricingSchemeComboBox.setEnabled(false);
		pricingSchemeComboBox.setSelectedItem(null);
		paymentTermComboBox.setEnabled(false);
		paymentTermComboBox.setSelectedItem(null);
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
		
		updatingDisplay = false;
	}

	private java.awt.Component createCustomerNamePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
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
				logger.error(e.getMessage(), e);
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
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Payment Term:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTermComboBox.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(paymentTermComboBox, c);
		
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
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item (F10)", true);
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
