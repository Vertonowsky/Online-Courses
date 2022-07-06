package com.example.hello_world.repository;

import com.example.hello_world.entity.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Integer> {


    //Iterable<Course> findAllWithCondition(String categoryFilters, String subjectFilters);

    Course findCourseById(Integer id);

}
