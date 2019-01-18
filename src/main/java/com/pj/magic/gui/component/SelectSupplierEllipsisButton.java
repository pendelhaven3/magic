package com.pj.magic.gui.component;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.model.Supplier;
import com.pj.magic.util.ComponentUtil;

public class SelectSupplierEllipsisButton extends EllipsisButton {

	private static final String TOOLTIP_TEXT = "Select Supplier (F5)";
	
	private SelectSupplierDialog selectSupplierDialog;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameLabel;
	private OnSelectSupplierAction onSelectSupplierAction;
	
	public SelectSupplierEllipsisButton(SelectSupplierDialog selectSupplierDialog, 
			MagicTextField supplierCodeField, JLabel supplierNameLabel) {
		this.selectSupplierDialog = selectSupplierDialog;
		this.supplierCodeField = supplierCodeField;
		this.supplierNameLabel = supplierNameLabel;
		
		setText(TOOLTIP_TEXT);
		addActionListener(e -> openSelectSupplierDialog());
		supplierCodeField.onF5Key(() -> openSelectSupplierDialog());
	}
	
	private void openSelectSupplierDialog() {
	    selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
	    selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
		    supplierCodeField.setText(supplier.getCode());
		    supplierNameLabel.setText(supplier.getName());
			if (onSelectSupplierAction != null) {
			    onSelectSupplierAction.onSelectSupplier(supplier);
			}
		}
	}

	public JPanel getFieldsPanel() {
		if (!supplierCodeField.isPreferredSizeSet()) {
		    supplierCodeField.setPreferredSize(new Dimension(100, 25));
		}
		if (!supplierNameLabel.isPreferredSizeSet()) {
		    supplierNameLabel.setPreferredSize(new Dimension(300, 25));
		}
		return ComponentUtil.createGenericPanel(supplierCodeField, this, 
				Box.createHorizontalStrut(10), supplierNameLabel);
	}
	
	public void addOnSelectSupplierAction(OnSelectSupplierAction onSelectSupplierAction) {
		this.onSelectSupplierAction = onSelectSupplierAction;
	}
	
	public interface OnSelectSupplierAction {
		
		void onSelectSupplier(Supplier supplier);
		
	}
	
}
