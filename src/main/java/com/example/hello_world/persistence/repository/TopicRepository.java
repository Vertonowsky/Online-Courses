package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.Topic;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TopicRepository extends CrudRepository<Topic, Integer> {

    Optional<Topic> findById(Integer id);

    void delete(Topic topic);

}
