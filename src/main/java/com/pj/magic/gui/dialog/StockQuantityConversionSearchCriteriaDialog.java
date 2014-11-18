package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import org.springframework.stereotype.Component;

import com.pj.magic.model.search.StockQuantityConversionSearchCriteria;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class StockQuantityConversionSearchCriteriaDialog extends MagicDialog {

	private JComboBox<String> statusComboBox;
	private JButton searchButton;
	private StockQuantityConversionSearchCriteria searchCriteria;
	
	public StockQuantityConversionSearchCriteriaDialog() {
		setSize(350, 160);
		setLocationRelativeTo(null);
		setTitle("Search Stock Quantity Conversions");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		statusComboBox = new JComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "Not Posted", "Posted"}));
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveStockQuantityConversionCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(statusComboBox);
	}

	private void saveStockQuantityConversionCriteria() {
		searchCriteria = new StockQuantityConversionSearchCriteria();
		
		if (statusComboBox.getSelectedIndex() != 0) {
			switch (statusComboBox.getSelectedIndex()) {
			case 1:
				searchCriteria.setPosted(false);
				break;
			case 2:
				searchCriteria.setPosted(true);
				break;
			}
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		statusComboBox.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		statusComboBox.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveStockQuantityConversionCriteria();
			}
		});
		
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(80, "Status:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusComboBox.setPreferredSize(new Dimension(150, 25));
		add(statusComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createVerticalFiller(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(), c);
	}
	
	public StockQuantityConversionSearchCriteria getSearchCriteria() {
		StockQuantityConversionSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		statusComboBox.setSelectedIndex(1);
	}
	
}