package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.Chapter;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChapterRepository extends CrudRepository<Chapter, Integer> {

    Optional<Chapter> findById(Integer id);

    void delete(Chapter chapter);

}
