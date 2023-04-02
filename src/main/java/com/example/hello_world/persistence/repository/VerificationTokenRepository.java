package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.VerificationToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {

    Optional<VerificationToken> findById(Integer id);

    @Query("SELECT t FROM VerificationToken t WHERE t.user = :user AND t.valid = :valid")
    Optional<VerificationToken> findByUserAndValid(User user, boolean valid);

    void delete(VerificationToken verificationToken);

}
