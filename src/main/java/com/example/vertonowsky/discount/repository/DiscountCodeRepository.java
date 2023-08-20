package com.example.vertonowsky.discount.repository;

import com.example.vertonowsky.discount.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Integer> {

    Optional<DiscountCode> findById(Integer id);

    Optional<DiscountCode> findByName(@Param("name") String name);

}
