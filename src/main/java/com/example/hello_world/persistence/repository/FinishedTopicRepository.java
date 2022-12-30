package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.FinishedTopic;
import com.example.hello_world.persistence.model.Topic;
import com.example.hello_world.persistence.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FinishedTopicRepository extends CrudRepository<FinishedTopic, Integer> {


    Optional<FinishedTopic> findById(Integer id);

    @Query("SELECT ft FROM FinishedTopic ft WHERE ft.user = :usr AND ft.topic = :tpc")
    Optional<FinishedTopic> findAllWithCondition(@Param("usr") User user, @Param("tpc") Topic topic);

    void delete(FinishedTopic finishedTopic);

}
