package com.pj.magic.gui.component;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public abstract class MagicCellEditor extends DefaultCellEditor {

	public MagicCellEditor(JTextField textField) {
		super(textField);
	}

	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(
				SwingUtilities.getWindowAncestor(getComponent()), 
				message,
				"Error Message",
				JOptionPane.ERROR_MESSAGE
		);
	}
	
}
