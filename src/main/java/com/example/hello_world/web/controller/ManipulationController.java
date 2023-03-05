package com.example.hello_world.web.controller;

import com.example.hello_world.Category;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ManipulationController {


    @Autowired
    private CourseRepository courseRepository;


    @GetMapping("/manipulation/loadCourses")
    public Iterable<Course> loadCourses(@RequestParam String typeFilters, @RequestParam String categoryFilters, @RequestParam int limit) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        if (limit < 0) limit = 0;
        List<String> typeParamList = mapper.readValue(typeFilters, new TypeReference<>(){}); //Convert string in JSON format to List<String>
        List<String> categoryParamList = mapper.readValue(categoryFilters, new TypeReference<>(){}); //Convert string in JSON format to List<String>
        categoryParamList.replaceAll(String::toUpperCase); //make every string contain only big characters

        if (typeParamList.size() == 0 && categoryParamList.size() == 0) {
            Iterable<Course> courses = courseRepository.findAll(); //return all courses if no filters are applied
            if (limit > 0) return limitNumberOfIterable(courses, limit);
            return courses;
        }


        //change category filters from List<String> to List<Category> [Enum type]
        List<Category> list = new ArrayList<>();
        for (String s : categoryParamList) {

            try {
                Category cat = Category.valueOf(s);
                list.add(cat);

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        Iterable<Course> courses = courseRepository.findAllWithCondition(typeParamList, list);
        if (limit > 0) return limitNumberOfIterable(courses, limit);
        return courses;
    }


    private ArrayList<Course> limitNumberOfIterable(Iterable<Course> iterable, int limit) {
        ArrayList<Course> array = new ArrayList<>();
        int size = 0;
        for (Course c : iterable) {
            if (size < limit) {
                array.add(c);
                size++;
            }
            if (size == limit) break;
        }
        return array;
    }




}




