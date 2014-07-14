package com.pj.magic.util;

import java.awt.Dimension;

import javax.swing.JLabel;

public class LabelUtil {

	public static final JLabel createFiller(int width, int height) {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(width, height));
		return label;
	}
	
	public static final JLabel createLabel(int width, String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width, 20));
		return label;
	}
	
}
