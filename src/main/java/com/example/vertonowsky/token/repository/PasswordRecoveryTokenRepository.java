package com.example.vertonowsky.token.repository;


import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.user.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordRecoveryTokenRepository extends TokenRepository<PasswordRecoveryToken> {

    @Query("SELECT t FROM PasswordRecoveryToken t WHERE t.user = :user AND t.valid = :valid")
    List<PasswordRecoveryToken> findByUserAndValid(User user, boolean valid);

}
