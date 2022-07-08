package com.example.hello_world.repository;

import com.example.hello_world.Category;
import com.example.hello_world.entity.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Integer> {


    @Query("SELECT c FROM Course c WHERE c.type IN :tp OR c.category IN :ctg")
    Iterable<Course> findAllWithCondition(@Param("tp") List<String> types, @Param("ctg") List<Category> categories);


    Course findCourseById(Integer id);

}
