package com.example.vertonowsky.course.repository;

import com.example.vertonowsky.course.Category;
import com.example.vertonowsky.course.dto.CourseDto;
import com.example.vertonowsky.course.dto.CourseListDto;
import com.example.vertonowsky.course.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {



    @Query("SELECT NEW com.example.vertonowsky.course.dto.CourseListDto(c.id, c.name, c.category, c.description) FROM Course c WHERE c.type IN :tp OR c.category IN :ctg")
    List<CourseListDto> findAllWithCondition(@Param("tp") List<String> types, @Param("ctg") List<Category> categories);

    @Query("SELECT NEW com.example.vertonowsky.course.dto.CourseListDto(c.id, c.name, c.category, c.description) FROM Course c")
    List<CourseListDto> findAllDtos();

    @Query("SELECT DISTINCT c.type FROM Course c ORDER BY c.type ASC")
    List<String> findAllTypes();

    @Query("SELECT DISTINCT c.category FROM Course c ORDER BY c.category ASC")
    List<String> findAllCategories();


    @Query("SELECT NEW com.example.vertonowsky.course.dto.CourseDto(c.id, c.name, c.category) FROM Course c")
    Iterable<CourseDto> findAllCoursesNames();

    Optional<Course> findById(Integer id);

}
