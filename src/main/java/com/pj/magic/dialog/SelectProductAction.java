package com.pj.magic.dialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.pj.magic.MagicDialog;

public class SelectProductAction extends AbstractAction {

	private static final long serialVersionUID = -1285125714776258014L;
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;

	private JTable table;
	private MagicDialog dialog;
	
	public SelectProductAction(JTable table, MagicDialog dialog) {
		this.table = table;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TableModel model = table.getModel();
		String productCode = (String)model.getValueAt(table.getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
		dialog.setReturnValue(SelectProductDialog.PRODUCT_CODE_RETURN_VALUE_NAME, productCode);
		dialog.setVisible(false);
	}

}
