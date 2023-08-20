package com.example.vertonowsky.discount.repository;

import com.example.vertonowsky.discount.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Integer> {

    Optional<DiscountCode> findById(Integer id);

    Optional<DiscountCode> findByName(String name);

}
