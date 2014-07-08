package com.pj.magic;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.pj.magic.dialog.SelectProductDialog;
import com.pj.magic.dialog.SelectUnitDialog;
import com.pj.magic.util.KeyUtil;

public class ShowSelectionDialogAction extends AbstractAction {

	private static final long serialVersionUID = 1358525704686641336L;
	
	private ItemsTable table;
	
	public ShowSelectionDialogAction(ItemsTable table) {
		this.table = table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (table.isProductCodeFieldSelected()) {
			SelectProductDialog dialog = new SelectProductDialog();
			dialog.setVisible(true);
			
			String productCode = dialog.getSelectedProductCode();
			if (table.isEditing()) {
				table.getCellEditor().cancelCellEditing();
			}
			table.setValueAt(productCode, table.getSelectedRow(), table.getSelectedColumn());
			KeyUtil.simulateTabKey();
		} else if (table.isUnitFieldSelected()) {
			SelectUnitDialog dialog = new SelectUnitDialog();
			dialog.setVisible(true);
			
			String unit = dialog.getSelectedUnit();
			if (table.isEditing()) {
				table.getCellEditor().cancelCellEditing();
			}
			table.setValueAt(unit, table.getSelectedRow(), table.getSelectedColumn());
			KeyUtil.simulateTabKey();
		}
	}

}
