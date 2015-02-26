package com.pj.magic.gui.panels.promo;

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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
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
	
	private Promo promo;
	private JLabel promoNumberLabel;
	private MagicTextField nameField;
	private JComboBox<PromoType> promoTypeComboBox;
	private JComboBox<Manufacturer> manufacturerComboBox;
	private MagicTextField targetAmountField;
	private MagicTextField productCodeField;
	private JLabel productDescriptionLabel;
	private EllipsisButton selectProductButton;
	private JComboBox<String> unitComboBox;
	private MagicTextField quantityField;
	private MagicButton saveButton;
	
	@Override
	protected void initializeComponents() {
		promoNumberLabel = new JLabel();
		
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		promoTypeComboBox = new JComboBox<>();
		promoTypeComboBox.setModel(ListUtil.toDefaultComboBoxModel(PromoType.getPromoTypes()));
		
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
				Arrays.asList(Unit.PIECES, Unit.TIE, Unit.CARTON, Unit.DOZEN, Unit.PIECES)));
		
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
		focusOrder.add(manufacturerComboBox);
		focusOrder.add(targetAmountField);
		focusOrder.add(productCodeField);
		focusOrder.add(unitComboBox);
		focusOrder.add(quantityField);
		focusOrder.add(saveButton);
	}
	
	private void savePromo() {
		if (!validatePromo()) {
			return;
		}
		
		if (confirm("Save Promo?")) {
			promo.setName(nameField.getText());
			promo.setPromoType((PromoType)promoTypeComboBox.getSelectedItem());
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

	private boolean validatePromo() {
		try {
			validateMandatoryField(nameField, "Name");
			validateMandatoryField(promoTypeComboBox, "Promo Type");
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
		mainPanel.add(ComponentUtil.createLabel(150, "Manufacturer: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(manufacturerComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Product: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(60, "Unit: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		unitComboBox.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(unitComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Quantity: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		quantityField.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(quantityField, c);
		
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
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
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
		promoTypeComboBox.setSelectedItem(promo.getPromoType());
		manufacturerComboBox.setSelectedItem(promo.getManufacturer());
		targetAmountField.setText(FormatterUtil.formatAmount(promo.getTargetAmount()));
		productCodeField.setText(promo.getPrize().getProduct().getCode());
		productDescriptionLabel.setText(promo.getPrize().getProduct().getDescription());
		unitComboBox.setSelectedItem(promo.getPrize().getUnit());
		quantityField.setText(promo.getPrize().getQuantity().toString());
	}

	private void clearDisplay() {
		promoNumberLabel.setText(null);
		nameField.setText(null);
		manufacturerComboBox.setSelectedIndex(0);
		targetAmountField.setText(null);
		productCodeField.setText(null);
		productDescriptionLabel.setText(null);
		unitComboBox.setSelectedItem(null);
		quantityField.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

}