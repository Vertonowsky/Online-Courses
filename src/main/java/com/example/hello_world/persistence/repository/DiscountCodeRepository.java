package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Integer> {


    Optional<DiscountCode> findById(Integer id);

    Optional<DiscountCode> findByName(String name);

}
