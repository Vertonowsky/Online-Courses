package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.Chapter;
import org.springframework.data.repository.CrudRepository;

public interface ChapterRepository extends CrudRepository<Chapter, Integer> {


}
