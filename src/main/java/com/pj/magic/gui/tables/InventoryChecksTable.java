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

import com.pj.magic.gui.panels.InventoryCheckListPanel;
import com.pj.magic.gui.tables.models.InventoryChecksTableModel;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.service.InventoryCheckService;

@Component
public class InventoryChecksTable extends JTable {

	public static final int INVENTORY_DATE_COLUMN_INDEX = 0;
	private static final String GO_TO_ADJUSTMENT_IN_ACTION_NAME = "goToInventoryCheck";
	private static final String DELETE_ADJUSTMENT_IN_ACTION_NAME = "deleteInventoryCheck";

	@Autowired private InventoryCheckService inventoryCheckService;
	@Autowired private InventoryChecksTableModel tableModel;
	
	@Autowired
	public InventoryChecksTable(InventoryChecksTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	public void update() {
		List<InventoryCheck> inventoryChecks = inventoryCheckService.getAllInventoryCheck();
		tableModel.setInventoryChecks(inventoryChecks);
		if (!inventoryChecks.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public InventoryCheck getCurrentlySelectedInventoryCheck() {
		return tableModel.getInventoryCheck(getSelectedRow());
	}
	
	public void displayInventoryCheckDetails(InventoryCheck salesRequisition) {
		InventoryCheckListPanel panel = (InventoryCheckListPanel)
				SwingUtilities.getAncestorOfClass(InventoryCheckListPanel.class, this);
		panel.displayInventoryCheckDetails(salesRequisition);
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		InventoryCheck inventoryCheck = getCurrentlySelectedInventoryCheck();
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
					selectInventoryCheck();
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
					selectInventoryCheck();
				}
			}
		});
	}

	protected void selectInventoryCheck() {
		displayInventoryCheckDetails(getCurrentlySelectedInventoryCheck());
	}
	
}
