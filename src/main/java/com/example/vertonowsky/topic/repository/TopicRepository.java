package com.example.vertonowsky.topic.repository;

import com.example.vertonowsky.topic.model.Topic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends CrudRepository<Topic, Integer> {

    Optional<Topic> findById(Integer id);

    @Query("SELECT t FROM Topic t WHERE t.location = :videoLocation")
    List<Topic> findAllByVideoLocation(@Param("videoLocation") String videoLocation);

    void delete(Topic topic);

}
