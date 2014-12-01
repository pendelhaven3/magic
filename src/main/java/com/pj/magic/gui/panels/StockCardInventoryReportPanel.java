package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
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
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;

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
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateStockCardInventoryReport();
			}
		});
		
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

	protected void generateStockCardInventoryReport() {
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
		
		List<StockCardInventoryReportItem> items = reportService.getStockCardInventoryReport(criteria);
		table.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		}
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
	}

	private JPanel createProductPanel() {
		productCodeField.setPreferredSize(new Dimension(150, 25));
		productDescriptionLabel.setPreferredSize(new Dimension(300, 20));
		
		JPanel panel = new JPanel();
		panel.add(productCodeField);
		panel.add(selectProductButton);
		panel.add(productDescriptionLabel);
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
		table.setItems(new ArrayList<StockCardInventoryReportItem>());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
