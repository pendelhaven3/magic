package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.SalesRequisitionsListPanel;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.SalesRequisitionService;

@Component
public class SalesRequisitionsTable extends JTable {

	public static final int SALES_REQUISITION_NUMBER_COLUMN_INDEX = 0;
	public static final int CUSTOMER_NAME_COLUMN_INDEX = 1;
	public static final int CREATE_DATE_COLUMN_INDEX = 2;
	public static final int ENCODER_COLUMN_INDEX = 3;
	public static final int TOTAL_AMOUNT_COLUMN_INDEX = 4;
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
	
	public SalesRequisitionsTableModel getSalesRequisitionsTableModel() {
		return (SalesRequisitionsTableModel)super.getModel();
	}
	
	public SalesRequisition getCurrentlySelectedSalesRequisition() {
		return getSalesRequisitionsTableModel().getSalesRequisition(getSelectedRow());
	}
	
	public void displaySalesRequisitionDetails(SalesRequisition salesRequisition) {
		SalesRequisitionsListPanel panel = (SalesRequisitionsListPanel)
				SwingUtilities.getAncestorOfClass(SalesRequisitionsListPanel.class, this);
		panel.displaySalesRequisitionDetails(salesRequisition);
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_SALES_REQUISITION_ACTION_NAME);
		getActionMap().put(GO_TO_SALES_REQUISITION_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					displaySalesRequisitionDetails(getCurrentlySelectedSalesRequisition());
				}
			}
		});
	}
	
}
