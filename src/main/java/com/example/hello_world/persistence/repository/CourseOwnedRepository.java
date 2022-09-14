package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.model.CourseKey;
import com.example.hello_world.persistence.model.CourseOwned;
import com.example.hello_world.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
public interface CourseOwnedRepository extends JpaRepository<CourseOwned, CourseKey> {

    Optional<CourseOwned> findById(CourseKey id);

    @Query("SELECT c FROM CourseOwned c WHERE c.user = :user AND c.course = :course")
    Optional<CourseOwned> findIfAlreadyBought(@Param("user") User user, @Param("course") Course course);


    @Modifying
    @Query("UPDATE CourseOwned c SET c.buyDate = :buyDate, c.expiryDate = :expiryDate, c.status = com.example.hello_world.CourseStatus.RENEWED WHERE c.user = :user AND c.course = :course")
    void updateExistingCourseExpiration(@Param("user") User user, @Param("course") Course course, @Param("buyDate") Date buyDate, @Param("expiryDate") Date expiryDate);

    List<CourseOwned> findAll();
}
