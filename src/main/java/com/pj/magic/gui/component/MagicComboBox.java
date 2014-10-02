package com.pj.magic.gui.component;

import javax.swing.JComboBox;

public class MagicComboBox<E> extends JComboBox<E> {

	public MagicComboBox() {
		super();
		putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	}
	
}
