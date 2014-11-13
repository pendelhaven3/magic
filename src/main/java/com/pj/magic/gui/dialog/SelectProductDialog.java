package com.pj.magic.gui.dialog;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ProductsTableModel;
import com.pj.magic.gui.tables.models.UnitCostsAndQuantitiesTableModel;
import com.pj.magic.gui.tables.models.UnitPricesAndQuantitiesTableModel;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.util.ProductSearchCriteria;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

@Component
public class SelectProductDialog extends MagicDialog {

	private static final String SELECT_PRODUCT_ACTION_NAME = "selectProduct";
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final String UNIT_PRICE_INFO_TABLE = "unitPriceInfoTable";
	private static final String UNIT_COST_INFO_TABLE = "unitCostInfoTable";

	@Autowired private ProductService productService;

	private ProductsTableModel productsTableModel = new ProductsTableModel();
	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = 
			new UnitPricesAndQuantitiesTableModel();
	private UnitCostsAndQuantitiesTableModel unitCostsAndQuantitiesTableModel =
			new UnitCostsAndQuantitiesTableModel();
	private JTable productsTable;
	private String selectedProductCode; // TODO: Why not use product here instead?
	private JTable unitPricesAndQuantitiesTable;
	private JTable unitCostsAndQuantitiesTable;
	private JPanel infoTablePanel;
	private Product selectedProduct;
	
	public SelectProductDialog() {
		setSize(500, 450);
		setLocationRelativeTo(null);
		setTitle("Select Product");
	}

	@PostConstruct
	public void initialize() {
		productsTable = new MagicListTable(productsTableModel);
		productsTable.getColumnModel().getColumn(ProductsTableModel.CODE_COLUMN_INDEX)
			.setPreferredWidth(150);
		productsTable.getColumnModel().getColumn(ProductsTableModel.DESCRIPTION_COLUMN_INDEX)
			.setPreferredWidth(300);
		
		unitPricesAndQuantitiesTable = new MagicListTable(unitPricesAndQuantitiesTableModel);
		unitCostsAndQuantitiesTable = new MagicListTable(unitCostsAndQuantitiesTableModel);
		
		layoutComponents();
		registerKeyBindings();
		
		productsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = productsTable.getSelectedRow();
				if (selectedRow != -1) {
					Product product = productsTableModel.getProduct(selectedRow);
					unitPricesAndQuantitiesTableModel.setProduct(product);
					unitCostsAndQuantitiesTableModel.setProduct(product);
				}
			}
		});
	}

	private void registerKeyBindings() {
		productsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
		productsTable.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProduct();
			}
		});
		
		productsTable.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectProduct();
			}
		});
		
	}

	protected void selectProduct() {
		selectedProductCode = (String)productsTable.getValueAt(productsTable.getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
		selectedProduct = productsTableModel.getProduct(productsTable.getSelectedRow());
		setVisible(false);
	}

	// TODO: Remove this
	public String getSelectedProductCode() {
		return selectedProductCode;
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}
	
	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedProduct = null;
		selectedProductCode = null;
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		
		JScrollPane productsScrollPane = new JScrollPane(productsTable);
		productsScrollPane.setPreferredSize(new Dimension(400, 100));
		add(productsScrollPane, c);

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
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(createInfoTablePanel(), c);
	}
	
	private JPanel createInfoTablePanel() {
		infoTablePanel = new JPanel(new CardLayout());
		infoTablePanel.setPreferredSize(new Dimension(400, 149));
		
		JScrollPane unitPricesAndQuantitiesScrollPane = new JScrollPane(unitPricesAndQuantitiesTable);
		infoTablePanel.add(unitPricesAndQuantitiesScrollPane, UNIT_PRICE_INFO_TABLE);
		
		JScrollPane unitCostsAndQuantitiesScrollPane = new JScrollPane(unitCostsAndQuantitiesTable);
		infoTablePanel.add(unitCostsAndQuantitiesScrollPane, UNIT_COST_INFO_TABLE);
		
		return infoTablePanel;
	}

	public void searchProducts(String productCode) {
		searchProducts(productCode, new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
	}

	public void searchProducts(String codeOrDescription, PricingScheme pricingScheme) {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setActive(true);
		criteria.setCodeOrDescriptionLike(codeOrDescription);
		criteria.setPricingScheme(pricingScheme);
		
		List<Product> products = productService.searchProducts(criteria);
		productsTableModel.setProducts(products);
		
		if (!products.isEmpty()) {
			productsTable.changeSelection(0, 0, false, false);
		}
		
		((CardLayout)infoTablePanel.getLayout()).show(infoTablePanel, UNIT_PRICE_INFO_TABLE);
	}

	public void searchProducts(String codeOrDescription, Supplier supplier) {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setActive(true);
		criteria.setCodeOrDescriptionLike(codeOrDescription);
		criteria.setSupplier(supplier);
		
		List<Product> products = productService.searchProducts(criteria);
		productsTableModel.setProducts(products);
		
		if (!products.isEmpty()) {
			productsTable.changeSelection(0, 0, false, false);
		}
		
		((CardLayout)infoTablePanel.getLayout()).show(infoTablePanel, UNIT_COST_INFO_TABLE);
	}
	
}
