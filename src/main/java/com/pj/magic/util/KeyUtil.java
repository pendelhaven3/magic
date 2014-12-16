package com.pj.magic.util;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class KeyUtil {

	public static boolean isAlphaNumericKeyCode(int keyCode) {
		return (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9)
				|| (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z);
	}

	public static boolean isNumericKeyCodeFromNumPad(int keyCode) {
		return (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9);
	}

	public static KeyStroke getEnterKey() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
	}

}