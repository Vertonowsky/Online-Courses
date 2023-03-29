package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {

    Optional<VerificationToken> findById(Integer id);

    void delete(VerificationToken verificationToken);

}
