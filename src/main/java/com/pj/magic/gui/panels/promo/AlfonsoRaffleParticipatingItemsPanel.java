package com.pj.magic.gui.panels.promo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.exception.ProductAlreadyExistsException;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.component.SelectProductEllipsisButton;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;

@Component
public class AlfonsoRaffleParticipatingItemsPanel extends StandardMagicPanel {

	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	
	@Autowired private PromoService promoService;
	@Autowired private ProductService productService;
	@Autowired private SelectProductDialog selectProductDialog;
	
	private MagicTextField productCodeField;
	private JLabel productDescriptionField = new JLabel();
	private SelectProductEllipsisButton selectProductButton;
	private MagicButton addButton;
	private MagicToolBarButton deleteItemButton;
	
	private MagicListTable table;
	private ProductsTableModel tableModel = new ProductsTableModel();
	
	public void updateDisplay() {
		productCodeField.setText(null);
		productDescriptionField.setText(null);
		
		tableModel.setItems(promoService.getAllAlfonsoRaffleParticipatingItems());
	}

	@Override
	protected void initializeComponents() {
		productCodeField = new MagicTextField();
		productCodeField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		productCodeField.setEnabled(false);
		
		selectProductButton = new SelectProductEllipsisButton(
				selectProductDialog, productCodeField, productDescriptionField);
		selectProductButton.setEnabled(false);
		
		addButton = new MagicButton("Add");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addProduct();
			}
		});
		addButton.setEnabled(false);
		
		table = new MagicListTable(tableModel);
		table.getColumnModel().getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		table.getColumnModel().getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(400);
		
		focusOnComponentWhenThisPanelIsDisplayed(productCodeField);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Product:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		mainPanel.add(createProductPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		addButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(addButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.insets.top = 15;
		c.weightx = c.weighty = 0.0;
		c.gridwidth = 6;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridwidth = 6;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	private JPanel createProductPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		productCodeField.setPreferredSize(new Dimension(120, 25));
		panel.add(productCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectProductButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectProductButton, c);
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		productDescriptionField.setPreferredSize(new Dimension(200, 25));
		panel.add(productDescriptionField, c);
		
		return panel;
	}
	
	@Override
	protected void registerKeyBindings() {
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToAlfonsoRaffleMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow >= 0) {
					deleteProduct(tableModel.getItem(selectedRow));
				}
			}
		});
		deleteItemButton.setEnabled(false);
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void addProduct() {
		String productCode = productCodeField.getText();
		if (StringUtils.isEmpty(productCode)) {
			showErrorMessage("Product Code must be specified");
			productCodeField.requestFocusInWindow();
			return;
		}
		
		Product product = productService.findProductByCode(productCode);
		if (product == null) {
			showErrorMessage("Invalid product code");
			productCodeField.requestFocusInWindow();
			return;
		}
		
		try {
			promoService.addProductToAlfonsoRaffleParticipatingItems(product);
			showMessage("Product added");
			updateDisplay();
			productCodeField.requestFocusInWindow();
		} catch (ProductAlreadyExistsException e) {
			showErrorMessage("Product already included in participating items");
			productCodeField.requestFocusInWindow();
		} catch (Exception e) {
			showMessageForUnexpectedError(e);
		}
	}
	
	private void deleteProduct(Product product) {
		try {
			promoService.deleteAlfonsoPartipatingItem(product);
			showMessage("Product removed");
			updateDisplay();
		} catch (Exception e) {
			showMessageForUnexpectedError(e);
		}
	}
	
	private class ProductsTableModel extends ListBackedTableModel<Product>{

		private final String[] columnNames = {"Product Code", "Product Description"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Product product = getItem(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return product.getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return product.getDescription();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}

	}
	
}
