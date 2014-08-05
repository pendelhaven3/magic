package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

@Component
public class SelectUnitDialog extends MagicDialog {

	private static final String SELECT_UNIT_ACTION_NAME = "selectUnit";
	private static final int UNIT_COLUMN_INDEX = 0;
	
	private JTable unitsTable;
	private UnitsTableModel unitsTableModel = new UnitsTableModel();
	private String selectedUnit;
	
	public SelectUnitDialog() {
		setSize(180, 150);
		setLocationRelativeTo(null);
		setTitle("Select Unit");
		initialize();
	}

	private void initialize() {
		unitsTable = new JTable(unitsTableModel);

		JScrollPane scrollPane = new JScrollPane(unitsTable);
		add(scrollPane);	
		
		unitsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_UNIT_ACTION_NAME);
		unitsTable.getActionMap().put(SELECT_UNIT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedUnit = (String)unitsTable.getValueAt(unitsTable.getSelectedRow(), UNIT_COLUMN_INDEX);
				setVisible(false);
			}
		});
	}

	public void setUnits(List<String> units) {
		unitsTableModel.setUnits(units);
	}
	
	public String getSelectedUnit() {
		return selectedUnit;
	}
	
	private class UnitsTableModel extends AbstractTableModel {

		private String[] columnNames = {"Unit"};
		private List<String> units = new ArrayList<>();
		
		public void setUnits(List<String> units) {
			this.units = units;
			fireTableDataChanged();
		}
		
		public List<String> getUnits() {
			return units;
		}
		
		@Override
		public int getRowCount() {
			return units.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return units.get(rowIndex);
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedUnit = null;
	}
	
	public void searchUnits(String unit) {
		int selectedRow = 0;
		List<String> units = unitsTableModel.getUnits();
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).startsWith(unit)) {
				selectedRow = i;
				break;
			}
		}
		unitsTable.changeSelection(selectedRow, 0, false, false);
	}
	
}
