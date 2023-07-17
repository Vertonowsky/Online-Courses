package com.example.vertonowsky.discount.repository;

import com.example.vertonowsky.discount.model.DiscountCodeUsed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountCodeUsedRepository extends JpaRepository<DiscountCodeUsed, Integer> {

    Optional<DiscountCodeUsed> findById(Integer id);

}
