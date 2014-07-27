package com.pj.magic.gui.component;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MagicToolBarButton extends JButton {

	public MagicToolBarButton(String imageName, String toolTipText) {
		setPreferredSize(new Dimension(36, 36));
		setIcon(new ImageIcon(getClass().getClassLoader().getResource("images\\" + imageName + ".png")));
		setToolTipText(toolTipText);
	}
	
}
