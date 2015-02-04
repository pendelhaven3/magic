package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.BadPurchaseReturnSearchCriteriaDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.search.BadPurchaseReturnSearchCriteria;
import com.pj.magic.service.BadPurchaseReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class BadPurchaseReturnListPanel extends StandardMagicPanel {
	
	private static final int BAD_PURCHASE_RETURN_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	private static final int STATUS_COLUMN_INDEX = 3;
	private static final int POST_DATE_COLUMN_INDEX = 4;
	
	@Autowired private BadPurchaseReturnService badPurchaseReturnService;
	@Autowired private BadPurchaseReturnSearchCriteriaDialog badPurchaseReturnSearchCriteriaDialog;
	
	private MagicListTable table;
	private BadPurchaseReturnsTableModel tableModel;
	
	@Override
	public void initializeComponents() {
		initializeTable();
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new BadPurchaseReturnsTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
		tableModel.setBadPurchaseReturns(badPurchaseReturnService.getAllNewBadPurchaseReturns());
		badPurchaseReturnSearchCriteriaDialog.updateDisplay();
	}

	public void displayBadPurchaseReturnDetails(BadPurchaseReturn badPurchaseReturn) {
		getMagicFrame().switchToBadPurchaseReturnPanel(badPurchaseReturn);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectBadPurchaseReturn();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectBadPurchaseReturn();
			}
		});
	}
	
	private void selectBadPurchaseReturn() {
		displayBadPurchaseReturnDetails(getCurrentlySelectedBadPurchaseReturn());
	}

	public BadPurchaseReturn getCurrentlySelectedBadPurchaseReturn() {
		return tableModel.getBadPurchaseReturn(table.getSelectedRow());
	}
	
	protected void switchToNewBadPurchaseReturnPanel() {
		getMagicFrame().switchToBadPurchaseReturnPanel(new BadPurchaseReturn());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewBadPurchaseReturnPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchBadPurchaseReturns();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchBadPurchaseReturns() {
		badPurchaseReturnSearchCriteriaDialog.setVisible(true);
		
		BadPurchaseReturnSearchCriteria criteria = badPurchaseReturnSearchCriteriaDialog.getSearchCriteria();
		if (criteria != null) {
			List<BadPurchaseReturn> badPurchaseReturns = badPurchaseReturnService.search(criteria);
			tableModel.setBadPurchaseReturns(badPurchaseReturns);
			if (!badPurchaseReturns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

	private class BadPurchaseReturnsTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"PRBS No.", "Supplier", "Total Amount", "Status", "Post Date"};
		
		private List<BadPurchaseReturn> badPurchaseReturns = new ArrayList<>();
		
		public void setBadPurchaseReturns(List<BadPurchaseReturn> badPurchaseReturns) {
			this.badPurchaseReturns = badPurchaseReturns;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return badPurchaseReturns.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			BadPurchaseReturn badPurchaseReturn = badPurchaseReturns.get(rowIndex);
			switch (columnIndex) {
			case BAD_PURCHASE_RETURN_NUMBER_COLUMN_INDEX:
				return badPurchaseReturn.getBadPurchaseReturnNumber();
			case SUPPLIER_COLUMN_INDEX:
				return badPurchaseReturn.getSupplier().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(badPurchaseReturn.getTotalAmount());
			case STATUS_COLUMN_INDEX:
				return badPurchaseReturn.getStatus();
			case POST_DATE_COLUMN_INDEX:
				return badPurchaseReturn.isPosted() ? 
						FormatterUtil.formatDateTime(badPurchaseReturn.getPostDate()) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		public BadPurchaseReturn getBadPurchaseReturn(int index) {
			return badPurchaseReturns.get(index);
		}
		
	}
	
}