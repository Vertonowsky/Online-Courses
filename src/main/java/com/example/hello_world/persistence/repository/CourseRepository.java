package com.example.hello_world.persistence.repository;

import com.example.hello_world.Category;
import com.example.hello_world.persistence.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {


    @Query("SELECT c FROM Course c WHERE c.type IN :tp OR c.category IN :ctg")
    Iterable<Course> findAllWithCondition(@Param("tp") List<String> types, @Param("ctg") List<Category> categories);

    @Query("SELECT DISTINCT c.type FROM Course c ORDER BY c.type ASC")
    Iterable<String> findAllTypes();

    @Query("SELECT DISTINCT c.category FROM Course c ORDER BY c.category ASC")
    Iterable<String> findAllCategories();

    Optional<Course> findById(Integer id);

}
