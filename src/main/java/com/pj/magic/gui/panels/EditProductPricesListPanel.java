package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.EditProductPrice2Dialog;
import com.pj.magic.gui.dialog.SearchProductsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.gui.tables.models.ProductPricesTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.ProductService;

@Component
public class EditProductPricesListPanel extends StandardMagicPanel {

    private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
    private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
    
    @Autowired private EditProductPrice2Dialog editProductPriceDialog;
    @Autowired private ProductService productService;
    @Autowired private SearchProductsDialog searchProductsDialog;
    @Autowired private LoginService loginService;
    
    private MagicListTable pricesTable;
    private ProductsTableModel pricesTableModel = new ProductsTableModel();
    private JButton searchButton;
    private JButton showAllButton;
    
    @Override
    protected void initializeComponents() {
        pricesTable = new MagicListTable(pricesTableModel);
    }

    private void layoutPricesTable() {
        TableColumnModel columnModel = pricesTable.getColumnModel();
        columnModel.getColumn(ProductPricesTableModel.CODE_COLUMN_INDEX).setPreferredWidth(50);
        columnModel.getColumn(ProductPricesTableModel.DESCRIPTION_COLUMN_INDEX).setPreferredWidth(250);
    }

    @Override
    protected void registerKeyBindings() {
        pricesTable.onEnterKey(new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                selectProductPrice();
            }
        });
        
        pricesTable.onDoubleClick(() -> selectProductPrice());
        
        onEscapeKey(new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                doOnBack();
            }
        });
    }

    private void selectProductPrice() {
        if (!loginService.getLoggedInUser().canModifyPricing()) {
            return;
        }
        
        int selectedRow = pricesTable.getSelectedRow();
        Product product = pricesTableModel.getItem(selectedRow);
        editProductPriceDialog.updateDisplay(product);
        editProductPriceDialog.setVisible(true);
        
        Product updatedProduct = productService.getProduct(product.getId());
        product.setUnitPrices(updatedProduct.getUnitPrices());
        product.setUnitCosts(updatedProduct.getUnitCosts());
        pricesTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
    }

    public void updateDisplay() {
        searchProductsDialog.updateDisplay();
        
        pricesTableModel.setItems(productService.getAllActiveProducts());
        pricesTable.changeSelection(0, 0, false, false);
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                pricesTable.requestFocusInWindow();
            }
        });
    }

    @Override
    protected void doOnBack() {
        pricesTableModel.clear();
        getMagicFrame().switchToInventoryMenuPanel();
    }

    @Override
    protected void layoutMainPanel(JPanel mainPanel) {
        mainPanel.setLayout(new GridBagLayout());
        int currentRow = 0;
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 4;
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
    }

    protected void showAllProducts() {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        pricesTableModel.setItems(productService.searchProducts(criteria));
        pricesTable.changeSelection(0, 0, false, false);
        pricesTable.requestFocusInWindow();
    }

    protected void searchProducts() {
        searchProductsDialog.setVisible(true);
        ProductSearchCriteria criteria = searchProductsDialog.getSearchCriteria();
        if (criteria != null) {
            criteria.setActive(true);
            List<Product> products = productService.searchProducts(criteria);
            pricesTableModel.setItems(products);
            if (!products.isEmpty()) {
                pricesTable.changeSelection(0, 0, false, false);
                pricesTable.requestFocusInWindow();
            } else {
                showErrorMessage("No matching records");
            }
        }
    }

    private class ProductsTableModel extends ListBackedTableModel<Product> {

        private final String[] columnNames = {"Code", "Description"};
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product product = getItem(rowIndex);
            switch (columnIndex) {
            case PRODUCT_CODE_COLUMN_INDEX:
                return product.getCode();
            case PRODUCT_DESCRIPTION_COLUMN_INDEX:
                return product.getDescription();
            default:
                throw new RuntimeException("Invalid column index:" + columnIndex);
            }
        }

        @Override
        protected String[] getColumnNames() {
            return columnNames;
        }
        
    }
    
}
