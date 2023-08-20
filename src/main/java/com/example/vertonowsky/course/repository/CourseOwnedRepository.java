package com.example.vertonowsky.course.repository;

import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.model.CourseKey;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseOwnedRepository extends JpaRepository<CourseOwned, CourseKey> {

    Optional<CourseOwned> findById(CourseKey id);

    @Query("SELECT c FROM CourseOwned c WHERE c.user = :user AND c.course = :course")
    Optional<CourseOwned> findIfAlreadyBought(@Param("user") User user, @Param("course") Course course);


    @Modifying
    @Query("UPDATE CourseOwned c SET c.buyDate = :buyDate, c.expiryDate = :expiryDate, c.status = com.example.vertonowsky.course.CourseStatus.RENEWED WHERE c.user = :user AND c.course = :course")
    void updateExistingCourseExpiration(@Param("user") User user, @Param("course") Course course, @Param("buyDate") OffsetDateTime buyDate, @Param("expiryDate") OffsetDateTime expiryDate);

    List<CourseOwned> findAll();
}
