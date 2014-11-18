package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.search.InventoryCheckSearchCriteria;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class InventoryCheckSearchCriteriaDialog extends MagicDialog {

	private MagicTextField codeOrDescriptionField;
	private JButton searchButton;
	private InventoryCheckSearchCriteria searchCriteria;
	
	public InventoryCheckSearchCriteriaDialog() {
		setSize(450, 160);
		setLocationRelativeTo(null);
		setTitle("Search Inventory Check Summary Items");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		codeOrDescriptionField = new MagicTextField();
		codeOrDescriptionField.setMaximumLength(30);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInventoryCheckCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(codeOrDescriptionField);
	}

	private void saveInventoryCheckCriteria() {
		searchCriteria = new InventoryCheckSearchCriteria();
		searchCriteria.setCodeOrDescriptionLike(codeOrDescriptionField.getText());
		setVisible(false);
	}

	private void registerKeyBindings() {
		codeOrDescriptionField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInventoryCheckCriteria();
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
		add(ComponentUtil.createLabel(140, "Code/Description:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		codeOrDescriptionField.setPreferredSize(new Dimension(200, 25));
		add(codeOrDescriptionField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createVerticalFiller(15), c);
		
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
	
	public InventoryCheckSearchCriteria getSearchCriteria() {
		InventoryCheckSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		codeOrDescriptionField.setText(null);
	}
	
}