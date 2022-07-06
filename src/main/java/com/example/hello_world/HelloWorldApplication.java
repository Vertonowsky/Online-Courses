package com.example.hello_world;

import com.example.hello_world.mysql.MySQL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
@RestController
public class HelloWorldApplication {


    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
        MySQL.verifyDatabase();

        MySQL.openConnection();
        MySQL.closeConnection();
    }


    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }


    @GetMapping("/logowanie")
    public ModelAndView loginView () {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("logowanie");
        return modelAndView;
    }


    @GetMapping("/rejestracja")
    public ModelAndView registerView () {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("rejestracja");
        return modelAndView;
    }

}
