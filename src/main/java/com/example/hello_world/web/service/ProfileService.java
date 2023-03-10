package com.example.hello_world.web.service;

public class ProfileService {


    private String profile;

    public ProfileService(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "ProfileService{" + "profile='" + profile + '\'' + '}';
    }
}