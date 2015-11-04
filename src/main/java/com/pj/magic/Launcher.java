package com.pj.magic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pj.magic.gui.MagicFrame;

public class Launcher {

	public void launch() {
		UISettings.initialize();
		
		List<String> configLocations = new ArrayList<>();
		configLocations.add("applicationContext.xml");
		configLocations.add("magicframe.xml");
		configLocations.add("datasource.xml");
		
		if (isHttpServerEnabled()) {
			configLocations.add("httpserver.xml");
		}
		
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext context = 
			new ClassPathXmlApplicationContext(configLocations.toArray(new String[] {}));
		context.registerShutdownHook();
		
		MagicFrame frame = context.getBean(MagicFrame.class);
		frame.setVisible(true);
	}

	private boolean isHttpServerEnabled() {
		return Files.exists(Paths.get("magic-lib", "httpserver"));
	}

}
