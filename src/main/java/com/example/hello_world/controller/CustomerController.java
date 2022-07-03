package com.example.hello_world.controller;

import com.example.hello_world.object.Customer;
import com.example.hello_world.object.CustomereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {


    @Autowired
    private CustomereRepository customereRepository;


    @PostMapping("/add")
    public String addCustomer(@RequestParam String first, @RequestParam String last) {
        Customer customer = new Customer();
        customer.setFirstName(first);
        customer.setLastName(last);
        customereRepository.save(customer);
        return "Added new customer to repository!";
    }


    @GetMapping("/list")
    public Iterable<Customer> getCustomers() {
        return customereRepository.findAll();
    }


    @GetMapping("/find/{id}")
    public Customer findCustomerById(@PathVariable Integer id) {
        return customereRepository.findCustomerById(id);
    }




}
