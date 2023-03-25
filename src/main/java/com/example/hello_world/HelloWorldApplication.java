package com.example.hello_world;

import com.example.hello_world.mysql.MySQL;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.web.service.ProfileConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
public class HelloWorldApplication extends SpringBootServletInitializer {


    @Autowired
    private CourseRepository courseRepository;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HelloWorldApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
        MySQL.verifyDatabase();

        MySQL.openConnection();
        MySQL.closeConnection();

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("prod");
        applicationContext.register(ProfileConfiguration.class);
        applicationContext.refresh();
    }

    @RequestMapping("/")
    public String indexView(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "index";
    }

}
