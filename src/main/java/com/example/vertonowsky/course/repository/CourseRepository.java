package com.example.vertonowsky.course.repository;

import com.example.vertonowsky.course.CategoryType;
import com.example.vertonowsky.course.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {

    Optional<Course> findById(Integer id);

    @Query("SELECT DISTINCT c.type FROM Course c ORDER BY c.type ASC")
    List<String> findAllTypes();

    @Query("SELECT DISTINCT c.categoryType FROM Course c ORDER BY c.categoryType ASC")
    List<String> findAllCategories();

    @Query("SELECT c FROM Course c ORDER BY c.name ASC")
    List<Course> findAll();

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.chapters AS ch LEFT JOIN FETCH ch.topics AS t WHERE t.id = :topicId")
    Optional<Course> findByTopicId(@Param("topicId") Integer topicId);

    @Query("SELECT c FROM Course c WHERE c.type IN :tp OR c.categoryType IN :ctg ORDER BY c.name ASC")
    List<Course> findAllWithCondition(@Param("tp") List<String> types, @Param("ctg") List<CategoryType> categories);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.advantages LEFT JOIN FETCH c.chapters AS chapters LEFT JOIN FETCH chapters.topics LEFT JOIN FETCH c.paymentHistories LEFT JOIN FETCH c.coursesOwned WHERE c.id = :id")
    Optional<Course> findByIdAllData(Integer id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.advantages WHERE c.id = :id")
    Optional<Course> findByIdWithAdvantages(Integer id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.chapters AS ch LEFT JOIN FETCH ch.topics WHERE c.id = :id")
    Optional<Course> findByIdWithChapters(Integer id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.paymentHistories WHERE c.id = :id")
    Optional<Course> findByIdWithPaymentHistories(Integer id);

}
