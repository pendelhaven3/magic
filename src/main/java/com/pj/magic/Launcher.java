package com.pj.magic;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pj.magic.gui.MagicFrame;

public class Launcher {

	public void launch() {
		String[] configLocations = new String[] {"applicationContext.xml", "datasource.xml", "bootstrap.xml"};
		
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configLocations);
		context.registerShutdownHook();
		
		MagicFrame frame = context.getBean(MagicFrame.class);
		frame.setVisible(true);
	}

}
