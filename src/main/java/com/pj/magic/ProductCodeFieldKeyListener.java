package com.pj.magic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import com.pj.magic.util.KeyUtil;

public class ProductCodeFieldKeyListener implements KeyListener {

	private static final int PRODUCT_CODE_MAXIMUM_LENGTH = 9;
	
	@Override
	public void keyPressed(KeyEvent event) {
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (!isAlphaNumericKeyCode(event.getKeyCode())) {
			return;
		}
		
		if (isMaximumProductCodeLengthReached(event)) {
			KeyUtil.simulateTabKey();
		}
	}

	private static boolean isMaximumProductCodeLengthReached(KeyEvent event) {
		JTextField textField = (JTextField)event.getComponent();
		return textField.getText().length() == PRODUCT_CODE_MAXIMUM_LENGTH;
	}
	
	private static boolean isAlphaNumericKeyCode(int keyCode) {
		return (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9)
				|| (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

}
