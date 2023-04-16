package com.example.hello_world;

import com.example.hello_world.web.config.EnvironmentConfig;
import com.example.hello_world.web.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@SpringBootApplication
@Controller
public class HelloWorldApplication extends SpringBootServletInitializer {


    private CourseService courseService;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HelloWorldApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("prod");
        applicationContext.register(EnvironmentConfig.class);
        applicationContext.refresh();

        System.out.println("[Online-Courses] Application started!");
    }

    @RequestMapping("/")
    public String indexView(Model model) {
        model.addAttribute("courses", courseService.getCoursesWithCriteria(new ArrayList<String>(), new ArrayList<String>(), 3));
        return "index";
    }

}
