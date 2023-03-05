package com.example.hello_world.web.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TermsController {

    @GetMapping("/terms")
    public ModelAndView openTermsPage() {
        return new ModelAndView("terms.html");
    }

}
