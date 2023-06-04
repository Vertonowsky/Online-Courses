package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.PasswordRecoveryToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryTokenRepository extends CrudRepository<PasswordRecoveryToken, Integer>, TokenRepository<PasswordRecoveryToken> {

    Optional<PasswordRecoveryToken> findById(Integer id);

    Optional<PasswordRecoveryToken> findByToken(UUID uuid);

    @Query("SELECT t FROM PasswordRecoveryToken t WHERE t.user = :user AND t.valid = :valid")
    Optional<PasswordRecoveryToken> findByUserAndValid(User user, boolean valid);

    List<PasswordRecoveryToken> findAllByUser(User user);

    void delete(PasswordRecoveryToken verificationToken);

}
