package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.EditProductPriceDialog;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SearchProductDialog;
import com.pj.magic.gui.tables.models.ProductPricesTableModel;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.util.ProductSearchCriteria;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainPricingSchemePanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainPricingSchemePanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	private static final String SELECT_PRODUCT_PRICE_ACTION_NAME = "selectProductPrice";
	
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private EditProductPriceDialog editProductPriceDialog;
	@Autowired private ProductService productService;
	@Autowired private SearchProductDialog searchProductDialog;
	@Autowired private PrintService printService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	
	private PricingScheme pricingScheme;
	private MagicTextField nameField;
	private JButton saveButton;
	private JTable pricesTable;
	private ProductPricesTableModel pricesTableModel = new ProductPricesTableModel();
	private JButton searchButton;
	private JButton showAllButton;
	private JButton printButton;
	private JButton printPreviewButton;
	
	@Override
	protected void initializeComponents() {
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePricingScheme();
			}
		});
		
		pricesTable = new JTable(pricesTableModel);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(saveButton);
	}
	
	protected void savePricingScheme() {
		if (!validatePricingScheme()) {
			return;
		}
		
		if (confirm("Save?")) {
			pricingScheme.setName(nameField.getText());
			
			try {
				pricingSchemeService.save(pricingScheme);
				showMessage("Saved!");
				updateDisplay(pricingScheme);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validatePricingScheme() {
		try {
			validateMandatoryField(nameField, "Name");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	private void layoutPricesTable() {
		TableColumnModel columnModel = pricesTable.getColumnModel();
		columnModel.getColumn(ProductPricesTableModel.CODE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.DESCRIPTION_COLUMN_INDEX).setPreferredWidth(250);
		columnModel.getColumn(ProductPricesTableModel.CASE_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.TIE_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.CARTON_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.DOZEN_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.PIECES_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.RIGHT);
		
		columnModel.getColumn(ProductPricesTableModel.CASE_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.TIE_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.CARTON_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.DOZEN_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.PIECES_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
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
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePricingScheme();
			}
		});
		
		pricesTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				SELECT_PRODUCT_PRICE_ACTION_NAME);
		pricesTable.getActionMap().put(SELECT_PRODUCT_PRICE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProductPrice();
			}
		});
		
		pricesTable.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectProductPrice();
			}
		});
		
	}

	protected void selectProductPrice() {
		int selectedRow = pricesTable.getSelectedRow();
		Product product = pricesTableModel.getProduct(selectedRow);
		editProductPriceDialog.updateDisplay(product, pricingScheme);
		editProductPriceDialog.setVisible(true);
		
		Product updatedProduct = productService.getProduct(product.getId(), pricingScheme);
		product.setUnitPrices(updatedProduct.getUnitPrices());
		product.setUnitCosts(updatedProduct.getUnitCosts());
		pricesTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
	}

	public void updateDisplay(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
		if (pricingScheme.getId() == null) {
			clearDisplay();
			return;
		}

		pricingScheme = pricingSchemeService.get(pricingScheme.getId());
		nameField.setText(pricingScheme.getName());
		pricesTableModel.setProducts(pricingScheme.getProducts());
		pricesTable.changeSelection(0, 0, false, false);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				pricesTable.requestFocusInWindow();
			}
		});
		
		searchButton.setEnabled(true);
		showAllButton.setEnabled(true);
		printButton.setEnabled(true);
		printPreviewButton.setEnabled(true);
	}

	private void clearDisplay() {
		nameField.setText(null);

		List<Product> products = Collections.emptyList();
		pricesTableModel.setProducts(products);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				nameField.requestFocusInWindow();
			}
		});
		
		searchButton.setEnabled(false);
		showAllButton.setEnabled(false);
		printButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPricingSchemeListPanel();
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
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Name: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(200, 20));
		mainPanel.add(nameField, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(new JSeparator(), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		layoutPricesTable();
		JScrollPane pricesTableScrollPane = new JScrollPane(pricesTable);
		pricesTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(pricesTableScrollPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		showAllButton = new MagicToolBarButton("all", "Show All");
		showAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAllProducts();
			}
		});
		toolBar.add(showAllButton);
		
		searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchProducts();
			}
		});
		toolBar.add(searchButton);

		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewPricingScheme();
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPricingScheme();
			}
		});
		toolBar.add(printButton);
	}

	private void printPreviewPricingScheme() {
		printPreviewDialog.updateDisplay(
				printService.generateReportAsString(pricingScheme, pricesTableModel.getProducts()));
		printPreviewDialog.setVisible(true);
	}

	private void printPricingScheme() {
		printService.print(pricingScheme, pricesTableModel.getProducts());
	}

	protected void showAllProducts() {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setPricingScheme(pricingScheme);
		pricesTableModel.setProducts(productService.searchProducts(criteria));
		pricesTable.changeSelection(0, 0, false, false);
		pricesTable.requestFocusInWindow();
	}

	protected void searchProducts() {
		searchProductDialog.updateDisplay();
		searchProductDialog.setVisible(true);
		String productCode = searchProductDialog.getProductCodeCriteria();
		if (!StringUtils.isEmpty(productCode)) {
			ProductSearchCriteria criteria = new ProductSearchCriteria();
			criteria.setPricingScheme(pricingScheme);
			criteria.setCode(productCode);
			
			List<Product> products = productService.searchProducts(criteria);
			pricesTableModel.setProducts(products);
			if (!products.isEmpty()) {
				pricesTable.changeSelection(0, 0, false, false);
				pricesTable.requestFocusInWindow();
			}
		}
	}

}
