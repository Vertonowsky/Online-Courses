package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {


}
