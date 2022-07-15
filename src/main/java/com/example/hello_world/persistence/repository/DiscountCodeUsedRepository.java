package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.DiscountCodeKey;
import com.example.hello_world.persistence.model.DiscountCodeUsed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountCodeUsedRepository extends JpaRepository<DiscountCodeUsed, DiscountCodeKey> {


    Optional<DiscountCodeUsed> findById(DiscountCodeKey id);


}
