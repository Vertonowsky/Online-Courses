package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.Token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository<T extends Token> {

    Optional<T> findById(Integer id);

    Optional<T> findByToken(UUID uuid);

    Optional<T> findByUserAndValid(User user, boolean valid);

    List<T> findAllByUser(User user);

    void delete(T token);

}
