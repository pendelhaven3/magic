package com.pj.magic.config;

import org.eclipse.jetty.server.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.pj.magic.OnServerCondition;
import com.pj.magic.httpserver.HttpServerHandler;

@Configuration
@Conditional(OnServerCondition.class)
public class HttpServerConfig {
    
    @Bean
    public Server httpServer() throws Exception {
        Server server = new Server(8080);
        server.setHandler(httpServerHandler());
        server.start();
        
        return server;
    }

    @Bean
    public HttpServerHandler httpServerHandler() {
        return new HttpServerHandler();
    }
    
}
