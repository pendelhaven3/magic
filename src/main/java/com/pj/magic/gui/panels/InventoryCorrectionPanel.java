package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.SelectProductEllipsisButton;
import com.pj.magic.gui.component.UnitComboBox;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.Product;
import com.pj.magic.service.InventoryCorrectionService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class InventoryCorrectionPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(InventoryCorrectionPanel.class);
	
	@Autowired private InventoryCorrectionService inventoryCorrectionService;
	@Autowired private ProductService productService;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private InventoryCorrection inventoryCorrection;
	private MagicTextField productCodeField;
	private SelectProductEllipsisButton selectProductButton;
	private JLabel productDescriptionLabel;
	private UnitComboBox unitComboBox;
	private JLabel currentQuantityLabel;
	private MagicTextField newQuantityField;
	private JLabel discrepancyLabel;
	private MagicTextField remarksField;
	private JLabel postDateLabel;
	private MagicButton saveButton;
	
	@Override
	protected void initializeComponents() {
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		productDescriptionLabel = new JLabel();
		
		selectProductButton = new SelectProductEllipsisButton(selectProductDialog, productCodeField, productDescriptionLabel);
		
		unitComboBox = new UnitComboBox();
		
		currentQuantityLabel = new JLabel();
		
		newQuantityField = new MagicTextField();
		newQuantityField.setNumbersOnly(true);
		
		discrepancyLabel = new JLabel();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		
		postDateLabel = new JLabel();
		
		saveButton = new MagicButton("Save");
		saveButton.addActionListener(e -> saveInventoryCorrection());
		
		focusOnComponentWhenThisPanelIsDisplayed(productCodeField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(productCodeField);
		focusOrder.add(unitComboBox);
		focusOrder.add(newQuantityField);
		focusOrder.add(saveButton);
	}
	
	private void saveInventoryCorrection() {
		if (!validateFields()) {
			return;
		}
		
		if (confirm("WARNING! Saving will automatically post this inventory correction. Proceed?")) {
			Product product = productService.findProductByCode(productCodeField.getText());
			String unit = (String)unitComboBox.getSelectedItem();
			inventoryCorrection.setProduct(product);
			inventoryCorrection.setUnit(unit);
			inventoryCorrection.setNewQuantity(Integer.parseInt(newQuantityField.getText()));
			inventoryCorrection.setOldQuantity(product.getUnitQuantity(unit));
			inventoryCorrection.setRemarks(remarksField.getText());
			try {
				inventoryCorrectionService.save(inventoryCorrection);
				showMessage("Saved!");
				getMagicFrame().switchToInventoryCorrectionListPanel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateFields() {
		if (isProductCodeNotSpecified()) {
			showErrorMessage("Product Code must be specified");
			productCodeField.requestFocusInWindow();
			return false;
		}
		
		if (isUnitNotSpecified()) {
			showErrorMessage("Unit must be specified");
			unitComboBox.requestFocusInWindow();
			return false;
		}
		
		if (isNewQuantityNotSpecified()) {
			showErrorMessage("New Quantity must be specified");
			newQuantityField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	private boolean isProductCodeNotSpecified() {
		return productCodeField.getText().isEmpty();
	}

	private boolean isUnitNotSpecified() {
		return unitComboBox.getSelectedItem() == null;
	}

	private boolean isNewQuantityNotSpecified() {
		return newQuantityField.getText().isEmpty();
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Product Code:"));
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(selectProductButton.getFieldsPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		unitComboBox.setPreferredSize(new Dimension(60, 25));
		panel.add(unitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Current Quantity: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		currentQuantityLabel.setPreferredSize(new Dimension(100, 25));
		panel.add(currentQuantityLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "New Quantity: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		newQuantityField.setPreferredSize(new Dimension(100, 25));
		panel.add(newQuantityField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Discrepancy: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		discrepancyLabel.setPreferredSize(new Dimension(100, 25));
		panel.add(discrepancyLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Remarks: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		panel.add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Post Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateLabel.setPreferredSize(new Dimension(100, 25));
		panel.add(postDateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.insets.top = 10;
		saveButton.setPreferredSize(new Dimension(100, 25));
		panel.add(saveButton, c);
		
		mainPanel.add(panel, BorderLayout.NORTH);
	}

	@Override
	protected void registerKeyBindings() {
		selectProductButton.addOnSelectProductAction(product -> updateCurrentQuantityLabel(product));
		unitComboBox.addOnSelectListener(e -> updateCurrentQuantityLabel());
		saveButton.onEnterKey(() -> saveInventoryCorrection());
	}

	private void updateCurrentQuantityLabel(Product product) {
		if (unitComboBox.getSelectedItem() != null) {
			String unit = (String)unitComboBox.getSelectedItem();
			currentQuantityLabel.setText(String.valueOf(product.getUnitQuantity(unit)));
		}
	}

	private void updateCurrentQuantityLabel() {
		if (unitComboBox.getSelectedItem() != null && productCodeField.getText().isEmpty()) {
			Product product = productService.findProductByCode(productCodeField.getText());
			if (product != null) {
				String unit = (String)unitComboBox.getSelectedItem();
				currentQuantityLabel.setText(String.valueOf(product.getUnitQuantity(unit)));
			} else {
				currentQuantityLabel.setText(null);
			}
		}
	}

	public void updateDisplay(InventoryCorrection inventoryCorrection) {
		this.inventoryCorrection = inventoryCorrection;
		if (inventoryCorrection.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.inventoryCorrection = inventoryCorrection =
				inventoryCorrectionService.getInventoryCorrection(inventoryCorrection.getId());
		productCodeField.setText(inventoryCorrection.getProduct().getCode());
		productDescriptionLabel.setText(inventoryCorrection.getProduct().getDescription());
		unitComboBox.setSelectedItem(inventoryCorrection.getUnit());
		remarksField.setText(inventoryCorrection.getRemarks());
		postDateLabel.setText(FormatterUtil.formatDate(inventoryCorrection.getPostDate()));
		saveButton.setEnabled(false);
	}

	private void clearDisplay() {
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		unitComboBox.setSelectedItem(null);
		remarksField.setText(null);
		postDateLabel.setText(null);
		saveButton.setEnabled(true);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryCorrectionListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

}