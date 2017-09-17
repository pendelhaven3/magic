package com.pj.magic.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.pj.magic.OnClientCondition;

/**
 * This class is intended to provide the RMI-server Spring beans which are
 * referenced by RMI-clients via @DependsOn.
 * 
 * @author PJ Miranda
 *
 */
@Configuration
@Conditional(OnClientCondition.class)
public class DummyRmiServerConfig {

    @Bean
    String salesRequisitionServiceExporter() {
        return StringUtils.EMPTY;
    }
    
}
