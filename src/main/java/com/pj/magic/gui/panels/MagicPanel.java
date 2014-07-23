package com.pj.magic.gui.panels;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pj.magic.gui.MagicFrame;

public abstract class MagicPanel extends JPanel {

	public MagicPanel() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	protected MagicFrame getMagicFrame() {
		return (MagicFrame)SwingUtilities.getRoot(this);
	}
	
	protected void focusOnComponentWhenThisPanelIsDisplayed(JComponent component) {
		final JComponent target = component;
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				target.requestFocusInWindow();
			}
		});
	}
	
}
