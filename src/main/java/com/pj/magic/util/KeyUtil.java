package com.pj.magic.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeyUtil {

	public static void simulateTabKey() {
		try {
			new Robot().keyPress(KeyEvent.VK_TAB);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
}
