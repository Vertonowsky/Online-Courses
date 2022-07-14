package com.example.hello_world;

import com.example.hello_world.mysql.MySQL;
import com.example.hello_world.persistence.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@Controller
public class HelloWorldApplication {

    @Autowired
    private CourseRepository courseRepository;


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


    @RequestMapping("/")
    public String indexView(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "index";
    }
}
