package com.ftn.sbnz.sv10.service.config;

import org.kie.api.KieBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ftn.sbnz.sv10.kjar.factory.KieBaseFactory;

@Configuration
public class DroolsConfig {

    @Bean
    public KieBase kieBase() {
        return KieBaseFactory.create();
    }
}
