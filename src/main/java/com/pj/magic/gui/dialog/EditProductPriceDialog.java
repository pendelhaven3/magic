package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

@Component
public class EditProductPriceDialog extends MagicDialog {

	private static final String SAVE_ACTION_COMMAND_NAME = "save";

	@Autowired private ProductService productService;

	private PricingScheme pricingScheme;
	private Product product;
	private JLabel productCodeLabel;
	private JLabel productDescriptionLabel;
	private JLabel pricingSchemeNameLabel;
	private MagicTextField caseUnitPriceField;
	private MagicTextField tieUnitPriceField;
	private MagicTextField cartonUnitPriceField;
	private MagicTextField dozenUnitPriceField;
	private MagicTextField piecesUnitPriceField;
	private JButton saveButton;
	
	public EditProductPriceDialog() {
		setSize(600, 500);
		setLocationRelativeTo(null);
		setTitle("Edit Product Price");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
	}

	private void initializeComponents() {
		productCodeLabel = new JLabel();
		productDescriptionLabel = new JLabel();
		pricingSchemeNameLabel = new JLabel();
		caseUnitPriceField = new MagicTextField();
		tieUnitPriceField = new MagicTextField();
		cartonUnitPriceField = new MagicTextField();
		dozenUnitPriceField = new MagicTextField();
		piecesUnitPriceField = new MagicTextField();
		
		saveButton = new JButton("Save");
		saveButton.setActionCommand(SAVE_ACTION_COMMAND_NAME);
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveUnitPrices();
			}
		});
	}

	private void saveUnitPrices() {
		if (product.hasUnit(Unit.CASE)) {
			product.setUnitPrice(Unit.CASE, new BigDecimal(caseUnitPriceField.getText()));
		}
		if (product.hasUnit(Unit.TIE)) {
			product.setUnitPrice(Unit.TIE, new BigDecimal(tieUnitPriceField.getText()));
		}
		if (product.hasUnit(Unit.CARTON)) {
			product.setUnitPrice(Unit.CARTON, new BigDecimal(cartonUnitPriceField.getText()));
		}
		if (product.hasUnit(Unit.DOZEN)) {
			product.setUnitPrice(Unit.DOZEN, new BigDecimal(dozenUnitPriceField.getText()));
		}
		if (product.hasUnit(Unit.PIECES)) {
			product.setUnitPrice(Unit.PIECES, new BigDecimal(piecesUnitPriceField.getText()));
		}
		
		productService.saveUnitPrices(product, pricingScheme);
		JOptionPane.showMessageDialog(this, "Saved!");
	}

	private void registerKeyBindings() {
//		productsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
//		productsTable.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new AbstractAction() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				selectProduct();
//			}
//		});
//		
//		productsTable.addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if (e.getClickCount() == 2) {
//					selectProduct();
//				}
//			}
//		});
//		
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Product Code:"), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productCodeLabel.setPreferredSize(new Dimension(100, 20));
		add(productCodeLabel, c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 1), c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Product Description:"), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productDescriptionLabel.setPreferredSize(new Dimension(250, 20));
		add(productDescriptionLabel, c);

		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Pricing Scheme:"), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeNameLabel.setPreferredSize(new Dimension(100, 20));
		add(pricingSchemeNameLabel, c);

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
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		add(new JSeparator(), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(10, 10), c);
		
		if (product.hasUnit(Unit.CASE)) {
			currentRow++;
			
			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createRightLabel(100, Unit.CASE), c);

			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			caseUnitPriceField.setPreferredSize(new Dimension(80, 20));
			add(caseUnitPriceField, c);
		}
		
		if (product.hasUnit(Unit.TIE)) {
			currentRow++;
			
			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createRightLabel(100, Unit.TIE), c);

			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			tieUnitPriceField.setPreferredSize(new Dimension(80, 20));
			add(tieUnitPriceField, c);
		}
		
		if (product.hasUnit(Unit.CARTON)) {
			currentRow++;
			
			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createRightLabel(100, Unit.CARTON), c);

			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			cartonUnitPriceField.setPreferredSize(new Dimension(80, 20));
			add(cartonUnitPriceField, c);
		}
		
		if (product.hasUnit(Unit.DOZEN)) {
			currentRow++;
			
			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createRightLabel(100, Unit.DOZEN), c);

			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			dozenUnitPriceField.setPreferredSize(new Dimension(80, 20));
			add(dozenUnitPriceField, c);
		}
		
		if (product.hasUnit(Unit.PIECES)) {
			currentRow++;
			
			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createRightLabel(100, Unit.PIECES), c);

			c.weightx = c.weighty = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			piecesUnitPriceField.setPreferredSize(new Dimension(80, 20));
			add(piecesUnitPriceField, c);
		}
		
		currentRow++;
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(ComponentUtil.createFiller(1, 5), c);
		
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
	
	public void updateDisplay(Product product, PricingScheme pricingScheme) {
		this.product = product;
		this.pricingScheme = pricingScheme;
		
		getContentPane().removeAll();
		layoutComponents();
		
		productCodeLabel.setText(product.getCode());
		productDescriptionLabel.setText(product.getDescription());
		pricingSchemeNameLabel.setText(pricingScheme.getName());
		
		if (product.hasUnit(Unit.CASE)) {
			caseUnitPriceField.setText(product.getUnitPrice(Unit.CASE).toString());
		} else {
			caseUnitPriceField.setText(null);
		}
		if (product.hasUnit(Unit.TIE)) {
			tieUnitPriceField.setText(product.getUnitPrice(Unit.TIE).toString());
		} else {
			tieUnitPriceField.setText(null);
		}
		if (product.hasUnit(Unit.CARTON)) {
			cartonUnitPriceField.setText(product.getUnitPrice(Unit.CARTON).toString());
		} else {
			cartonUnitPriceField.setText(null);
		}
		if (product.hasUnit(Unit.DOZEN)) {
			dozenUnitPriceField.setText(product.getUnitPrice(Unit.DOZEN).toString());
		} else {
			dozenUnitPriceField.setText(null);
		}
		if (product.hasUnit(Unit.PIECES)) {
			piecesUnitPriceField.setText(product.getUnitPrice(Unit.PIECES).toString());
		} else {
			piecesUnitPriceField.setText(null);
		}
	}

}
