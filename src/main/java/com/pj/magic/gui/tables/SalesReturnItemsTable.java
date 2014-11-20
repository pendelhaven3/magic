package com.pj.magic.gui.tables;

import java.awt.Color;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.ColoredTableCellRenderer;
import com.pj.magic.gui.tables.models.SalesReturnItemsTableModel;
import com.pj.magic.model.SalesReturn;

@Component
public class SalesReturnItemsTable extends MagicTable {

	public static final int CODE_COLUMN_INDEX = 0;
	public static final int DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	public static final int RETURN_QUANTITY_COLUMN_INDEX = 6;
	public static final int RETURN_AMOUNT_COLUMN_INDEX = 7;
	
	@Autowired private SalesReturnItemsTableModel tableModel;
	
	@Autowired
	public SalesReturnItemsTable(SalesReturnItemsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
	}

	private void initializeColumns() {
		ColoredTableCellRenderer coloredCellRenderer = new ColoredTableCellRenderer() {
			
			@Override
			protected boolean isCellColored(int row, int column) {
				return column == RETURN_QUANTITY_COLUMN_INDEX;
			}
			
			@Override
			protected Color getColor() {
				return Color.yellow;
			}
		};
		columnModel.getColumn(RETURN_QUANTITY_COLUMN_INDEX).setCellRenderer(coloredCellRenderer);
	}

	public void setSalesReturn(SalesReturn salesReturn) {
		tableModel.setSalesReturn(salesReturn);
	}

	public void highlight() {
		requestFocusInWindow();
	}
	
}