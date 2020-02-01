package com.pj.magic.gui.dialog;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.gui.tables.models.ProductsTableModel;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.service.BadStockService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SelectBadStockDialog extends MagicDialog {

	private static final String SELECT_PRODUCT_ACTION_NAME = "selectProduct";
	private static final String UNIT_COST_INFO_TABLE = "unitCostInfoTable";

	@Autowired
	private BadStockService badStockService;

	private BadStocksTableModel badStocksTableModel = new BadStocksTableModel();
	private UnitCostsAndQuantitiesTableModel unitCostsAndQuantitiesTableModel;
	private MagicListTable badStocksTable;
	private JTable unitCostsAndQuantitiesTable;
	private JPanel infoTablePanel;
	private Product selectedProduct;
	
	public SelectBadStockDialog() {
		setSize(500, 450);
		setLocationRelativeTo(null);
		setTitle("Select Bad Stock");
		
		initialize();
	}

	public void initialize() {
		badStocksTable = new MagicListTable(badStocksTableModel);
		badStocksTable.getColumnModel().getColumn(ProductsTableModel.CODE_COLUMN_INDEX)
			.setPreferredWidth(150);
		badStocksTable.getColumnModel().getColumn(ProductsTableModel.DESCRIPTION_COLUMN_INDEX)
			.setPreferredWidth(300);
		
		unitCostsAndQuantitiesTableModel = new UnitCostsAndQuantitiesTableModel();
		unitCostsAndQuantitiesTable = new MagicListTable(unitCostsAndQuantitiesTableModel);
		
		layoutComponents();
		registerKeyBindings();
		
		badStocksTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = badStocksTable.getSelectedRow();
				if (selectedRow != -1) {
					BadStock badStock= badStocksTableModel.getItem(selectedRow);
					unitCostsAndQuantitiesTableModel.setBadStock(badStock);
				}
			}
		});
	}

	private void registerKeyBindings() {
		badStocksTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
		badStocksTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_PRODUCT_ACTION_NAME);
		badStocksTable.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProduct();
			}
		});
		
		badStocksTable.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectProduct();
			}
		});
		
	}

	protected void selectProduct() {
		selectedProduct = badStocksTableModel.getItem(badStocksTable.getSelectedRow()).getProduct();
		setVisible(false);
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}
	
	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedProduct = null;
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
		
		JScrollPane productsScrollPane = new JScrollPane(badStocksTable);
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
		
		JScrollPane unitCostsAndQuantitiesScrollPane = new JScrollPane(unitCostsAndQuantitiesTable);
		infoTablePanel.add(unitCostsAndQuantitiesScrollPane, UNIT_COST_INFO_TABLE);
		
		return infoTablePanel;
	}

	public void searchBadStocks(String codeOrDescription, String currentlySelectedCode, Supplier supplier) {
		List<BadStock> badStocks = badStockService.searchAllBadStocksBySupplier(supplier, codeOrDescription);
		badStocksTableModel.setItems(badStocks);
		
		if (!badStocks.isEmpty()) {
			int selectedRow = 0;
			if (!StringUtils.isEmpty(currentlySelectedCode)) {
				int i = 0;
				for (BadStock badStock : badStocks) {
					if (badStock.getProduct().getCode().equals(currentlySelectedCode)) {
						selectedRow = i;
						break;
					}
					i++;
				}
			}
			badStocksTable.changeSelection(selectedRow, 0, false, false);
		}
		
		((CardLayout)infoTablePanel.getLayout()).show(infoTablePanel, UNIT_COST_INFO_TABLE);
	}

	private class BadStocksTableModel extends ListBackedTableModel<BadStock> {

		private final String[] columnNames = {"Code", "Description"};
		
		public static final int CODE_COLUMN_INDEX = 0;
		public static final int DESCRIPTION_COLUMN_INDEX = 1;
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			BadStock badStock = getItem(rowIndex);
			switch (columnIndex) {
			case CODE_COLUMN_INDEX:
				return badStock.getProduct().getCode();
			case DESCRIPTION_COLUMN_INDEX:
				return badStock.getProduct().getDescription();
			default:
				return null;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}
	
	private class UnitCostsAndQuantitiesTableModel extends AbstractTableModel {

		private static final int UNIT_COLUMN_INDEX = 0;
		private static final int QUANTITY_COLUMN_INDEX = 1;
		private static final int UNIT_COST_COLUMN_INDEX = 2;
		private final String[] columnNames = {"Unit", "Quantity", "Cost"};
		private final String[] units = {Unit.CASE, Unit.TIE, Unit.CARTON, Unit.DOZEN, Unit.PIECES};
		
		private BadStock badStock;
		
		public void setBadStock(BadStock badStock) {
			this.badStock = badStock;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return 5;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String unit = units[rowIndex];
			
			switch (columnIndex) {
			case UNIT_COLUMN_INDEX:
				return unit;
			case QUANTITY_COLUMN_INDEX:
				return (badStock != null) ? (badStock.getUnitQuantity(unit) != null ? badStock.getUnitQuantity(unit) : 0) : 0; 
			case UNIT_COST_COLUMN_INDEX:
				if (badStock != null && badStock.getProduct().hasUnit(unit)) {
					return FormatterUtil.formatAmount(badStock.getProduct().getGrossCost(unit));
				} else {
					return "0.00";
				}
			default:
				return null;
			}
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == UNIT_COST_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
	}
	
}