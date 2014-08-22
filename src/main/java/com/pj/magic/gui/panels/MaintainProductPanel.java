package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.ProductSuppliersTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.ProductCategoryService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainProductPanel extends AbstractMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainProductPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private ProductService productService;
	@Autowired private ManufacturerService manufacturerService;
	@Autowired private ProductCategoryService categoryService;
	@Autowired private ProductSuppliersTable productSuppliersTable;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private Product product;
	private MagicTextField codeField;
	private MagicTextField descriptionField;
	private MagicTextField maximumStockLevelField;
	private MagicTextField minimumStockLevelField;
	private JCheckBox activeIndicatorCheckBox;
	private JCheckBox caseUnitIndicatorCheckBox;
	private JCheckBox tieUnitIndicatorCheckBox;
	private JCheckBox cartonUnitIndicatorCheckBox;
	private JCheckBox dozenUnitIndicatorCheckBox;
	private JCheckBox piecesUnitIndicatorCheckBox;
	private MagicTextField caseQuantityField;
	private MagicTextField tieQuantityField;
	private MagicTextField cartonQuantityField;
	private MagicTextField dozenQuantityField;
	private MagicTextField piecesQuantityField;
	private JComboBox<Manufacturer> manufacturerComboBox;
	private JComboBox<ProductCategory> categoryComboBox;
	private JButton saveButton;
	private JButton addSupplierButton;
	
	@Override
	protected void initializeComponents() {
		codeField = new MagicTextField();
		codeField.setMaximumLength(9);
		
		descriptionField = new MagicTextField();
		descriptionField.setMaximumLength(50);
		
		maximumStockLevelField = new MagicTextField();
		maximumStockLevelField.setMaximumLength(4);
		maximumStockLevelField.setNumbersOnly(true);
		
		minimumStockLevelField = new MagicTextField();
		minimumStockLevelField.setMaximumLength(4);
		minimumStockLevelField.setNumbersOnly(true);

		activeIndicatorCheckBox = new JCheckBox("Yes");
		
		caseUnitIndicatorCheckBox = new JCheckBox("Case");
		tieUnitIndicatorCheckBox = new JCheckBox("Tie");
		cartonUnitIndicatorCheckBox = new JCheckBox("Carton");
		dozenUnitIndicatorCheckBox = new JCheckBox("Dozen");
		piecesUnitIndicatorCheckBox = new JCheckBox("Pieces");
		
		caseQuantityField = new MagicTextField();
		caseQuantityField.setMaximumLength(6);
		caseQuantityField.setNumbersOnly(true);
		
		tieQuantityField = new MagicTextField();
		tieQuantityField.setMaximumLength(6);
		tieQuantityField.setNumbersOnly(true);
		
		cartonQuantityField = new MagicTextField();
		cartonQuantityField.setMaximumLength(6);
		cartonQuantityField.setNumbersOnly(true);
		
		dozenQuantityField = new MagicTextField();
		dozenQuantityField.setMaximumLength(6);
		dozenQuantityField.setNumbersOnly(true);
		
		piecesQuantityField = new MagicTextField();
		piecesQuantityField.setMaximumLength(6);
		piecesQuantityField.setNumbersOnly(true);
		
		manufacturerComboBox = new JComboBox<>();
		categoryComboBox = new JComboBox<>();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProduct();
			}
		});
		
		addSupplierButton = new JButton("Add Supplier");
		addSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addProductSupplier();
			}
		});
		
		productSuppliersTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (productSuppliersTable.getSelectedColumn() == ProductSuppliersTable.BUTTON_COLUMN_INDEX) {
					deleteProductSupplier();
				}
			}
			
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
	}

	protected void deleteProductSupplier() {
		int confirm = showConfirmMessage("Delete?");
		if (confirm == JOptionPane.OK_OPTION) {
			int selectedRow = productSuppliersTable.getSelectedRow();
			Supplier supplier = productSuppliersTable.getSupplier(selectedRow);
			productService.deleteProductSupplier(product, supplier);
			productSuppliersTable.updateDisplay(product);
		}
	}

	protected void addProductSupplier() {
		selectSupplierDialog.searchAvailableSuppliers(product);
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			productService.addProductSupplier(product, supplier);
			productSuppliersTable.updateDisplay(product);
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(codeField);
		focusOrder.add(descriptionField);
		focusOrder.add(categoryComboBox);
		focusOrder.add(maximumStockLevelField);
		focusOrder.add(minimumStockLevelField);
		focusOrder.add(activeIndicatorCheckBox);
		focusOrder.add(manufacturerComboBox);
		focusOrder.add(caseUnitIndicatorCheckBox);
		focusOrder.add(caseQuantityField);
		focusOrder.add(tieUnitIndicatorCheckBox);
		focusOrder.add(tieQuantityField);
		focusOrder.add(cartonUnitIndicatorCheckBox);
		focusOrder.add(cartonQuantityField);
		focusOrder.add(dozenUnitIndicatorCheckBox);
		focusOrder.add(dozenQuantityField);
		focusOrder.add(piecesUnitIndicatorCheckBox);
		focusOrder.add(piecesQuantityField);
		focusOrder.add(saveButton);
	}
	
	protected void saveProduct() {
		if (!validateProduct()) {
			return;
		}
		
		int confirm = showConfirmMessage("Save?");
		if (confirm == JOptionPane.OK_OPTION) {
			product.setCode(codeField.getText());
			product.setDescription(descriptionField.getText());
			product.setMaximumStockLevel(Integer.parseInt(maximumStockLevelField.getText()));
			product.setMinimumStockLevel(Integer.parseInt(minimumStockLevelField.getText()));
			product.setActive(activeIndicatorCheckBox.isSelected());
			
			product.getUnits().clear();
			product.getUnitQuantities().clear();
			if (caseUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.CASE);
				product.addUnitQuantity(Unit.CASE, Integer.parseInt(caseQuantityField.getText()));
			}
			if (tieUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.TIE);
				product.addUnitQuantity(Unit.TIE, Integer.parseInt(tieQuantityField.getText()));
			}
			if (cartonUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.CARTON);
				product.addUnitQuantity(Unit.CARTON, Integer.parseInt(cartonQuantityField.getText()));
			}
			if (dozenUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.DOZEN);
				product.addUnitQuantity(Unit.DOZEN, Integer.parseInt(dozenQuantityField.getText()));
			}
			if (piecesUnitIndicatorCheckBox.isSelected()) {
				product.getUnits().add(Unit.PIECES);
				product.addUnitQuantity(Unit.PIECES, Integer.parseInt(piecesQuantityField.getText()));
			}
			
			product.setCategory((ProductCategory)categoryComboBox.getSelectedItem());
			product.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
			
			try {
				productService.save(product);
				showMessage("Saved!");
				updateDisplay(product);
				codeField.requestFocusInWindow();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
			
		}
	}

	private boolean validateProduct() {
		try {
			validateMandatoryField(codeField, "Code");
			validateMandatoryField(descriptionField, "Description");
			validateMandatoryField(maximumStockLevelField, "Maximum Stock Level");
			validateMandatoryField(minimumStockLevelField, "Minimum Stock Level");
			validateStockLevel();
			if (caseUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(caseQuantityField, "Available Quantity (Case)");
			}
			if (tieUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(tieQuantityField, "Available Quantity (Tie)");
			}
			if (cartonUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(cartonQuantityField, "Available Quantity (Carton)");
			}
			if (dozenUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(dozenQuantityField, "Available Quantity (Dozen)");
			}
			if (piecesUnitIndicatorCheckBox.isSelected()) {
				validateMandatoryField(piecesQuantityField, "Available Quantity (Pieces)");
			}
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private void validateStockLevel() throws ValidationException {
		int maximumStockLevel = Integer.parseInt(maximumStockLevelField.getText());
		int minimumStockLevel = Integer.parseInt(minimumStockLevelField.getText());
		if (maximumStockLevel < minimumStockLevel) {
			showErrorMessage("Maximum stock level must be greater than or equal to minimum stock level");
			maximumStockLevelField.requestFocusInWindow();
			throw new ValidationException();
		}
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Code: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeField.setPreferredSize(new Dimension(100, 20));
		add(codeField, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createFiller(50, 20), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Suppliers:"), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 1), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createLabel(100, "Description: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		descriptionField.setPreferredSize(new Dimension(300, 20));
		add(descriptionField, c);

		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.gridheight = 4;
		c.anchor = GridBagConstraints.NORTHWEST;
		JScrollPane scrollPane = new JScrollPane(productSuppliersTable);
		scrollPane.setPreferredSize(new Dimension(350, 110));
		add(scrollPane, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.gridheight = 1;
		add(ComponentUtil.createLabel(100, "Category: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		categoryComboBox.setPreferredSize(new Dimension(300, 20));
		add(categoryComboBox, c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createLabel(150, "Maximum Stock Level: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		maximumStockLevelField.setPreferredSize(new Dimension(50, 20));
		add(maximumStockLevelField, c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createLabel(150, "Minimum Stock Level: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		minimumStockLevelField.setPreferredSize(new Dimension(50, 20));
		add(minimumStockLevelField, c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createLabel(150, "Active? "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(activeIndicatorCheckBox, c);

		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		add(addSupplierButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createLabel(150, "Manufacturer:"), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(300, 20));
		add(manufacturerComboBox, c);

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		add(new JSeparator(), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		add(ComponentUtil.createLabel(100, "Units: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		add(createUnitsPanel(), c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 20));
		add(saveButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 1), c);
	}

	private java.awt.Component createUnitsPanel() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 160));
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(100, 20), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Available Qty"), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(caseUnitIndicatorCheckBox, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		caseQuantityField.setPreferredSize(new Dimension(50, 20));
		panel.add(caseQuantityField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(tieUnitIndicatorCheckBox, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		tieQuantityField.setPreferredSize(new Dimension(50, 20));
		panel.add(tieQuantityField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(cartonUnitIndicatorCheckBox, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		cartonQuantityField.setPreferredSize(new Dimension(50, 20));
		panel.add(cartonQuantityField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(dozenUnitIndicatorCheckBox, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		dozenQuantityField.setPreferredSize(new Dimension(50, 20));
		panel.add(dozenQuantityField, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(piecesUnitIndicatorCheckBox, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		piecesQuantityField.setPreferredSize(new Dimension(50, 20));
		panel.add(piecesQuantityField, c);
		
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		manufacturerComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		manufacturerComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		categoryComboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		categoryComboBox.getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProduct();
			}
		});
	}

	public void updateDisplay(Product product) {
		updateComboBoxes();
		
		this.product = product;
		if (!product.isValid()) {
			clearDisplay();
			return;
		}
		
		codeField.setText(product.getCode());
		descriptionField.setText(product.getDescription());
		maximumStockLevelField.setText(String.valueOf(product.getMaximumStockLevel()));
		minimumStockLevelField.setText(String.valueOf(product.getMinimumStockLevel()));
		activeIndicatorCheckBox.setSelected(product.isActive());
		caseUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CASE));
		if (caseUnitIndicatorCheckBox.isSelected()) {
			caseQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.CASE)));
		} else {
			caseQuantityField.setText(null);
		}
		tieUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.TIE));
		if (tieUnitIndicatorCheckBox.isSelected()) {
			tieQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.TIE)));
		} else {
			tieQuantityField.setText(null);
		}
		cartonUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CARTON));
		if (cartonUnitIndicatorCheckBox.isSelected()) {
			cartonQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.CARTON)));
		} else {
			cartonQuantityField.setText(null);
		}
		dozenUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.DOZEN));
		if (dozenUnitIndicatorCheckBox.isSelected()) {
			dozenQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.DOZEN)));
		} else {
			dozenQuantityField.setText(null);
		}
		piecesUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.PIECES));
		if (piecesUnitIndicatorCheckBox.isSelected()) {
			piecesQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.PIECES)));
		} else {
			piecesQuantityField.setText(null);
		}
		
		if (product.getManufacturer() != null) {
			manufacturerComboBox.setSelectedItem(product.getManufacturer());
		} else {
			manufacturerComboBox.setSelectedItem(null);
		}
		
		if (product.getCategory() != null) {
			categoryComboBox.setSelectedItem(product.getCategory());
		} else {
			categoryComboBox.setSelectedItem(null);
		}
		
		productSuppliersTable.updateDisplay(product);
		addSupplierButton.setEnabled(true);
	}

	private void updateComboBoxes() {
		List<ProductCategory> categories = categoryService.getAllProductCategories();
		categoryComboBox.setModel(
				new DefaultComboBoxModel<>(categories.toArray(new ProductCategory[categories.size()])));
		categoryComboBox.insertItemAt(null, 0);
		
		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		manufacturerComboBox.setModel(
				new DefaultComboBoxModel<>(manufacturers.toArray(new Manufacturer[manufacturers.size()])));
		manufacturerComboBox.insertItemAt(null, 0);
	}

	private void clearDisplay() {
		codeField.setText(null);
		descriptionField.setText(null);
		maximumStockLevelField.setText(null);
		minimumStockLevelField.setText(null);
		activeIndicatorCheckBox.setSelected(true);
		caseUnitIndicatorCheckBox.setSelected(false);
		caseQuantityField.setText(null);
		tieUnitIndicatorCheckBox.setSelected(false);
		tieQuantityField.setText(null);
		cartonUnitIndicatorCheckBox.setSelected(false);
		cartonQuantityField.setText(null);
		dozenUnitIndicatorCheckBox.setSelected(false);
		dozenQuantityField.setText(null);
		piecesUnitIndicatorCheckBox.setSelected(false);
		piecesQuantityField.setText(null);
		manufacturerComboBox.setSelectedItem(null);
		categoryComboBox.setSelectedItem(null);
		addSupplierButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToProductListPanel();
	}

}
