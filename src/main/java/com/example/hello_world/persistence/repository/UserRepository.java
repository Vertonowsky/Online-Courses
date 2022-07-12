package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Override
    void delete(User user);

}
