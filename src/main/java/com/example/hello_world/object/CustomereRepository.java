package com.example.hello_world.object;

import org.springframework.data.repository.CrudRepository;

public interface CustomereRepository extends CrudRepository<Customer, Integer> {

    Customer findCustomerById(Integer id);

}
