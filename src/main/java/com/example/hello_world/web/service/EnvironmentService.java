package com.example.hello_world.web.service;

public class EnvironmentService {


    private final String profile;

    public EnvironmentService(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "EnvironmentService{" + "profile='" + profile + '\'' + '}';
    }
}