package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.AdjustmentInListPanel;
import com.pj.magic.gui.tables.models.AdjustmentInsTableModel;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.service.AdjustmentInService;

@Component
public class AdjustmentInsTable extends MagicListTable {

	public static final int ADJUSTMENT_IN_NUMBER_COLUMN_INDEX = 0;
	public static final int TOTAL_AMOUNT_COLUMN_INDEX = 4;
	private static final String GO_TO_ADJUSTMENT_IN_ACTION_NAME = "goToAdjustmentIn";
	private static final String DELETE_ADJUSTMENT_IN_ACTION_NAME = "deleteAdjustmentIn";

	@Autowired private AdjustmentInService salesRequisitionService;
	@Autowired private AdjustmentInsTableModel tableModel;
	
	@Autowired
	public AdjustmentInsTable(AdjustmentInsTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	public void update() {
		List<AdjustmentIn> salesRequisitions = salesRequisitionService.getAllNonPostedAdjustmentIns();
		tableModel.setAdjustmentIns(salesRequisitions);
		if (!salesRequisitions.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public AdjustmentInsTableModel getAdjustmentInsTableModel() {
		return (AdjustmentInsTableModel)super.getModel();
	}
	
	public AdjustmentIn getCurrentlySelectedAdjustmentIn() {
		return getAdjustmentInsTableModel().getAdjustmentIn(getSelectedRow());
	}
	
	public void displayAdjustmentInDetails(AdjustmentIn salesRequisition) {
		AdjustmentInListPanel panel = (AdjustmentInListPanel)
				SwingUtilities.getAncestorOfClass(AdjustmentInListPanel.class, this);
		panel.displayAdjustmentInDetails(salesRequisition);
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		AdjustmentIn salesRequisition = getCurrentlySelectedAdjustmentIn();
		salesRequisitionService.delete(salesRequisition);
		tableModel.remove(salesRequisition);
		
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
					selectAdjustmentIn();
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
					selectAdjustmentIn();
				}
			}
		});
	}

	protected void selectAdjustmentIn() {
		displayAdjustmentInDetails(getCurrentlySelectedAdjustmentIn());
	}
	
}
