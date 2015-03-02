package com.pj.magic.gui.panels.promo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.PromoType2RulesTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoPrize;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.ListUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PromoPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PromoPanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	
	@Autowired private PromoService promoService;
	@Autowired private ProductService productService;
	@Autowired private ManufacturerService manufacturerService;
	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private PromoType2RulesTable promoType2RulesTable;
	
	private Promo promo;
	private JLabel promoNumberLabel;
	private MagicTextField nameField;
	private JComboBox<PromoType> promoTypeComboBox;
	private JCheckBox activeCheckBox;
	private JPanel promoType1Panel;
	private JPanel promoType2Panel;
	private JComboBox<Manufacturer> manufacturerComboBox;
	private MagicTextField targetAmountField;
	private MagicTextField productCodeField;
	private JLabel productDescriptionLabel;
	private EllipsisButton selectProductButton;
	private JComboBox<String> unitComboBox;
	private MagicTextField quantityField;
	private MagicButton saveButton;
	private JButton addRuleButton;
	private JButton removeRuleButton;
	
	@Override
	protected void initializeComponents() {
		promoNumberLabel = new JLabel();
		
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		promoTypeComboBox = new JComboBox<>();
		promoTypeComboBox.setModel(ListUtil.toDefaultComboBoxModel(PromoType.getPromoTypes(), true));
		
		activeCheckBox = new JCheckBox();
		
		manufacturerComboBox = new JComboBox<>();
		
		targetAmountField = new MagicTextField();
		
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		productDescriptionLabel = new JLabel();
		
		selectProductButton = new EllipsisButton("Select Product (F5)");
		selectProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		unitComboBox = new JComboBox<>();
		unitComboBox.setModel(ListUtil.toDefaultComboBoxModel(
				Arrays.asList(Unit.CASE, Unit.TIE, Unit.CARTON, Unit.DOZEN, Unit.PIECES)));
		
		quantityField = new MagicTextField();
		quantityField.setNumbersOnly(true);
		
		saveButton = new MagicButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePromo();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(nameField);
	}

	private void openSelectProductDialog() {
		selectProductDialog.searchProducts(productCodeField.getText());
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			productCodeField.setText(product.getCode());
			productDescriptionLabel.setText(product.getDescription());
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(promoTypeComboBox);
		focusOrder.add(activeCheckBox);
		focusOrder.add(manufacturerComboBox);
		focusOrder.add(targetAmountField);
		focusOrder.add(productCodeField);
		focusOrder.add(unitComboBox);
		focusOrder.add(quantityField);
		focusOrder.add(saveButton);
	}
	
	private void savePromo() {
		if (promo.getId() == null) {
			saveNewPromo();
		} else {
			switch (promo.getPromoType().getId().intValue()) {
			case 1:
				savePromoType1();
				break;
			case 2:
				savePromoType2();
				break;
			}
		}
	}

	private void savePromoType2() {
		if (!validatePromoType2()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}

	private void setCommonFieldsForSaving() {
		promo.setName(nameField.getText());
		promo.setActive(activeCheckBox.isSelected());
	}

	private boolean validatePromoType2() {
		try {
			validateMandatoryField(nameField, "Name");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private void saveNewPromo() {
		if (!validateNewPromo()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			promo.setPromoType((PromoType)promoTypeComboBox.getSelectedItem());
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}

	private void savePromoType1() {
		if (!validatePromoType1()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			promo.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
			promo.setTargetAmount(NumberUtil.toBigDecimal(targetAmountField.getText()));
			promo.setPrize(new PromoPrize());
			promo.getPrize().setProduct(
					productService.findProductByCode(productCodeField.getText()));
			promo.getPrize().setUnit((String)unitComboBox.getSelectedItem());
			promo.getPrize().setQuantity(Integer.valueOf(quantityField.getText()));
			
			try {
				promoService.save(promo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
				return;
			}
			
			showMessage("Saved!");
			updateDisplay(promo);
		}
	}
	
	private boolean validateNewPromo() {
		try {
			validateMandatoryField(nameField, "Name");
			validateMandatoryField(promoTypeComboBox, "Promo Type");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private boolean validatePromoType1() {
		try {
			validateMandatoryField(nameField, "Name");
			validateMandatoryField(manufacturerComboBox, "Manufacturer");
			validateMandatoryField(targetAmountField, "Target Amount");
			validateMandatoryField(productCodeField, "Product Code");
			validateMandatoryField(unitComboBox, "Unit");
			validateMandatoryField(quantityField, "Quantity");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be a valid amount");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		if (productService.findProductByCode(productCodeField.getText()) == null) {
			showErrorMessage("No product matching code specified");
			productCodeField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50));
			
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Promo No.: "), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoNumberLabel.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(promoNumberLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(nameField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Promo Type: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoTypeComboBox.setPreferredSize(new Dimension(400, 25));
		mainPanel.add(promoTypeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(new JLabel("Active?"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(activeCheckBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createPromoType1Panel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createPromoType2Panel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
	}

	private JPanel createPromoType2Panel() {
		promoType2Panel = new JPanel(new GridBagLayout());
		promoType2Panel.setVisible(false);
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		promoType2Panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoType2Panel.add(createPromoType2RulesTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(promoType2RulesTable);
		scrollPane.setPreferredSize(new Dimension(900, 200));
		promoType2Panel.add(scrollPane, c);
		
		return promoType2Panel;
	}

	private JPanel createPromoType2RulesTableToolBar() {
		JPanel panel = new JPanel();
		
		addRuleButton = new MagicToolBarButton("plus_small", "Add Rule", true);
		addRuleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addPromoType2Rule();
			}
		});
		panel.add(addRuleButton, BorderLayout.WEST);
		
		removeRuleButton = new MagicToolBarButton("minus_small", "Delete", true);
		removeRuleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removePromoType2Rule();
			}
		});
		panel.add(removeRuleButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removePromoType2Rule() {
		promoType2RulesTable.removeCurrentlySelectedRule();
	}

	private void addPromoType2Rule() {
		promoType2RulesTable.addNewRow();
	}

	private JPanel createPromoType1Panel() {
		promoType1Panel = new JPanel(new GridBagLayout());
		promoType1Panel.setVisible(false);
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		promoType1Panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoType1Panel.add(ComponentUtil.createLabel(150, "Manufacturer: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(300, 25));
		promoType1Panel.add(manufacturerComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoType1Panel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		promoType1Panel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoType1Panel.add(ComponentUtil.createLabel(100, "Product: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoType1Panel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoType1Panel.add(ComponentUtil.createLabel(60, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		unitComboBox.setPreferredSize(new Dimension(100, 25));
		promoType1Panel.add(unitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		promoType1Panel.add(ComponentUtil.createLabel(100, "Quantity: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		quantityField.setPreferredSize(new Dimension(100, 25));
		promoType1Panel.add(quantityField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		promoType1Panel.add(Box.createGlue(), c);
		
		return promoType1Panel;
	}

	private JPanel createProductPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		productCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(productCodeField, c);
		
		panel.add(selectProductButton, c);
		panel.add(Box.createHorizontalStrut(10), c);
		
		productDescriptionLabel.setPreferredSize(new Dimension(400, 20));
		panel.add(productDescriptionLabel, c);
		
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
		
		productCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		saveButton.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePromo();
			}
		});
	}

	public void updateDisplay(Promo promo) {
		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		manufacturerComboBox.setModel(ListUtil.toDefaultComboBoxModel(manufacturers, true));
		
		this.promo = promo;
		if (promo.getId() == null) {
			clearDisplay();
			return;
		}
		
		this.promo = promo = promoService.getPromo(promo.getId());
		
		promoNumberLabel.setText(String.valueOf(promo.getPromoNumber()));
		nameField.setText(promo.getName());
		promoTypeComboBox.setEnabled(false);
		promoTypeComboBox.setSelectedItem(promo.getPromoType());
		activeCheckBox.setSelected(promo.isActive());
		
		promoType1Panel.setVisible(promo.getPromoType().isType1());
		promoType2Panel.setVisible(promo.getPromoType().isType2());
		
		if (promo.getPromoType().isType1()) {
			manufacturerComboBox.setSelectedItem(promo.getManufacturer());
			if (promo.getPrize() != null) {
				targetAmountField.setText(FormatterUtil.formatAmount(promo.getTargetAmount()));
				productCodeField.setText(promo.getPrize().getProduct().getCode());
				productDescriptionLabel.setText(promo.getPrize().getProduct().getDescription());
				unitComboBox.setSelectedItem(promo.getPrize().getUnit());
				quantityField.setText(promo.getPrize().getQuantity().toString());
			} else {
				targetAmountField.setText(null);
				productCodeField.setText(null);
				productDescriptionLabel.setText(null);
				unitComboBox.setSelectedItem(null);
				quantityField.setText(null);
			}
		} else if (promo.getPromoType().isType2()) {
			promoType2RulesTable.setPromo(promo);
		}
	}

	private void clearDisplay() {
		promoNumberLabel.setText(null);
		nameField.setText(null);
		promoTypeComboBox.setEnabled(true);
		promoTypeComboBox.setSelectedIndex(0);
		activeCheckBox.setSelected(true);
		manufacturerComboBox.setSelectedIndex(0);
		targetAmountField.setText(null);
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		unitComboBox.setSelectedItem(null);
		quantityField.setText(null);
		
		promoType1Panel.setVisible(false);
		promoType2Panel.setVisible(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

}