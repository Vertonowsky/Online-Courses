package com.example.hello_world.web.controller;


import com.example.hello_world.DiscountType;
import com.example.hello_world.persistence.model.DiscountCode;
import com.example.hello_world.persistence.model.DiscountCodeUsed;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.DiscountCodeRepository;
import com.example.hello_world.persistence.repository.DiscountCodeUsedRepository;
import com.example.hello_world.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
public class DiscountCodeController {


    @Autowired
    DiscountCodeRepository discountCodeRepository;

    @Autowired
    DiscountCodeUsedRepository discountCodeUsedRepository;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/add-discount-code")
    public String addDiscountCode() {
        Date now = new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000 * 10);
        DiscountCode discountCode = new DiscountCode("jesien30", "jesien30 | KOD RABATOWY -30%", DiscountType.PERCENTAGE, 30.0, now);

        discountCodeRepository.save(discountCode);
        return "Added new discount code to repository!";
    }



    @GetMapping("/use-discount-code")
    public String useDiscountCode() {
        DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();

        Optional<DiscountCode> discountCode = discountCodeRepository.findById(1);
        discountCodeUsed.setDiscountCode(discountCode.get());

        Optional<User> user = userRepository.findById(2);
        discountCodeUsed.setUser(user.get());

        Date now = new Date(System.currentTimeMillis());
        discountCodeUsed.setDate(now);
        discountCodeUsedRepository.save(discountCodeUsed);
        return "Used discount code with ID = 1";
    }




    @GetMapping("/list-discount-code")
    public Iterable<DiscountCode> getDiscountCodes() {
        return discountCodeRepository.findAll();
    }

    @GetMapping("/list-used-discount-code")
    public Iterable<DiscountCodeUsed> getDiscountCodesUsed() {
        return discountCodeUsedRepository.findAll();
    }


}
