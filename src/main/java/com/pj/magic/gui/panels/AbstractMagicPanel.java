package com.pj.magic.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

// TODO: Make other panels use this
public abstract class AbstractMagicPanel extends MagicPanel {

	private List<JComponent> focusOrder;
	
	@PostConstruct
	public void initialize() {
		initializeComponents();
		layoutComponents();
		registerKeyBindings();
		registerBackKeyBinding();
	}
	
	protected abstract void initializeComponents();

	protected abstract void layoutComponents();

	protected abstract void registerKeyBindings();
	
	/**
	 * What to do when back key (currently F9) is pressed.
	 */
	protected abstract void doOnBack();
	
	/**
	 * Add the components to focusOrder list in the order that you want the focus to be.
	 * Custom focus order only used when calling focusNextField() method.
	 * 
	 * @param focusOrder
	 */
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		// meant to be overriden
	}
	
	protected void focusNextField() {
		if (focusOrder == null) {
			focusOrder = new ArrayList<>();
			initializeFocusOrder(focusOrder);
		}
		java.awt.Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
		int focusOwnerIndex = focusOrder.indexOf(focusOwner);
		if (focusOwnerIndex != focusOrder.size() - 1) {
			focusOrder.get(focusOwnerIndex + 1).requestFocusInWindow();
		}
	}

	private void registerBackKeyBinding() {
		final String actionName = "back";
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), actionName);
		getActionMap().put(actionName, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected int showConfirmMessage(String message) {
		return JOptionPane.showConfirmDialog(this, message, "Confirmation Message", JOptionPane.YES_NO_OPTION);
	}
	
	protected void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
}
