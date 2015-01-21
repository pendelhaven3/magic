package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class InventoryReportPanel extends StandardMagicPanel {

	private static final int CODE_COLUMN_INDEX = 0;
	private static final int DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int QUANTITY_COLUMN_INDEX = 3;
	private static final int UNIT_COST_COLUMN_INDEX = 4;
	private static final int TOTAL_COST_COLUMN_INDEX = 5;
	
	@Autowired private ReportService reportService;
	
	private JLabel totalCostLabel;
	private JTable table;
	private InventoryReportItemsTableModel tableModel;
	
	public void updateDisplay() {
		InventoryReport report = reportService.getInventoryReport();
		tableModel.setItems(report.getItems());
		if (!report.getItems().isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		totalCostLabel.setText(FormatterUtil.formatAmount(report.getTotalCost()));
	}

	@Override
	protected void initializeComponents() {
		totalCostLabel = new JLabel();
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new InventoryReportItemsTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(DESCRIPTION_COLUMN_INDEX).setPreferredWidth(250);
		table.getColumnModel().getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		totalCostLabel.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(ComponentUtil.createGenericPanel(
				ComponentUtil.createLabel(100, "Total Cost:"), totalCostLabel), c);
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private class InventoryReportItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Code", "Description", "Unit", "Quantity", "Unit Cost",
				"Total Cost"};
		
		private List<InventoryReportItem> items = new ArrayList<>();
		
		public void setItems(List<InventoryReportItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return items.size();
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
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case UNIT_COST_COLUMN_INDEX:
			case TOTAL_COST_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryReportItem item = items.get(rowIndex);
			switch (columnIndex) {
			case CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return item.getQuantity();
			case UNIT_COST_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getCost());
			case TOTAL_COST_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getTotalCost());
			default:
				throw new RuntimeException("Error fetching column index: " + columnIndex);
			}
		}
		
	}
	
}