package com.example.hello_world.repository;

import com.example.hello_world.entity.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Integer> {


    Course findCourseById(Integer id);

}
