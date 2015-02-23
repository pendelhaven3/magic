package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.tables.StockCardInventoryReportTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.Unit;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class StockCardInventoryReportPanel extends StandardMagicPanel {

	@Autowired private ProductService productService;
	@Autowired private ReportService reportService;
	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private StockCardInventoryReportTable table;
	
	private MagicTextField productCodeField;
	private JLabel productDescriptionLabel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private EllipsisButton selectProductButton;
	private JComboBox<String> unitComboBox;
	private JCheckBox salesInvoiceTransactionTypeCheckBox;
	private JCheckBox stockQuantityConversionTransactionTypeCheckBox;
	private JCheckBox salesReturnTransactionTypeCheckBox;
	private JCheckBox adjustmentInTransactionTypeCheckBox;
	private JCheckBox adjustmentOutTransactionTypeCheckBox;
	private JCheckBox receivingReceiptTransactionTypeCheckBox;
	private JCheckBox inventoryCheckTransactionTypeCheckBox;
	private JCheckBox promoRedemptionTransactionTypeCheckBox;
	
	private JLabel totalLessQuantityLabel;
	private JLabel totalAddQuantityLabel;
	
	@Override
	protected void initializeComponents() {
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		productDescriptionLabel = new JLabel();
		
		selectProductButton = new EllipsisButton();
		selectProductButton.setToolTipText("Select Product");
		selectProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		unitComboBox = new JComboBox<>(
				new String[] {null, Unit.PIECES, Unit.DOZEN, Unit.CARTON, Unit.TIE, Unit.CASE});
		
		salesInvoiceTransactionTypeCheckBox = new JCheckBox();
		receivingReceiptTransactionTypeCheckBox = new JCheckBox();
		stockQuantityConversionTransactionTypeCheckBox = new JCheckBox();
		adjustmentOutTransactionTypeCheckBox = new JCheckBox();
		adjustmentInTransactionTypeCheckBox = new JCheckBox();
		salesReturnTransactionTypeCheckBox = new JCheckBox();
		inventoryCheckTransactionTypeCheckBox = new JCheckBox();
		promoRedemptionTransactionTypeCheckBox = new JCheckBox();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateStockCardInventoryReport();
			}
		});
		
		totalLessQuantityLabel = new JLabel();
		totalAddQuantityLabel = new JLabel();
		
		focusOnComponentWhenThisPanelIsDisplayed(productCodeField);
	}

	protected void openSelectProductDialog() {
		selectProductDialog.searchProducts(productCodeField.getText());
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			productCodeField.setText(product.getCode());
			productDescriptionLabel.setText(product.getDescription());
		}
	}

	private void generateStockCardInventoryReport() {
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
		
		StockCardInventoryReportSearchCriteria criteria = new StockCardInventoryReportSearchCriteria();
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
		
		List<StockCardInventoryReportItem> items = reportService.getStockCardInventoryReport(criteria);
		table.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		}
		totalLessQuantityLabel.setText(FormatterUtil.formatInteger(getTotalLessQuantity(items)));
		totalAddQuantityLabel.setText(FormatterUtil.formatInteger(getTotalAddQuantity(items)));
	}

	private void setTransactionTypeCriteria(StockCardInventoryReportSearchCriteria criteria) {
		List<String> transactionTypes = criteria.getTransactionTypes();
		if (salesInvoiceTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("SALES INVOICE");
		}
		if (stockQuantityConversionTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("STOCK QTY CONVERSION");
		}
		if (adjustmentInTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("ADJUSTMENT IN");
		}
		if (adjustmentOutTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("ADJUSTMENT OUT");
		}
		if (receivingReceiptTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("RECEIVING RECEIPT");
		}
		if (salesReturnTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("SALES RETURN");
		}
		if (inventoryCheckTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("INVENTORY CHECK");
		}
		if (promoRedemptionTransactionTypeCheckBox.isSelected()) {
			transactionTypes.add("PROMO REDEMPTION");
		}
	}

	private static int getTotalLessQuantity(List<StockCardInventoryReportItem> items) {
		int total = 0;
		for (StockCardInventoryReportItem item : items) {
			if (item.getLessQuantity() != null) {
				total += item.getLessQuantity();
			}
		}
		return total;
	}

	private static int getTotalAddQuantity(List<StockCardInventoryReportItem> items) {
		int total = 0;
		for (StockCardInventoryReportItem item : items) {
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
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
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
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
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
		c.gridwidth = 4;
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
		panel.add(salesInvoiceTransactionTypeCheckBox, c);
		panel.add(new JLabel("Sales Invoice"), c);
		panel.add(receivingReceiptTransactionTypeCheckBox, c);
		panel.add(new JLabel("Receiving Receipt"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(stockQuantityConversionTransactionTypeCheckBox, c);
		panel.add(new JLabel("Stock Quantity Conversion"), c);
		panel.add(salesReturnTransactionTypeCheckBox, c);
		panel.add(new JLabel("Sales Return"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(adjustmentInTransactionTypeCheckBox, c);
		panel.add(new JLabel("Adjustment In"), c);
		panel.add(inventoryCheckTransactionTypeCheckBox, c);
		panel.add(new JLabel("Inventory Check"), c);
		
		currentRow++;
		
		c.gridy = currentRow;
		panel.add(adjustmentOutTransactionTypeCheckBox, c);
		panel.add(new JLabel("Adjustment Out"), c);
		panel.add(promoRedemptionTransactionTypeCheckBox, c);
		panel.add(new JLabel("Promo Redemption"), c);
		
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

	@Override
	protected void registerKeyBindings() {
		productCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
	}

	public void updateDisplay() {
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
		unitComboBox.setSelectedItem(null);
		totalLessQuantityLabel.setText(null);
		totalAddQuantityLabel.setText(null);
		table.setItems(new ArrayList<StockCardInventoryReportItem>());
		
		salesInvoiceTransactionTypeCheckBox.setSelected(false);
		receivingReceiptTransactionTypeCheckBox.setSelected(false);
		stockQuantityConversionTransactionTypeCheckBox.setSelected(false);
		adjustmentInTransactionTypeCheckBox.setSelected(false);
		adjustmentOutTransactionTypeCheckBox.setSelected(false);
		salesReturnTransactionTypeCheckBox.setSelected(false);
		inventoryCheckTransactionTypeCheckBox.setSelected(false);
		promoRedemptionTransactionTypeCheckBox.setSelected(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToStockMovementMenuPanel();
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
		panel.add(ComponentUtil.createLabel(150, "Total Less Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalLessQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(totalLessQuantityLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Total Add Quantity:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAddQuantityLabel = ComponentUtil.createRightLabel(50);
		panel.add(totalAddQuantityLabel, c);
		
		return panel;
	}
	
}