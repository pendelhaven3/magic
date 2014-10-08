package com.pj.magic.util;

import java.awt.Dimension;

import javax.swing.JLabel;

public class ComponentUtil {

	public static final JLabel createFiller() {
		return createFiller(1, 1);
	}

	public static final JLabel createFiller(int width, int height) {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(width, height));
		return label;
	}
	
	public static final JLabel createLabel(int width) {
		return createLabel(width, "");
	}
	
	public static final JLabel createLabel(int width, String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width, 30));
		return label;
	}
	
	public static final JLabel createRightLabel(int width, String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width, 30));
		label.setHorizontalAlignment(JLabel.RIGHT);
		return label;
	}
	
}
