package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.springframework.stereotype.Component;

import com.pj.magic.exception.SalesRequisitionItemPostException;
import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.gui.tables.MagicListTable;

@Component
public class SalesRequisitionPostExceptionsDialog extends MagicDialog {

	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int QUANTITY_COLUMN_INDEX = 3;
	private static final int ERROR_MESSAGE_COLUMN_INDEX = 4;
	
	private ExceptionsTableModel tableModel;
	private MagicListTable table;
	
	public SalesRequisitionPostExceptionsDialog() {
		setSize(800, 300);
		setLocationRelativeTo(null);
		setTitle("Post Validation Errors");
	}

	@PostConstruct
	public void initialize() {
		tableModel = new ExceptionsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(200);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ERROR_MESSAGE_COLUMN_INDEX).setPreferredWidth(200);
		
		layoutComponents();
		registerKeyBindings();
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
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		
		JScrollPane productsScrollPane = new JScrollPane(table);
		productsScrollPane.setPreferredSize(new Dimension(400, 100));
		add(productsScrollPane, c);
	}
	
	public void updateDisplay(SalesRequisitionPostException exception) {
		tableModel.setExceptions(exception.getExceptions());
	}
	
	private class ExceptionsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Code", "Description", "Unit", "Quantity", "Error"};
		
		private List<SalesRequisitionItemPostException> exceptions = new ArrayList<>();
		
		public void setExceptions(List<SalesRequisitionItemPostException> exceptions) {
			this.exceptions = exceptions;
		}
		
		@Override
		public int getRowCount() {
			return exceptions.size();
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
			SalesRequisitionItemPostException exception = exceptions.get(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return exception.getItem().getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return exception.getItem().getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return exception.getItem().getUnit();
			case QUANTITY_COLUMN_INDEX:
				return exception.getItem().getQuantity();
			case ERROR_MESSAGE_COLUMN_INDEX:
				return exception.getErrorMessage();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

	}
	
}