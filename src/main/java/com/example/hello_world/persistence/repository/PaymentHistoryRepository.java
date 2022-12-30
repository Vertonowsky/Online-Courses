package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.PaymentHistory;
import org.springframework.data.repository.CrudRepository;

public interface PaymentHistoryRepository extends CrudRepository<PaymentHistory, Integer> {


}
