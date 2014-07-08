package com.pj.magic;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import com.pj.magic.dialog.SelectProductDialog;
import com.pj.magic.util.KeyUtil;

public class ShowSelectProductDialogAction extends AbstractAction {

	private static final long serialVersionUID = 1358525704686641336L;
	
	private JTable table;
	
	public ShowSelectProductDialogAction(JTable table) {
		this.table = table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SelectProductDialog dialog = new SelectProductDialog();
		dialog.setVisible(true);
		
		String productCode = dialog.getSelectedProductCode();
		table.getCellEditor().cancelCellEditing();
		table.getModel().setValueAt(productCode, table.getSelectedRow(), table.getSelectedColumn());
		KeyUtil.simulateTabKey();
	}

}
