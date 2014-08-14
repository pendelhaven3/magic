package com.pj.magic;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pj.magic.gui.MagicFrame;

public class Launcher {

	public void launch() {
//		Font courierNewFont = new Font("Courier New", Font.PLAIN, 14);
//		UIManager.put("Table.font", courierNewFont);
//		UIManager.put("TextField.font", courierNewFont);
		
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		context.registerShutdownHook();
		
		MagicFrame frame = context.getBean(MagicFrame.class);
		frame.setVisible(true);
	}

}
