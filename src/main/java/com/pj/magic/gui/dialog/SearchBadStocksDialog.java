package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;

import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.BadStockSearchCriteria;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ListUtil;

public class SearchBadStocksDialog extends MagicDialog {

	private SupplierService supplierService;
	
	private MagicComboBox<Supplier> supplierComboBox;
	private MagicButton searchButton;
	private BadStockSearchCriteria searchCriteria;
	    
	public SearchBadStocksDialog(SupplierService supplierService) {
	    this.supplierService = supplierService;
	    
		setSize(480, 155);
		setLocationRelativeTo(null);
		setTitle("Search Bad Stocks");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
        initializeComponents();
        registerKeyBindings();
        layoutComponents();
	}

	private void initializeComponents() {
		supplierComboBox = new MagicComboBox<>();
		
		searchButton = new MagicButton("Search");
		searchButton.addActionListener(e -> saveCriteria());
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierComboBox);
	}

	private void saveCriteria() {
		searchCriteria = new BadStockSearchCriteria();
		searchCriteria.setSupplier((Supplier)supplierComboBox.getSelectedItem());
		searchCriteria.setEmpty(false);
		
		setVisible(false);
	}

	private void registerKeyBindings() {
	    supplierComboBox.onEnterKey(() -> searchButton.requestFocusInWindow());
	    searchButton.onEnterKey(() -> saveCriteria());
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
		add(ComponentUtil.createLabel(120, "Supplier:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
        supplierComboBox.setPreferredSize(new Dimension(300, 25));
        add(supplierComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 10;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	public BadStockSearchCriteria getSearchCriteria() {
		BadStockSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void resetDisplay() {
		searchCriteria = null;
		supplierComboBox.setModel(ListUtil.toDefaultComboBoxModel(supplierService.getAllSuppliers(), true));
		supplierComboBox.setSelectedIndex(0);
	}
	
}
