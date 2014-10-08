package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.AreaInventoryReportListPanel;
import com.pj.magic.gui.tables.models.AreaInventoryReportsTableModel;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.service.AreaInventoryReportService;

@Component
public class AreaInventoryReportsTable extends JTable {

	public static final int INVENTORY_DATE_COLUMN_INDEX = 0;
	public static final int AREA_COLUMN_INDEX = 1;
	private static final String GO_TO_ADJUSTMENT_IN_ACTION_NAME = "goToAreaInventoryReport";
	private static final String DELETE_ADJUSTMENT_IN_ACTION_NAME = "deleteAreaInventoryReport";

	@Autowired private AreaInventoryReportService inventoryCheckService;
	@Autowired private AreaInventoryReportsTableModel tableModel;
	
	@Autowired
	public AreaInventoryReportsTable(AreaInventoryReportsTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	public void update() {
		List<AreaInventoryReport> areaInventoryReports = inventoryCheckService.getAllAreaInventoryReports();
		tableModel.setAreaInventoryReports(areaInventoryReports);
		if (!areaInventoryReports.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public AreaInventoryReport getCurrentlySelectedAreaInventoryReport() {
		return tableModel.getAreaInventoryReport(getSelectedRow());
	}
	
	public void displayAreaInventoryReportDetails(AreaInventoryReport salesRequisition) {
		AreaInventoryReportListPanel panel = (AreaInventoryReportListPanel)
				SwingUtilities.getAncestorOfClass(AreaInventoryReportListPanel.class, this);
		panel.displayAreaInventoryReportDetails(salesRequisition);
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		AreaInventoryReport inventoryCheck = getCurrentlySelectedAreaInventoryReport();
		inventoryCheckService.delete(inventoryCheck);
		tableModel.remove(inventoryCheck);
		
		if (tableModel.getRowCount() > 0) {
			if (selectedRowIndex == tableModel.getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
		
		// TODO: update table as well if any new SR has been created
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_ADJUSTMENT_IN_ACTION_NAME);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_ADJUSTMENT_IN_ACTION_NAME);
		
		getActionMap().put(GO_TO_ADJUSTMENT_IN_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					selectAreaInventoryReport();
				}
			}
		});
		getActionMap().put(DELETE_ADJUSTMENT_IN_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					int confirm = JOptionPane.showConfirmDialog(getParent(), "Delete selected adjustment out?");
					if (confirm == JOptionPane.YES_OPTION) {
						removeCurrentlySelectedRow();
					}
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectAreaInventoryReport();
				}
			}
		});
	}

	protected void selectAreaInventoryReport() {
		displayAreaInventoryReportDetails(getCurrentlySelectedAreaInventoryReport());
	}
	
}
