package com.example.vertonowsky.token.repository;

import com.example.vertonowsky.token.model.Token;
import com.example.vertonowsky.user.User;
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
    List<T> findByUserAndValid(User user, boolean valid);

    void delete(T token);

}
