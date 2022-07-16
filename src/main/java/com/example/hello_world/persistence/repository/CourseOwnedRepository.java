package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.CourseKey;
import com.example.hello_world.persistence.model.CourseOwned;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseOwnedRepository extends JpaRepository<CourseOwned, CourseKey> {

    Optional<CourseOwned> findById(CourseKey id);

    List<CourseOwned> findAll();
}
