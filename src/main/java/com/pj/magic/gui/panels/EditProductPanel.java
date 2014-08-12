package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

@Component
public class EditProductPanel extends AbstractMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(EditProductPanel.class);
	private static final String BACK_ACTION_NAME = "back";
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private ProductService productService;
	
	private Product product;
	private MagicTextField codeField;
	private MagicTextField descriptionField;
	private MagicTextField maximumStockLevelField;
	private MagicTextField minimumStockLevelField;
	private JCheckBox activeIndicatorCheckBox;
	private JCheckBox caseUnitIndicatorCheckBox;
	private JCheckBox cartonUnitIndicatorCheckBox;
	private JCheckBox dozenUnitIndicatorCheckBox;
	private JCheckBox piecesUnitIndicatorCheckBox;
	private MagicTextField caseQuantityField;
	private MagicTextField cartonQuantityField;
	private MagicTextField dozenQuantityField;
	private MagicTextField piecesQuantityField;
	private JButton saveButton;
	private List<JComponent> focusOrder = new ArrayList<>();
	
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
		cartonUnitIndicatorCheckBox = new JCheckBox("Carton");
		dozenUnitIndicatorCheckBox = new JCheckBox("Dozen");
		piecesUnitIndicatorCheckBox = new JCheckBox("Pieces");
		
		caseQuantityField = new MagicTextField();
		caseQuantityField.setMaximumLength(6);
		caseQuantityField.setNumbersOnly(true);
		
		cartonQuantityField = new MagicTextField();
		cartonQuantityField.setMaximumLength(6);
		cartonQuantityField.setNumbersOnly(true);
		
		dozenQuantityField = new MagicTextField();
		dozenQuantityField.setMaximumLength(6);
		dozenQuantityField.setNumbersOnly(true);
		
		piecesQuantityField = new MagicTextField();
		piecesQuantityField.setMaximumLength(6);
		piecesQuantityField.setNumbersOnly(true);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProduct();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeField);
		initializeFocusOrder();
	}

	private void initializeFocusOrder() {
		focusOrder.add(codeField);
		focusOrder.add(descriptionField);
		focusOrder.add(maximumStockLevelField);
		focusOrder.add(minimumStockLevelField);
		focusOrder.add(activeIndicatorCheckBox);
		focusOrder.add(caseUnitIndicatorCheckBox);
		focusOrder.add(caseQuantityField);
		focusOrder.add(cartonUnitIndicatorCheckBox);
		focusOrder.add(cartonQuantityField);
		focusOrder.add(dozenUnitIndicatorCheckBox);
		focusOrder.add(dozenQuantityField);
		focusOrder.add(piecesUnitIndicatorCheckBox);
		focusOrder.add(piecesQuantityField);
		focusOrder.add(saveButton);
	}

	protected void saveProduct() {
		product.setCode(codeField.getText());
		product.setDescription(descriptionField.getText());
		product.setMaximumStockLevel(Integer.parseInt(maximumStockLevelField.getText()));
		product.setMinimumStockLevel(Integer.parseInt(minimumStockLevelField.getText()));
		product.setActive(activeIndicatorCheckBox.isSelected());
		try {
			int confirm = JOptionPane.showConfirmDialog(this, "Save changes?", "Message", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.OK_OPTION) {
				productService.save(product);
				JOptionPane.showMessageDialog(this, "Changes saved!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(this, "Error occurred during saving!", "Error Message",
					JOptionPane.ERROR_MESSAGE);
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
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.weighty = 0.0;
		c.gridx = 2;
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

		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		add(new JSeparator(), c);
		
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
		panel.setPreferredSize(new Dimension(300, 125));
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
		panel.add(ComponentUtil.createLabel(100, "Quantity"), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		caseUnitIndicatorCheckBox.setPreferredSize(new Dimension(100, 20));
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
		registerBackKeyBinding();
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
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

	protected void focusNextField() {
		java.awt.Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
		int focusOwnerIndex = focusOrder.indexOf(focusOwner);
		if (focusOwnerIndex != focusOrder.size() - 1) {
			focusOrder.get(focusOwnerIndex + 1).requestFocusInWindow();
		}
	}

	private void registerBackKeyBinding() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), BACK_ACTION_NAME);
		getActionMap().put(BACK_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getMagicFrame().switchToProductListPanel();
			}
		});
	}

	public void updateDisplay(Product product) {
		this.product = product;
		codeField.setText(product.getCode());
		descriptionField.setText(product.getDescription());
		maximumStockLevelField.setText(product.getMaximumStockLevel().toString());
		minimumStockLevelField.setText(product.getMinimumStockLevel().toString());
		activeIndicatorCheckBox.setSelected(product.isActive());
		caseUnitIndicatorCheckBox.setSelected(product.hasUnit(Unit.CASE));
		if (caseUnitIndicatorCheckBox.isSelected()) {
			caseQuantityField.setText(String.valueOf(product.getUnitQuantity(Unit.CASE)));
		} else {
			caseQuantityField.setText(null);
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
	}

}
