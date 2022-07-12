package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.Topic;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends CrudRepository<Topic, Integer> {


}
