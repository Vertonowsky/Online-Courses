package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.FinishedTopic;
import com.example.hello_world.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.finishedTopics FROM User u WHERE u = :user")
    Iterable<FinishedTopic> findFinishedTopics(@Param("user") User user);

    @Override
    void delete(User user);

}
