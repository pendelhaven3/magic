package com.pj.magic.gui.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchProductsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ProductsTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

@Component
public class ProductListPanel extends StandardMagicPanel {

	private static final String EDIT_PRODUCT_ACTION_NAME = "editProduct";
	
	@Autowired private ProductService productService;
	@Autowired private SearchProductsDialog searchProductsDialog;
	
	private JTable table;
	private ProductsTableModel tableModel = new ProductsTableModel();
	
	public void updateDisplay() {
		List<Product> products = productService.getAllProducts();
		tableModel.setProducts(products);
		if (!products.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchProductsDialog.updateDisplay();
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel) {
			
			@Override
			public java.awt.Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				java.awt.Component r = super.prepareRenderer(renderer, row, column);
				Product item = tableModel.getProduct(row);
				if (item.isActive()) {
					r.setForeground(Color.black);
				} else {
					r.setForeground(Color.red);
				}
				return r;
			}
			
		};
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	private void searchProducts() {
		searchProductsDialog.setVisible(true);
		
		ProductSearchCriteria criteria = searchProductsDialog.getSearchCriteria();
		if (criteria != null) {
			List<Product> products = productService.searchProducts(criteria);
			tableModel.setProducts(products);
			if (!products.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_PRODUCT_ACTION_NAME);
		table.getActionMap().put(EDIT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProduct();
			}
		});
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectProduct();
			}
		});
	}

	protected void selectProduct() {
		Product product = tableModel.getProduct(table.getSelectedRow());
		getMagicFrame().switchToEditProductPanel(product);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getMagicFrame().switchToAddNewProductPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton showAllButton = new MagicToolBarButton("all", "Show All");
		showAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAllProducts();
			}
		});
		toolBar.add(showAllButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchProducts();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void showAllProducts() {
		tableModel.setProducts(productService.getAllProducts());
		table.changeSelection(0, 0, false, false);
		table.requestFocusInWindow();
		searchProductsDialog.updateDisplay();
	}
	
}
