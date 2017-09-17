package com.pj.magic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

import com.pj.magic.OnServerCondition;
import com.pj.magic.service.SalesRequisitionService;

@Configuration
@Conditional(OnServerCondition.class)
public class RmiServerConfig {

    @Bean
    RmiServiceExporter salesRequisitionServiceExporter(SalesRequisitionService service) {
        Class<SalesRequisitionService> serviceInterface = SalesRequisitionService.class;
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setServiceInterface(serviceInterface);
        exporter.setService(service);
        exporter.setServiceName(serviceInterface.getSimpleName());
        exporter.setRegistryPort(1099); 
        return exporter;
    }
    
}
