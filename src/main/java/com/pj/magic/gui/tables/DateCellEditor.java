package com.pj.magic.gui.tables;

import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.util.DateUtil;

public class DateCellEditor extends MagicCellEditor {

	private String fieldName;
	
	public DateCellEditor(JTextField textField, String fieldName) {
		super(textField);
		this.fieldName = fieldName;
	}

	@Override
	public boolean stopCellEditing() {
		String dateString = ((JTextField)getComponent()).getText();
		boolean valid = false;
		if (StringUtils.isEmpty(dateString)) {
			showErrorMessage(fieldName + " must be specified");
		} else if (!DateUtil.isDateString(dateString)) {
			showErrorMessage(fieldName + " must be a valid date");
		} else {
			valid = true;
		}
		return (valid) ? super.stopCellEditing() : false;
	}
	
}