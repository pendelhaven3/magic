package com.pj.magic.gui.itemstable;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.SalesRequisitionService;

@Component
public class SalesRequisitionsTable extends JTable {

	private static final String[] COLUMN_NAMES = {"SR No.", "Customer Name"};
	private static final int SALES_REQUISITION_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_NAME_COLUMN_INDEX = 1;
	private static final String GO_TO_SALES_REQUISITION_ACTION_NAME = "goToSalesRequisition";

	@Autowired private SalesRequisitionService salesRequisitionService;
	
	private SalesRequisitionsTableModel tableModel = new SalesRequisitionsTableModel();
	
	@PostConstruct
	public void initialize() {
		setModel(tableModel);
		registerKeyBindings();
    }
	
	public void update() {
		List<SalesRequisition> salesRequisitions = salesRequisitionService.getAllSalesRequisitions();
		tableModel.setSalesRequisitions(salesRequisitions);
		if (!salesRequisitions.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_SALES_REQUISITION_ACTION_NAME);
		getActionMap().put(GO_TO_SALES_REQUISITION_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("here!");
			}
		});
	}
	
	private class SalesRequisitionsTableModel extends AbstractTableModel {

		private List<SalesRequisition> salesRequisitions = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return salesRequisitions.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesRequisition salesRequisition = salesRequisitions.get(rowIndex);
			switch (columnIndex) {
			case SALES_REQUISITION_NUMBER_COLUMN_INDEX:
				return salesRequisition.getSalesRequisitionNumber().toString();
			case CUSTOMER_NAME_COLUMN_INDEX:
				return salesRequisition.getCustomerName();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		public void setSalesRequisitions(List<SalesRequisition> salesRequisitions) {
			this.salesRequisitions = salesRequisitions;
			fireTableDataChanged();
		}
		
	}
	
}
