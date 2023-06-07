package com.example.hello_world.persistence.repository.token;


import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.VerificationToken;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationTokenRepository extends TokenRepository<VerificationToken> {


    @Query("SELECT t FROM VerificationToken t WHERE t.user = :user AND t.valid = :valid")
    Optional<VerificationToken> findByUserAndValid(User user, boolean valid);

}
