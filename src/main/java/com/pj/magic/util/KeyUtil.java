package com.pj.magic.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class KeyUtil {

	public static void simulateTabKey() {
		try {
			new Robot().keyPress(KeyEvent.VK_TAB);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static void simulateDownKey() {
		try {
			new Robot().keyPress(KeyEvent.VK_DOWN);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isAlphaNumericKeyCode(int keyCode) {
		return (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9)
				|| (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z);
	}

	public static KeyStroke getEnterKey() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
	}
	
}
