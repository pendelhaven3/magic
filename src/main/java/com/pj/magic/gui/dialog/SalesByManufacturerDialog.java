package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesByManufacturerDialog extends MagicDialog {

	private static final int MANUFACTURER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	
	private MagicListTable table;
	private ManufacturersTableModel tableModel;
	private SalesRequisition salesRequisition;
	
	public SalesByManufacturerDialog() {
		setSize(600, 300);
		setLocationRelativeTo(null);
		setTitle("Sales By Manufacturer");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		tableModel = new ManufacturersTableModel();
		table = new MagicListTable(tableModel);
	}

	private void registerKeyBindings() {
		// none
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 100));
		add(scrollPane, c);
	}
	
	public void updateDisplay(SalesRequisition salesRequisition) {
		this.salesRequisition = salesRequisition;
		List<Manufacturer> manufacturers = salesRequisition.getAllItemProductManufacturers();
		tableModel.setManufacturers(manufacturers);
		if (!manufacturers.isEmpty()) {
			table.changeSelection(0, 0);
		}
	}

	private class ManufacturersTableModel extends AbstractTableModel {
		
		private final String[] columnNames = {"Manufacturer", "Sales Amount"};
		
		private List<Manufacturer> manufacturers = new ArrayList<>();
		
		public void setManufacturers(List<Manufacturer> manufacturers) {
			this.manufacturers = manufacturers;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return manufacturers.size();
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
			if (columnIndex == AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Manufacturer manufacturer = manufacturers.get(rowIndex);
			switch (columnIndex) {
			case MANUFACTURER_COLUMN_INDEX:
				return manufacturer.getName();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(salesRequisition.getSalesByManufacturer(manufacturer));
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}