package com.pj.magic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import com.pj.magic.service.SalesRequisitionService;

@Configuration
public class RmiClientConfig {

    @Bean
    @DependsOn("salesRequisitionServiceExporter")
    RmiProxyFactoryBean salesRequisitionServiceRmiClient() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl("rmi://magic-db:1099/SalesRequisitionService");
        rmiProxyFactory.setServiceInterface(SalesRequisitionService.class);
        return rmiProxyFactory;
    }
    
}
