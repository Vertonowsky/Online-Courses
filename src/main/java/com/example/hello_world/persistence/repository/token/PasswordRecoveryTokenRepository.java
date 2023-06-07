package com.example.hello_world.persistence.repository.token;


import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.PasswordRecoveryToken;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordRecoveryTokenRepository extends TokenRepository<PasswordRecoveryToken> {


    @Query("SELECT t FROM PasswordRecoveryToken t WHERE t.user = :user AND t.valid = :valid")
    Optional<PasswordRecoveryToken> findByUserAndValid(User user, boolean valid);

}
