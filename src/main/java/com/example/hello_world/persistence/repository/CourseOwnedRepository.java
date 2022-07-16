package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.CourseKey;
import com.example.hello_world.persistence.model.CourseOwned;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseOwnedRepository extends JpaRepository<CourseOwned, CourseKey> {

    List<CourseOwned> findAll();
}
