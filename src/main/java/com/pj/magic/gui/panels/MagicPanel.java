package com.pj.magic.gui.panels;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.pj.magic.gui.MagicFrame;

public abstract class MagicPanel extends JPanel {

	public MagicPanel() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		disableF11KeyBinding();
	}
	
	protected MagicFrame getMagicFrame() {
		return (MagicFrame)SwingUtilities.getRoot(this);
	}
	
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
	
	/*
	 * F11 seems to select the window options 
	 * (triggered by clicking the icon in the upper left window corner)
	 */
	private void disableF11KeyBinding() {
		getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
	}
	
}
