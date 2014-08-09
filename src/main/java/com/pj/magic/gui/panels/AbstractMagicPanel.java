package com.pj.magic.gui.panels;

import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.KeyStroke;

// TODO: Make other panels use this
public abstract class AbstractMagicPanel extends MagicPanel {

	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
		registerKeyBindings();
		getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
	}
	
	protected abstract void initializeComponents();

	protected abstract void layoutComponents();

	protected abstract void registerKeyBindings();
	
}
