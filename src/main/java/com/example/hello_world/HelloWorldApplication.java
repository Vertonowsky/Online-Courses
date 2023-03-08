package com.example.hello_world;

import com.example.hello_world.mysql.MySQL;
import com.example.hello_world.persistence.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
public class HelloWorldApplication extends SpringBootServletInitializer {


    @Autowired
    private CourseRepository courseRepository;


    public static String videoesPath = "http://localhost:8080/videos/";
    public static String videoesLocalPath = "D:\\Xampp\\htdocs\\edu\\videos";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HelloWorldApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
        MySQL.verifyDatabase();

        MySQL.openConnection();
        MySQL.closeConnection();
    }

    @RequestMapping("/")
    public String indexView(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "index";
    }
}
