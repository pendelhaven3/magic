package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.ProductsTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;

@Component
public class ProductListPanel extends AbstractMagicPanel {

	private static final String EDIT_PRODUCT_ACTION_NAME = "editProduct";
	private static final String BACK_ACTION_NAME = "back";
	
	@Autowired private ProductService productService;
	
	private JTable table;
	private ProductsTableModel tableModel = new ProductsTableModel();
	
	public void updateDisplay() {
		tableModel.setProducts(productService.getAllProducts());
		table.changeSelection(0, 0, false, false);
	}

	@Override
	protected void initializeComponents() {
		table = new JTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		registerBackKeyBinding();
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_PRODUCT_ACTION_NAME);
		table.getActionMap().put(EDIT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Product product = tableModel.getProduct(table.getSelectedRow());
				getMagicFrame().switchToEditProductListPanel(product);
			}
		});
	}

	private void registerBackKeyBinding() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), BACK_ACTION_NAME);
		getActionMap().put(BACK_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getMagicFrame().switchToMainMenuPanel();
			}
		});
	}
	
}
