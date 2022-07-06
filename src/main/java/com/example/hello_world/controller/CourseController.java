package com.example.hello_world.controller;

import com.example.hello_world.Category;
import com.example.hello_world.entity.Course;
import com.example.hello_world.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/add2")
    public String addCourse() {
        Course course = new Course("Egzamin Ósmoklasisty", "Kurs", "Multimedialny kurs", Category.Matematyka, "Brak korzyści", 200.0, 0.0);
        Course course2 = new Course("Egzamin Maturalny", "Kurs 2", "2222222 kurs", Category.Informatyka, "Super fajne korzyści", 17.0, 0.0);
        courseRepository.save(course);
        courseRepository.save(course2);
        return "Added new course to repository!";
    }


    @GetMapping("/list2")
    public Iterable<Course> getCourses() {
        return courseRepository.findAll();
    }

}
