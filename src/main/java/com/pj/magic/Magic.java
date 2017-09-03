package com.pj.magic;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.pj.magic.gui.MagicFrame;

@SpringBootApplication
public class Magic {

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UISettings.initialize();
                
//                if (ApplicationProperties.isServer()) {
//                    configLocations.add("httpserver.xml");
//                }
                
                ConfigurableApplicationContext context = new SpringApplicationBuilder(Magic.class).headless(false).run(args);
                
                MagicFrame frame = context.getBean(MagicFrame.class);
                frame.setVisible(true);
            }
        });
	}
	
	@Bean
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
	    return new TransactionTemplate(transactionManager);
	}
	
}
