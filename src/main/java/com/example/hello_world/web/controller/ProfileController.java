package com.example.hello_world.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {



    @GetMapping("/profil")
    public String profil() {
        return "profil";
    }
}
