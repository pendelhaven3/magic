package com.pj.magic.gui.component;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import com.pj.magic.gui.panels.AbstractMagicPanel;

public class MagicToolBar extends JToolBar {

	public MagicToolBar() {
		setFloatable(false);
	}
	
	public void addBackButton(AbstractMagicPanel panel) {
		JButton backButton = new MagicToolBarButton("back", "Back (F9)");
		backButton.setActionCommand(AbstractMagicPanel.BACK_ACTION_COMMAND_NAME);
		if (panel instanceof ActionListener) {
			backButton.addActionListener((ActionListener)panel);
		}
		add(backButton);
	}
	
}
