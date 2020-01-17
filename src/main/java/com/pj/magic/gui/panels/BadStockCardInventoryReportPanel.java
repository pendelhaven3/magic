package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.SelectProductEllipsisButton;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.BadStockCardInventoryReportItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.search.BadStockCardInventoryReportCriteria;
import com.pj.magic.service.BadStockService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

public class BadStockCardInventoryReportPanel extends StandardMagicPanel {

	private static final int POST_DATE_COLUMN_INDEX = 0;
	private static final int TRANSACTION_NUMBER_COLUMN_INDEX = 1;
	private static final int SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX = 2;
	private static final int TRANSACTION_TYPE_COLUMN_INDEX = 3;
	private static final int UNIT_COLUMN_INDEX = 4;
	private static final int ADD_QUANTITY_COLUMN_INDEX = 5;
	private static final int LESS_QUANTITY_COLUMN_INDEX = 6;
	private static final int CURRENT_COST_COLUMN_INDEX = 7;
	private static final int AMOUNT_COLUMN_INDEX = 8;
	private static final int REFERENCE_NUMBER_COLUMN_INDEX = 9;
	
	@Autowired private ProductService productService;
	@Autowired private ReportService reportService;
	@Autowired private BadStockService badStockService;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private MagicTextField productCodeField = new MagicTextField();
	private JLabel productDescriptionLabel = new JLabel();
	private UtilCalendarModel fromDateModel = new UtilCalendarModel();
	private UtilCalendarModel toDateModel = new UtilCalendarModel();
	private JButton generateButton;
	private EllipsisButton selectProductButton;
	private JComboBox<String> unitComboBox;
	private JCheckBox badStockReturnTransactionTypeCheckBox = new JCheckBox();
	private JCheckBox purchaseReturnBadStockTransactionTypeCheckBox = new JCheckBox();
	private JCheckBox badStockAdjustmentInTransactionTypeCheckBox = new JCheckBox();
	private JCheckBox badStockAdjustmentOutTransactionTypeCheckBox = new JCheckBox();
	private JCheckBox badStockReportTransactionTypeCheckBox = new JCheckBox();
	private JLabel currentQuantityLabel;
	private JLabel totalLessQuantityLabel = new JLabel();
	private JLabel totalAddQuantityLabel = new JLabel();
	private JLabel quantityDifferenceLabel;
	private MagicListTable table;
	private BadStockCardInventoryReportTableModel tableModel = new BadStockCardInventoryReportTableModel();
	
	public BadStockCardInventoryReportPanel() {
		setTitle("Bad Stock Card Inventory Report");
	}
	
	@Override
	protected void initializeComponents() {
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		selectProductButton = new SelectProductEllipsisButton(selectProductDialog, productCodeField, productDescriptionLabel);
		
		unitComboBox = new JComboBox<>(
				new String[] {null, Unit.PIECES, Unit.DOZEN, Unit.CARTON, Unit.TIE, Unit.CASE});
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(e -> generateBadStockCardInventoryReport());
		
		focusOnComponentWhenThisPanelIsDisplayed(productCodeField);
		
		initializeTable();
	}

	private void initializeTable() {
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(POST_DATE_COLUMN_INDEX).setPreferredWidth(150);
		columnModel.getColumn(SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ADD_QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(LESS_QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
	}

	private void generateBadStockCardInventoryReport() {
		String productCode = productCodeField.getText();
		if (StringUtils.isEmpty(productCode)) {
			showErrorMessage("Product Code must be specified");
			return;
		}
		
		Product product = productService.findProductByCode(productCode);
		if (product == null) {
			showErrorMessage("No product matching code specified");
			return;
		} else {
			productCodeField.setText(product.getCode());
			productDescriptionLabel.setText(product.getDescription());
		}
		
		BadStockCardInventoryReportCriteria criteria = new BadStockCardInventoryReportCriteria();
		criteria.setProduct(product);
		if (fromDateModel.getValue() != null) {
			criteria.setFromDate(fromDateModel.getValue().getTime());
		}
		if (toDateModel.getValue() != null) {
			criteria.setToDate(toDateModel.getValue().getTime());
		}
		if (unitComboBox.getSelectedItem() != null) {
			criteria.setUnit((String)unitComboBox.getSelectedItem());
		}
		setTransactionTypeCriteria(criteria);
		
		List<BadStockCardInventoryReportItem> items = reportService.getBadStockCardInventoryReport(criteria);
		tableModel.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		} else {
			table.selectFirstRow();
		}
		
		if (unitComboBox.getSelectedIndex() > 0) {
			BadStock badStock = badStockService.getBadStock(product);
			
			int totalLessQuantity = getTotalLessQuantity(items);
			int totalAddQuantity = getTotalAddQuantity(items);
			currentQuantityLabel.setText(FormatterUtil.formatInteger(badStock.getUnitQuantity((String)unitComboBox.getSelectedItem())));
			totalLessQuantityLabel.setText(FormatterUtil.formatInteger(totalLessQuantity));
			totalAddQuantityLabel.setText(FormatterUtil.formatInteger(totalAddQuantity));
			quantityDifferenceLabel.setText(FormatterUtil.formatInteger(totalAddQuantity - totalLessQuantity));
		} else {
			ComponentUtil.clearLabels(currentQuantityLabel, totalLessQuantityLabel, totalAddQuantityLabel,
					quantityDifferenceLabel);
		}
	}

	private void setTransactionTypeCriteria(BadStockCardInventoryReportCriteria criteria) {
		List<String> transactionTypes = criteria.getTransactionTypes();
		if (badStockReturnTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("BAD STOCK RETURN");
		}
		if (purchaseReturnBadStockTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("PURCHASE RETURN BAD STOCK");
		}
		if (badStockAdjustmentInTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("BAD STOCK ADJUSTMENT IN");
		}
		if (badStockAdjustmentOutTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("BAD STOCK ADJUSTMENT OUT");
		}
		if (badStockReportTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("BAD STOCK REPORT");
		}
	}

	private static int getTotalLessQuantity(List<BadStockCardInventoryReportItem> items) {
		int total = 0;
		for (BadStockCardInventoryReportItem item : items) {
			if (item.getLessQuantity() != null) {
				total += item.getLessQuantity();
			}
		}
		return total;
	}

	private static int getTotalAddQuantity(List<BadStockCardInventoryReportItem> items) {
		int total = 0;
		for (BadStockCardInventoryReportItem item : items) {
			if (item.getAddQuantity() != null) {
				total += item.getAddQuantity();
			}
		}
		return total;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Product Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 5;
		mainPanel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "From Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(fromDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "To Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		datePanel = new JDatePanelImpl(toDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 6;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		unitComboBox.setPreferredSize(new Dimension(60, 25));
		mainPanel.add(unitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Transaction Type: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createTransactionTypesPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.gridwidth = 4;
		generateButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createTransactionTypesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets.right = 5;
		
		c.gridy = currentRow;
		panel.add(badStockReturnTransactionTypeCheckBox, c);
		panel.add(new JLabel("Bad Stock Return"), c);
		panel.add(badStockAdjustmentInTransactionTypeCheckBox, c);
		panel.add(new JLabel("Bad Stock Adjustment In"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(purchaseReturnBadStockTransactionTypeCheckBox, c);
		panel.add(new JLabel("Purchase Return Bad Stock"), c);
		panel.add(badStockAdjustmentOutTransactionTypeCheckBox, c);
		panel.add(new JLabel("Bad Stock Adjustment Out"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(badStockReportTransactionTypeCheckBox, c);
		panel.add(new JLabel("Bad Stock Report"), c);
		
		return panel;
	}

	private JPanel createProductPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		productCodeField.setPreferredSize(new Dimension(150, 25));
		panel.add(productCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectProductButton.setPreferredSize(new Dimension(30, 25));
		panel.add(selectProductButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		productDescriptionLabel.setPreferredSize(new Dimension(300, 20));
		panel.add(productDescriptionLabel, c);
		
		return panel;
	}

	public void updateDisplay() {
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
		unitComboBox.setSelectedItem(null);
		ComponentUtil.clearLabels(currentQuantityLabel, totalLessQuantityLabel, totalAddQuantityLabel, quantityDifferenceLabel);
		tableModel.clear();
		
		badStockReturnTransactionTypeCheckBox.setSelected(false);
		purchaseReturnBadStockTransactionTypeCheckBox.setSelected(false);
		badStockAdjustmentInTransactionTypeCheckBox.setSelected(false);
		badStockAdjustmentOutTransactionTypeCheckBox.setSelected(false);
		badStockReportTransactionTypeCheckBox.setSelected(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToBadStockMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Current Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.insets.right = 20;
		currentQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(currentQuantityLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Add Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAddQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(totalAddQuantityLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Less Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.insets.right = 10;
		totalLessQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(totalLessQuantityLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Difference:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		quantityDifferenceLabel = ComponentUtil.createRightLabel(50);
		panel.add(quantityDifferenceLabel, c);
		
		return panel;
	}
	
	@Override
	protected void registerKeyBindings() {
	}
	
	private class BadStockCardInventoryReportTableModel extends ListBackedTableModel<BadStockCardInventoryReportItem> {

		private final String[] columnNames = 
			{"Post Date", "Trans. No.", "Supplier/Customer", "Trans. Type", "Unit", "Add Qty", "Less Qty", 
				"Cost / Price", "Amount", "Ref. No."};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			BadStockCardInventoryReportItem item = getItem(rowIndex);
			switch (columnIndex) {
			case POST_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(item.getPostDate());
			case TRANSACTION_NUMBER_COLUMN_INDEX:
				return item.getTransactionNumber();
			case SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX:
				return item.getSupplierOrCustomerName();
			case TRANSACTION_TYPE_COLUMN_INDEX:
				return item.getTransactionType();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case ADD_QUANTITY_COLUMN_INDEX:
				return item.getAddQuantity();
			case LESS_QUANTITY_COLUMN_INDEX:
				return item.getLessQuantity();
			case CURRENT_COST_COLUMN_INDEX:
				BigDecimal cost = item.getCurrentCost();
				return (cost != null) ? FormatterUtil.formatAmount(cost) : null;
			case AMOUNT_COLUMN_INDEX:
				BigDecimal amount = item.getAmount();
				return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
			case REFERENCE_NUMBER_COLUMN_INDEX:
				return item.getReferenceNumber();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case CURRENT_COST_COLUMN_INDEX:
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}

}