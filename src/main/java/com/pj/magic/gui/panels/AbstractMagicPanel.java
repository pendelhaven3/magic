package com.pj.magic.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.util.StringUtils;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.MagicFrame;

public abstract class AbstractMagicPanel extends JPanel {

	public static final String BACK_ACTION_COMMAND_NAME = "back";
	
	private List<JComponent> focusOrder;
	
	@PostConstruct
	public void initialize() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		registerBackKeyBinding();
		
		initializeComponents();
		layoutComponents();
		registerKeyBindings();
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
	
	protected MagicFrame getMagicFrame() {
		return (MagicFrame)SwingUtilities.getRoot(this);
	}
	
	protected void focusOnComponentWhenThisPanelIsDisplayed(JComponent component) {
		final JComponent target = component;
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if (!target.isFocusable()) {
					target.setFocusable(true);
				}
				target.requestFocusInWindow();
			}
		});
	}
	
	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error Message", JOptionPane.ERROR_MESSAGE);
	}
	
	protected void validateMandatoryField(JTextField field, String description) throws ValidationException {
		if (StringUtils.isEmpty(field.getText())) {
			showErrorMessage(description + " must be specified");
			field.requestFocusInWindow();
			throw new ValidationException();
		}
	}
	
}
