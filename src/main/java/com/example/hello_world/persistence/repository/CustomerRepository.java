package com.example.hello_world.persistence.repository;

import com.example.hello_world.persistence.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Customer findCustomerById(Integer id);

}
