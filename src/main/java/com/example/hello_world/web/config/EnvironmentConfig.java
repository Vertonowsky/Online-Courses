package com.example.hello_world.web.config;


import com.example.hello_world.web.service.EnvironmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EnvironmentConfig {

    /**
     * Environment profiles. There are two environments available.
     * - dev  -> development environment
     * - prod -> production environment
     */

    @Bean
    @Profile("dev")
    public EnvironmentService environmentServiceDev(){
        return new EnvironmentService("dev");
    }

    @Bean
    @Profile("prod")
    public EnvironmentService environmentServiceProd(){
        return new EnvironmentService("prod");
    }
}