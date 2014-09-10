package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public abstract class MagicDialog extends JDialog {

	private static final String CLOSE_ACTION_NAME = "close";
	
	public MagicDialog() {
		setModal(true);
		closeDialogWhenEscapeKeyPressed();
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
	
	protected void closeDialogWhenEscapeKeyPressed() {
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE_ACTION_NAME);
		getRootPane().getActionMap().put(CLOSE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doWhenEscapeKeyPressed();
				setVisible(false);
			}

		});
	}

	protected abstract void doWhenEscapeKeyPressed();
	
	protected void focusOnComponentWhenThisPanelIsDisplayed(JComponent component) {
		final JComponent target = component;
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if (!target.isFocusable()) {
					target.setFocusable(true);
				}
				target.requestFocusInWindow();
			}
		});
	}
	
}
