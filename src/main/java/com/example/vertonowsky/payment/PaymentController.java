package com.example.vertonowsky.payment;

import com.example.vertonowsky.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
public class PaymentController {

    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    /**
     * Add the discount code to the current transaction
     *
     * @param courseId id of the course
     * @param codeName name of the used discount code
     * @return Map of objects with success state and calculated discount amount
     */
    @PostMapping("/purchase/useDiscountCode")
    public Map<String, Object> useDiscountCode(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName") String codeName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth)) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "Błąd: Kody rabatowe dostępne są jedynie dla zalogowanych użytkowników.");
            return map;
        }

        try {

            // Validate data given by user
            return paymentService.useDiscoundCode(User.getEmail(auth), courseId, codeName, true);

        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
    }


    /**
     * Validate and process the payment
     *
     * @param courseId id of the course
     * @param codeName name of the used discount code
     * @return Map of objects with success state and calculated discount amount
     */
    @PostMapping("/purchase/verifyPayment")
    public Map<String, Object> finalizePayment(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName", defaultValue = "") String codeName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth)) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "Błąd: Zakup kursu możliwy jedynie dla zalogowanych użytkowników.");
            return map;
        }

        try {

            // Validate data given by user
            return paymentService.finalizePayment(User.getEmail(auth), courseId, codeName, !codeName.isEmpty());

        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
    }

}
