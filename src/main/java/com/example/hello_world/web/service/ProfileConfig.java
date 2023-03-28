package com.example.hello_world.web.service;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProfileConfig {

    /**
     * Environment profiles. There are two environments available.
     * - dev  -> development environment
     * - prod -> production environment
     */

    @Bean
    @Profile("dev")
    public ProfileService profileServiceDev(){
        return new ProfileService("dev");
    }

    @Bean
    @Profile("prod")
    public ProfileService profileServiceProd(){
        return new ProfileService("prod");
    }
}