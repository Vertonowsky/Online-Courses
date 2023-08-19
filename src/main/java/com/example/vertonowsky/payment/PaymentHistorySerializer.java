package com.example.vertonowsky.payment;

public class PaymentHistorySerializer {

    public static PaymentHistoryDto serialize(PaymentHistory paymentHistory) {
        PaymentHistoryDto dto = new PaymentHistoryDto();
        dto.setId(paymentHistory.getId());
        dto.setAmount(paymentHistory.getAmount());
        dto.setCurrency(paymentHistory.getCurrency());
        dto.setCourseName(paymentHistory.getCourse().getName());
        dto.setSuccess(paymentHistory.isSuccess());
        dto.setProceedDate(paymentHistory.getProceedDate());
        return dto;
    }

}
