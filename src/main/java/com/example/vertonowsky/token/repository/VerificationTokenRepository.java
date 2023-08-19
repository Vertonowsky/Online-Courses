package com.example.vertonowsky.token.repository;


import com.example.vertonowsky.token.model.VerificationToken;
import com.example.vertonowsky.user.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationTokenRepository extends TokenRepository<VerificationToken> {

    @Query("SELECT t FROM VerificationToken t WHERE t.user = :user AND t.valid = :valid")
    Optional<VerificationToken> findByUserAndValid(User user, boolean valid);

}
