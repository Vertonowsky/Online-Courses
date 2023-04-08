package com.example.hello_world.web.service;

import com.example.hello_world.Category;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {


    private CourseRepository courseRepository;

    @Autowired
    public void setCourseRepository(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    public Iterable<Course> getCoursesWithCriteria(List<String> typeFilters, List<String> categoryFilters, int limit) {

        if (typeFilters.size() == 0 && categoryFilters.size() == 0) {
            Iterable<Course> courses = courseRepository.findAll(); //return all courses if no filters are applied
            if (limit > 0) return limitNumberOfIterable(courses, limit);
            return courses;
        }


        //change category filters from List<String> to List<Category> [Enum type]
        List<Category> catFilters = new ArrayList<>();
        for (String s : categoryFilters) {
            try {
                Category cat = Category.valueOf(s);
                catFilters.add(cat);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        Iterable<Course> courses = courseRepository.findAllWithCondition(typeFilters, catFilters);
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
