package com.pj.magic;

import org.eclipse.jetty.server.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pj.magic.httpserver.HttpServerHandler;

@Configuration
@ConditionalOnResource(resources = {"classpath:server.properties"})
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
