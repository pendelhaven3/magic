package com.pj.magic.util;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;

import com.pj.magic.Constants;

public class LabelUtil {

	public static final JLabel createFiller(int width, int height) {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(width, height));
		return label;
	}
	
	public static final JLabel createLabel(int width, String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width, 30));
		return label;
	}
	
	public static final String formatDate(Date date) {
		return new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
	}
	
}
