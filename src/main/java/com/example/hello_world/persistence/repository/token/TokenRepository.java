package com.example.hello_world.persistence.repository.token;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@NoRepositoryBean
public interface TokenRepository<T extends Token> extends CrudRepository<T, Integer> {

    Optional<T> findById(Integer id);

    Optional<T> findByToken(UUID uuid);

    //@Query("SELECT t FROM #{#entityName} t WHERE t.user = :user AND t.valid = :valid")
    Optional<T> findByUserAndValid(User user, boolean valid);

    List<T> findAllByUser(User user);

    void delete(T token);

}
