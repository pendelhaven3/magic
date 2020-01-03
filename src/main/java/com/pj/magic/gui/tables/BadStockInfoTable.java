package com.pj.magic.gui.tables;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.pj.magic.model.BadStock;
import com.pj.magic.model.Unit;

public class BadStockInfoTable extends JTable {

	private BadStockInfoTableModel tableModel;
	
	public BadStockInfoTable() {
		tableModel = new BadStockInfoTableModel();
		setModel(tableModel);
		
		setTableHeader(null);
		setRowHeight(20);
		setShowGrid(false);
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			
			@Override
			public java.awt.Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
				setHorizontalAlignment(JLabel.CENTER);
				return this;
			}
			
		});
	}
	
	public void setBadStock(BadStock badStock) {
		tableModel.setBadStock(badStock);
	}
	
	private class BadStockInfoTableModel extends AbstractTableModel {

		private BadStock badStock = new BadStock();
		
		public void setBadStock(BadStock badStock) {
			this.badStock = badStock;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return 2;
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (rowIndex) {
			case 0:
			    switch (columnIndex) {
			    case 0: return Unit.CASE;
                case 1: return Unit.TIE;
                case 2: return Unit.CARTON;
                case 3: return Unit.DOZEN;
                case 4: return Unit.PIECES;
			    }
			case 1:
			    if (badStock == null) {
			        return null;
			    }
			    
                switch (columnIndex) {
                case 0: return badStock.getUnitQuantityForDisplay(Unit.CASE);
                case 1: return badStock.getUnitQuantityForDisplay(Unit.TIE);
                case 2: return badStock.getUnitQuantityForDisplay(Unit.CARTON);
                case 3: return badStock.getUnitQuantityForDisplay(Unit.DOZEN);
                case 4: return badStock.getUnitQuantityForDisplay(Unit.PIECES);
                }
			}
			
			return null;
		}
	}
	
}