package com.pj.magic.gui.component;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pj.magic.gui.MagicFrame;

public abstract class MagicPanel extends JPanel {

	protected MagicFrame getMagicFrame() {
		return (MagicFrame)SwingUtilities.getRoot(this);
	}
	
}
