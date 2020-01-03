package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchBadStocksDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.Unit;
import com.pj.magic.model.search.BadStockSearchCriteria;
import com.pj.magic.service.BadStockService;
import com.pj.magic.service.SupplierService;

@Component
public class BadStockInventoryListPanel extends StandardMagicPanel {

	@Autowired
	private BadStockService badStockService;
	
	@Autowired
	private SupplierService supplierService;
	
	private MagicListTable table;
	private BadStockTableModel tableModel = new BadStockTableModel();
    private SearchBadStocksDialog searchBadStocksDialog;
	
	public BadStockInventoryListPanel() {
	    setTitle("Bad Stock Inventory List");
	}
	
    @Override
    protected void initializeComponents() {
        table = new MagicListTable(tableModel);
        focusOnComponentWhenThisPanelIsDisplayed(table);
        
        initializeTable();
    }

	private void initializeTable() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(40);
        columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(200);
        columnModel.getColumn(AVAILABLE_QTY_CSE_COLUMN_INDEX).setPreferredWidth(40);
        columnModel.getColumn(AVAILABLE_QTY_TIE_COLUMN_INDEX).setPreferredWidth(40);
        columnModel.getColumn(AVAILABLE_QTY_CTN_COLUMN_INDEX).setPreferredWidth(40);
        columnModel.getColumn(AVAILABLE_QTY_DOZ_COLUMN_INDEX).setPreferredWidth(40);
        columnModel.getColumn(AVAILABLE_QTY_PCS_COLUMN_INDEX).setPreferredWidth(40);
    }

    public void updateDisplay() {
		List<BadStock> products = badStockService.getAllAvailableBadStocks();
		tableModel.setItems(products);
		if (!products.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
        if (searchBadStocksDialog != null) {
            searchBadStocksDialog.resetDisplay();
        }
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = c.gridy = 0;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
	    registerEscapeKeyAsBack();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToBadStockMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
        toolBar.add(new MagicToolBarButton("search", "Search", e -> searchBadStocks()));
	}

	private void searchBadStocks() {
        if (searchBadStocksDialog == null) {
            searchBadStocksDialog = new SearchBadStocksDialog(supplierService);
            searchBadStocksDialog.resetDisplay();
        }
        
        searchBadStocksDialog.setVisible(true);
        
        BadStockSearchCriteria criteria = searchBadStocksDialog.getSearchCriteria();
        if (criteria != null) {
            tableModel.setItems(badStockService.search(criteria));
            if (tableModel.hasItems()) {
                table.selectFirstRowThenFocus();
            } else {
                showMessage("No matching records");
            }
        }
	}
	
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
    private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
    private static final int AVAILABLE_QTY_CSE_COLUMN_INDEX = 2;
    private static final int AVAILABLE_QTY_TIE_COLUMN_INDEX = 3;
    private static final int AVAILABLE_QTY_CTN_COLUMN_INDEX = 4;
    private static final int AVAILABLE_QTY_DOZ_COLUMN_INDEX = 5;
    private static final int AVAILABLE_QTY_PCS_COLUMN_INDEX = 6;
    private static final String[] COLUMN_NAMES = {"Code", "Description", "CSE", "TIE", "CTN", "DOZ", "PCS"};
	
	private class BadStockTableModel extends ListBackedTableModel<BadStock> {

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BadStock badStock = getItem(rowIndex);
            
            switch (columnIndex) {
            case PRODUCT_CODE_COLUMN_INDEX:
                return badStock.getProduct().getCode();
            case PRODUCT_DESCRIPTION_COLUMN_INDEX:
                return badStock.getProduct().getDescription();
            case AVAILABLE_QTY_CSE_COLUMN_INDEX:
                return badStock.getUnitQuantity(Unit.CASE);
            case AVAILABLE_QTY_TIE_COLUMN_INDEX:
                return badStock.getUnitQuantity(Unit.TIE);
            case AVAILABLE_QTY_CTN_COLUMN_INDEX:
                return badStock.getUnitQuantity(Unit.CARTON);
            case AVAILABLE_QTY_DOZ_COLUMN_INDEX:
                return badStock.getUnitQuantity(Unit.DOZEN);
            case AVAILABLE_QTY_PCS_COLUMN_INDEX:
                return badStock.getUnitQuantity(Unit.PIECES);
            default:
                return null;
            }
        }

        @Override
        protected String[] getColumnNames() {
            return COLUMN_NAMES;
        }
	    
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case AVAILABLE_QTY_CSE_COLUMN_INDEX:
            case AVAILABLE_QTY_TIE_COLUMN_INDEX:
            case AVAILABLE_QTY_CTN_COLUMN_INDEX:
            case AVAILABLE_QTY_DOZ_COLUMN_INDEX:
            case AVAILABLE_QTY_PCS_COLUMN_INDEX:
                return Number.class;
            default:
                return super.getColumnClass(columnIndex);
            }
        }
        
	}
	
}
