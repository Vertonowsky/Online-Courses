package com.example.vertonowsky.discount;

import com.example.vertonowsky.discount.model.DiscountCode;

public class DiscountCodeSerializer {

    public static DiscountCodeDto serialize(DiscountCode discountCode) {
        DiscountCodeDto dto = new DiscountCodeDto();
        dto.setExpiryDate(discountCode.getExpiryDate());
        dto.setName(discountCode.getName());
        dto.setTitle(discountCode.getTitle());
        dto.setType(discountCode.getType());
        dto.setValue(discountCode.getValue());
        return dto;
    }

}
