package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
	
	private List<String> unitChoices;
	private JTable table;
	private String selectedUnit;
	
	public SelectUnitDialog() {
		setSize(180, 150);
		setLocationRelativeTo(null);
		setTitle("Select Unit");
		addContents();
	}

	private void addContents() {
		table = new JTable();
		
		// TODO: Attach escape key binding to parent of table component instead
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_UNIT_ACTION_NAME);
		table.getActionMap().put(SELECT_UNIT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedUnit = (String)table.getValueAt(table.getSelectedRow(), UNIT_COLUMN_INDEX);
				setVisible(false);
			}
		});
		
		registerCloseOnEscapeKeyBinding(table);
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	@Override
	public void setVisible(boolean b) {
		table.setModel(new UnitsTableModel(unitChoices));
		table.setRowSelectionInterval(0, 0);
		
		super.setVisible(b);
	}
	
	public void setUnitChoices(List<String> unitChoices) {
		this.unitChoices = unitChoices;
	}
	
	public String getSelectedUnit() {
		return selectedUnit;
	}
	
	private class UnitsTableModel extends AbstractTableModel {

		private String[] columnNames = {"Unit"};
		private List<String> units;
		
		public UnitsTableModel(List<String> units) {
			this.units = units;
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
	
}
