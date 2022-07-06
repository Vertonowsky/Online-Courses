package com.example.hello_world.repository;

import com.example.hello_world.entity.Topic;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends CrudRepository<Topic, Integer> {


}
