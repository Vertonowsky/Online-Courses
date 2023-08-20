package com.example.vertonowsky.discount;

import com.example.vertonowsky.discount.model.DiscountCode;
import com.example.vertonowsky.discount.model.DiscountCodeUsed;
import com.example.vertonowsky.discount.repository.DiscountCodeRepository;
import com.example.vertonowsky.discount.repository.DiscountCodeUsedRepository;
import com.example.vertonowsky.user.User;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;
    private final DiscountCodeUsedRepository discountCodeUsedRepository;

    public DiscountCodeService(DiscountCodeRepository discountCodeRepository, DiscountCodeUsedRepository discountCodeUsedRepository) {
        this.discountCodeRepository = discountCodeRepository;
        this.discountCodeUsedRepository = discountCodeUsedRepository;
    }

    public DiscountCode get(String name) {
        return discountCodeRepository.findByName(name).orElse(null);
    }

    public void newDiscountCodeUsed(User user, DiscountCode discountCode) {
        DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();
        discountCodeUsed.setDiscountCode(discountCode);
        discountCodeUsed.setUser(user);
        discountCodeUsed.setDate(OffsetDateTime.now());

        discountCodeUsedRepository.save(discountCodeUsed);
    }

}
