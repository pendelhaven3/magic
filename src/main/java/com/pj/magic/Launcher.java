package com.pj.magic;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {

	public void launch() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		try {
			MagicFrame frame = context.getBean(MagicFrame.class);
			frame.setVisible(true);
		} finally {
			context.close();
		}
	}

}
