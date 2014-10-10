package com.pj.magic.gui.component;

import javax.swing.JComboBox;

public class MagicComboBox<E> extends JComboBox<E> {

	private boolean triggerCustomListeners = true;
	
	public MagicComboBox() {
		super();
		putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	}
	
	public void setSelectedItem(Object anObject, boolean triggerCustomListeners) {
		this.triggerCustomListeners = triggerCustomListeners;
		super.setSelectedItem(anObject);
		this.triggerCustomListeners = true;
	}
	
	public boolean shouldTriggerCustomListeners() {
		return triggerCustomListeners;
	}
	
}
