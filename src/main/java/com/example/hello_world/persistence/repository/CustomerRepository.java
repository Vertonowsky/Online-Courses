package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Optional<Customer> findById(Integer id);

}
