package com.example.hello_world.web.controller;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.web.service.PurchaseService;
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
public class PurchaseController {

    private final PurchaseService purchaseService;


    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }



    @PostMapping("/purchase/useDiscountCode")
    public Map<String, Object> useDiscountCode(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName") String codeName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth)) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "Błąd: Zakup kursu możliwy jedynie dla zalogowanych użytkowników.");
            return map;
        }

        try {

            // Validate data given by user
            return purchaseService.useDiscoundCode(User.getEmail(auth), courseId, codeName, true);

        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
    }







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
            return purchaseService.finalizePayment(User.getEmail(auth), courseId, codeName, !codeName.isEmpty());

        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
    }

}
