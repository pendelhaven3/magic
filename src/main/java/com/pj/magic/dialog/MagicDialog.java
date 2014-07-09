package com.pj.magic.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public abstract class MagicDialog extends JDialog {

	private static final String CLOSE_ACTION_NAME = "close";
	
	public MagicDialog() {
		setModal(true);
	}
	
	protected void registerCloseOnEscapeKeyBinding(JTable table) {
		final JDialog dialog = this;
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE_ACTION_NAME);
		table.getActionMap().put(CLOSE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
	}
	
}
