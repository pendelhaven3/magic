package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.util.ComponentUtil;

@Component
public class SearchProductDialog extends MagicDialog {

	private static final String SEARCH_PRODUCT_ACTION = "search";
	
	private MagicTextField productCodeField;
	private JButton searchButton;
	
	public SearchProductDialog() {
		setSize(450, 120);
		setLocationRelativeTo(null);
		setTitle("Search Product");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				returnProductCodeCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(productCodeField);
	}

	protected void returnProductCodeCriteria() {
		setVisible(false);
	}

	private void registerKeyBindings() {
		productCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SEARCH_PRODUCT_ACTION);
		productCodeField.getActionMap().put(SEARCH_PRODUCT_ACTION, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				returnProductCodeCriteria();			
			}
		});
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		productCodeField.setText(null);
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
		add(ComponentUtil.createLabel(200, "Product Code or Description:"), c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productCodeField.setPreferredSize(new Dimension(150, 20));
		add(productCodeField, c);

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
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 20));
		add(searchButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 1), c);
	}
	
	public String getProductCodeCriteria() {
		return productCodeField.getText();
	}

	public void updateDisplay() {
		productCodeField.setText(null);
	}
	
}
