package com.example.vertonowsky.discount;


import com.example.vertonowsky.discount.model.DiscountCode;
import com.example.vertonowsky.discount.repository.DiscountCodeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class DiscountCodeController {


    private final DiscountCodeRepository discountCodeRepository;

    public DiscountCodeController(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }


    /**
     * Function adding new discount code do the database
     *
     * @return information if code was successfully added
     */
    @GetMapping("/add-discount-code")
    public String addDiscountCode() {
        Date now = new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000 * 10);
        DiscountCode discountCode = new DiscountCode("jesien30", "jesien30 | KOD RABATOWY -30%", DiscountType.PERCENTAGE, 30.0, now);

        discountCodeRepository.save(discountCode);
        return "Added new discount code to repository!";
    }

    /*@GetMapping("/list-discount-code")
    public Iterable<DiscountCode> getDiscountCodes() {
        return discountCodeRepository.findAll();
    }

    @GetMapping("/list-used-discount-code")
    public Iterable<DiscountCodeUsed> getDiscountCodesUsed() {
        return discountCodeUsedRepository.findAll();
    }*/


}
