package com.pj.magic.gui.panels.promo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.PromoType2RulesTable;
import com.pj.magic.gui.tables.PromoType3RulePromoProductsTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.PromoType1Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.PricingSchemeService;
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
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private PromoType2RulesTable promoType2RulesTable;
	@Autowired private PromoType3RulePromoProductsTable promoType3RulePromoProductsTable;
	
	private Promo promo;
	private JLabel promoNumberLabel;
	private MagicTextField nameField;
	private JComboBox<PromoType> promoTypeComboBox;
	private UtilCalendarModel startDateModel;
	private JCheckBox activeCheckBox;
	private JPanel promoDetailsPanel;
	private JComboBox<Manufacturer> manufacturerComboBox;
	private MagicTextField targetAmountField;
	private MagicTextField freeProductCodeField;
	private JLabel freeProductDescriptionLabel;
	private EllipsisButton selectProductButton;
	private JComboBox<String> freeUnitComboBox;
	private MagicTextField freeQuantityField;
	private JComboBox<PricingScheme> pricingSchemeComboBox;
	private MagicButton saveButton;
	private JButton addRuleButton;
	private JButton removeRuleButton;
	private JButton addPromoProductButton;
	private JButton removePromoProductButton;
	private JButton addAllPromoProductButton;
	private JButton removeAllPromoProductButton;
	
	@Override
	protected void initializeComponents() {
		promoNumberLabel = new JLabel();
		
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		promoTypeComboBox = new JComboBox<>();
		promoTypeComboBox.setModel(ListUtil.toDefaultComboBoxModel(PromoType.getPromoTypes(), true));
		
		startDateModel = new UtilCalendarModel();
		
		activeCheckBox = new JCheckBox();
		
		manufacturerComboBox = new JComboBox<>();
		
		targetAmountField = new MagicTextField();
		
		freeProductCodeField = new MagicTextField();
		freeProductCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		freeProductDescriptionLabel = new JLabel();
		
		selectProductButton = new EllipsisButton("Select Product (F5)");
		selectProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialog();
			}
		});
		
		freeUnitComboBox = new JComboBox<>();
		freeUnitComboBox.setModel(ListUtil.toDefaultComboBoxModel(
				Arrays.asList(Unit.CASE, Unit.TIE, Unit.CARTON, Unit.DOZEN, Unit.PIECES)));
		
		freeQuantityField = new MagicTextField();
		freeQuantityField.setNumbersOnly(true);
		
		pricingSchemeComboBox = new JComboBox<>();
		
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
		selectProductDialog.searchProducts(freeProductCodeField.getText());
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			freeProductCodeField.setText(product.getCode());
			freeProductDescriptionLabel.setText(product.getDescription());
		}
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(promoTypeComboBox);
		focusOrder.add(activeCheckBox);
		focusOrder.add(manufacturerComboBox);
		focusOrder.add(targetAmountField);
		focusOrder.add(freeProductCodeField);
		focusOrder.add(freeUnitComboBox);
		focusOrder.add(freeQuantityField);
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
			case 3:
				savePromoType3();
				break;
			}
		}
	}

	private void savePromoType3() {
		if (!validatePromoType3()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			setCommonFieldsForSaving();
			if (promo.getPromoType3Rule() == null) {
				promo.setPromoType3Rule(new PromoType3Rule());
			}
			PromoType3Rule rule = promo.getPromoType3Rule();
			rule.setParent(promo);
			rule.setTargetAmount(NumberUtil.toBigDecimal(targetAmountField.getText()));
			rule.setFreeProduct(productService.findProductByCode(freeProductCodeField.getText()));
			rule.setFreeUnit((String)freeUnitComboBox.getSelectedItem());
			rule.setFreeQuantity(Integer.valueOf(freeQuantityField.getText()));
			if (pricingSchemeComboBox.getSelectedIndex() == 0) {
				rule.setPricingScheme(null);
			} else {
				rule.setPricingScheme((PricingScheme)pricingSchemeComboBox.getSelectedItem());
			}
			
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

	private boolean validatePromoType3() {
		try {
			validateMandatoryField(nameField, "Name");
			validateMandatoryField(targetAmountField, "Target Amount");
			validateMandatoryField(freeProductCodeField, "Product Code");
			validateMandatoryField(freeUnitComboBox, "Unit");
			validateMandatoryField(freeQuantityField, "Quantity");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be a valid amount");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		if (productService.findProductByCode(freeProductCodeField.getText()) == null) {
			showErrorMessage("No product matching code specified");
			freeProductCodeField.requestFocusInWindow();
			return false;
		}
		
		return true;
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
		promo.setStartDate(startDateModel.getValue().getTime());
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
			if (promo.getPromoType1Rule() == null) {
				promo.setPromoType1Rule(new PromoType1Rule());
			}
			PromoType1Rule rule = promo.getPromoType1Rule();
			rule.setParent(promo);
			rule.setTargetAmount(NumberUtil.toBigDecimal(targetAmountField.getText()));
			rule.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
			rule.setProduct(productService.findProductByCode(freeProductCodeField.getText()));
			rule.setUnit((String)freeUnitComboBox.getSelectedItem());
			rule.setQuantity(Integer.valueOf(freeQuantityField.getText()));
			
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
			validateMandatoryField(freeProductCodeField, "Product Code");
			validateMandatoryField(freeUnitComboBox, "Unit");
			validateMandatoryField(freeQuantityField, "Quantity");
		} catch (ValidationException e) {
			return false;
		}
		
		if (!NumberUtil.isAmount(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be a valid amount");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		if (productService.findProductByCode(freeProductCodeField.getText()) == null) {
			showErrorMessage("No product matching code specified");
			freeProductCodeField.requestFocusInWindow();
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
		promoTypeComboBox.setPreferredSize(new Dimension(500, 25));
		mainPanel.add(promoTypeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(new JLabel("Start Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(startDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Active?"), c);
		
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
		
		promoDetailsPanel = new JPanel(new GridBagLayout());
		mainPanel.add(promoDetailsPanel, c);
		
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

	private void updateToPromoType2Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createPromoType2RulesTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(promoType2RulesTable);
		scrollPane.setPreferredSize(new Dimension(900, 200));
		panel.add(scrollPane, c);
	}

	private void updateToPromoType3Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		panel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Free Product: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Free Unit:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeUnitComboBox.setPreferredSize(new Dimension(60, 25));
		panel.add(freeUnitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Free Quantity:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeQuantityField.setPreferredSize(new Dimension(100, 25));
		panel.add(freeQuantityField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Pricing Scheme:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
//		pricingSchemeComboBox.setPreferredSize(new Dimension(100, 25));
		pricingSchemeComboBox.getPreferredSize().height = 25;
		panel.add(pricingSchemeComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		panel.add(createPromoType3RulesTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		
		JScrollPane scrollPane = new JScrollPane(promoType3RulePromoProductsTable);
		scrollPane.setPreferredSize(new Dimension(600, 100));
		panel.add(scrollPane, c);
	}

	private JPanel createPromoType3RulesTableToolBar() {
		JPanel panel = new JPanel();
		
		addPromoProductButton = new MagicToolBarButton("plus_small", "Add Promo Product", true);
		addPromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addPromoProduct();
			}
		});
		panel.add(addPromoProductButton, BorderLayout.WEST);
		
		removePromoProductButton = new MagicToolBarButton("minus_small", "Remove Promo Product", true);
		removePromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removePromoProduct();
			}
		});
		panel.add(removePromoProductButton, BorderLayout.WEST);
		
		addAllPromoProductButton = new MagicToolBarButton("add_all_small", "Add All Promo Product", true);
		addAllPromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllPromoProduct();
			}
		});
		panel.add(addAllPromoProductButton, BorderLayout.WEST);
		
		removeAllPromoProductButton = new MagicToolBarButton("delete_all_small", "Remove All Promo Products", true);
		removeAllPromoProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAllPromoProduct();
			}
		});
		panel.add(removeAllPromoProductButton, BorderLayout.WEST);
		
		return panel;
	}

	private void removeAllPromoProduct() {
		if (confirm("Remove all promo products?")) {
			try {
				promoService.removeAllPromoProducts(promo.getPromoType3Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
			updateDisplay(promo);
		}
	}

	private void removePromoProduct() {
		promoType3RulePromoProductsTable.removeCurrentlySelectedPromoProduct();
	}

	private void addPromoProduct() {
		promoType3RulePromoProductsTable.addNewRow();
	}

	private void addAllPromoProduct() {
		if (confirm("Add all products to promo?")) {
			try {
				promoService.addAllPromoProducts(promo.getPromoType3Rule());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			updateDisplay(promo);
		}
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

	private void updateToPromoType1Panel(JPanel panel) {
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Manufacturer: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(300, 25));
		panel.add(manufacturerComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		panel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Product: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(60, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeUnitComboBox.setPreferredSize(new Dimension(60, 25));
		panel.add(freeUnitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Quantity: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		freeQuantityField.setPreferredSize(new Dimension(100, 25));
		panel.add(freeQuantityField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createGlue(), c);
	}

	private JPanel createProductPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		freeProductCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(freeProductCodeField, c);
		
		panel.add(selectProductButton, c);
		panel.add(Box.createHorizontalStrut(10), c);
		
		freeProductDescriptionLabel.setPreferredSize(new Dimension(400, 20));
		panel.add(freeProductDescriptionLabel, c);
		
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
		
		freeProductCodeField.onF5Key(new AbstractAction() {
			
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
		startDateModel.setValue(DateUtils.toCalendar(promo.getStartDate()));
		activeCheckBox.setSelected(promo.isActive());
		
		updatePromoDetailsPanel();
	}

	private void updatePromoDetailsPanel() {
		promoDetailsPanel.removeAll();
		switch (promo.getPromoType().getId().intValue()) {
		case 1:
			updateDisplayForPromoType1();
			break;
		case 2:
			updateDisplayForPromoType2();
			break;
		case 3:
			updateDisplayForPromoType3();
			break;
		}
		promoDetailsPanel.revalidate();
		promoDetailsPanel.repaint();
		promoDetailsPanel.setVisible(true);
	}

	private void updateDisplayForPromoType2() {
		updateToPromoType2Panel(promoDetailsPanel);
		promoType2RulesTable.setPromo(promo);
	}

	private void updateDisplayForPromoType3() {
		updatePricingSchemeComboBox();
		updateToPromoType3Panel(promoDetailsPanel);
		
		PromoType3Rule rule = promo.getPromoType3Rule();
		if (rule != null) {
			targetAmountField.setText(FormatterUtil.formatAmount(rule.getTargetAmount()));
			freeProductCodeField.setText(rule.getFreeProduct().getCode());
			freeProductDescriptionLabel.setText(rule.getFreeProduct().getDescription());
			freeUnitComboBox.setSelectedItem(rule.getFreeUnit());
			freeQuantityField.setText(rule.getFreeQuantity().toString());
			if (rule.getPricingScheme() != null) {
				pricingSchemeComboBox.setSelectedItem(rule.getPricingScheme());
			} else {
				pricingSchemeComboBox.setSelectedIndex(0);
			}
			promoType3RulePromoProductsTable.setRule(rule);
			addPromoProductButton.setEnabled(true);
			removePromoProductButton.setEnabled(true);
			addAllPromoProductButton.setEnabled(true);
			removePromoProductButton.setEnabled(true);
		} else {
			targetAmountField.setText(null);
			freeProductCodeField.setText(null);
			freeProductDescriptionLabel.setText(null);
			freeUnitComboBox.setSelectedItem(null);
			freeQuantityField.setText(null);
			pricingSchemeComboBox.setSelectedIndex(0);
			promoType3RulePromoProductsTable.clear();
			addPromoProductButton.setEnabled(false);
			removePromoProductButton.setEnabled(false);
			addAllPromoProductButton.setEnabled(false);
			removeAllPromoProductButton.setEnabled(false);
		}
	}

	private void updatePricingSchemeComboBox() {
		DefaultComboBoxModel<PricingScheme> model = ListUtil.toDefaultComboBoxModel(pricingSchemeService.getAllPricingSchemes());
		model.insertElementAt(new PricingScheme() {
			
			@Override
			public String toString() {
				return "ANY";
			}
			
		}, 0);
		pricingSchemeComboBox.setModel(model);
	}

	private void updateDisplayForPromoType1() {
		List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
		manufacturerComboBox.setModel(ListUtil.toDefaultComboBoxModel(manufacturers, true));
		
		updateToPromoType1Panel(promoDetailsPanel);
		
		PromoType1Rule rule = promo.getPromoType1Rule();
		if (rule != null) {
			manufacturerComboBox.setSelectedItem(rule.getManufacturer());
			targetAmountField.setText(FormatterUtil.formatAmount(rule.getTargetAmount()));
			freeProductCodeField.setText(rule.getProduct().getCode());
			freeProductDescriptionLabel.setText(rule.getProduct().getDescription());
			freeUnitComboBox.setSelectedItem(rule.getUnit());
			freeQuantityField.setText(rule.getQuantity().toString());
		} else {
			manufacturerComboBox.setSelectedIndex(0);
			targetAmountField.setText(null);
			freeProductCodeField.setText(null);
			freeProductDescriptionLabel.setText(null);
			freeUnitComboBox.setSelectedItem(null);
			freeQuantityField.setText(null);
		}
	}

	private void clearDisplay() {
		promoNumberLabel.setText(null);
		nameField.setText(null);
		promoTypeComboBox.setEnabled(true);
		promoTypeComboBox.setSelectedIndex(0);
		startDateModel.setValue(Calendar.getInstance());
		activeCheckBox.setSelected(true);
		
		promoDetailsPanel.setVisible(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

}