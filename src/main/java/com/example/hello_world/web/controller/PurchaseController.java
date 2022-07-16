package com.example.hello_world.web.controller;

import com.example.hello_world.CourseStatus;
import com.example.hello_world.DiscountType;
import com.example.hello_world.persistence.model.*;
import com.example.hello_world.persistence.repository.*;
import com.example.hello_world.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RestController
public class PurchaseController {

    private final String DISCOUNT_PATTERN = "[a-zA-Z0-9]+";

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseOwnedRepository courseOwnedRepository;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    @Autowired
    DiscountCodeUsedRepository discountCodeUsedRepository;



    @PostMapping("/purchase/useDiscountCode")
    public Map<String, Object> useDiscountCode(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName") String codeName) {
        Date now = new Date(System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName));
        map.put("discount", null);
        map.put("newPrice", null);

        Pattern pattern = Pattern.compile(DISCOUNT_PATTERN);
        Matcher matcher = pattern.matcher(codeName);

        Optional<Course> course = courseRepository.findById(courseId);
        Optional<DiscountCode> discountCode = discountCodeRepository.findByName(codeName);



        if (courseId < 1 || course.isEmpty()) {
            map.replace("message", "Błąd: Nie odnaleziono podanego kursu.");
            return map;
        }

        if (codeName == null || codeName.isEmpty()) return map;
        if (!matcher.matches()) return map;
        if (discountCode.isEmpty()) return map;

        if (now.compareTo(discountCode.get().getExpiryDate()) > 0) {
            map.replace("message", "Błąd: Podany kod utracił ważność.");
            return map;
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            map.put("success", false);
            map.replace("message", "Błąd: Zakup kursu możliwy jedynie dla zalogowanych użytkowników.");
        }

        MyUserDetails currentUserName = (MyUserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByEmail(currentUserName.getUsername());
        if (user.isPresent()) {
            double discount = 0.0;
            double newPrice = 0;
            double coursePrice = course.get().getPricePromotion() > 0 ? course.get().getPricePromotion() : course.get().getPrice();

            //Calculate discount
            if (discountCode.get().getType() == DiscountType.PERCENTAGE) {
                double value = discountCode.get().getValue();
                discount = (value/100) * coursePrice;
                newPrice = coursePrice - discount;
            }

            if (discountCode.get().getType() == DiscountType.VALUE) {
                discount = discountCode.get().getValue();
                newPrice = coursePrice - discount;
            }

            DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();
            discountCodeUsed.setDiscountCode(discountCode.get());
            discountCodeUsed.setUser(user.get());
            discountCodeUsed.setDate(now);
            discountCodeUsedRepository.save(discountCodeUsed);

            map.replace("success", true);
            map.replace("message", String.format("Sukces: Użyto kodu rabatowego %s!", codeName));
            map.replace("discount", discount);
            map.replace("newPrice", newPrice);
            map.put("title", discountCode.get().getTitle());
            return map;
        }


        map.replace("success", false);
        map.replace("message", "Wystąpił nieoczekiwany błąd ;c");
        return map;
    }











    @GetMapping("/purchase/buyCourse")
    public Map<String, Object> buyCourse() {
        Map<String, Object> map = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails currentUserName = (MyUserDetails) authentication.getPrincipal();
            Optional<User> user = userRepository.findByEmail(currentUserName.getUsername());
            if (user.isPresent()) {
                Optional<Course> course = courseRepository.findById(1);

                CourseOwned courseOwned = new CourseOwned();
                courseOwned.setCourse(course.get());
                courseOwned.setUser(user.get());



                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date(System.currentTimeMillis()));
                calendar.add(Calendar.DATE, 90);
                Date expiryDate = calendar.getTime();


                courseOwned.setBuyDate(new Date(System.currentTimeMillis()));
                courseOwned.setExpiryDate(expiryDate);
                courseOwned.setStatus(CourseStatus.NEW);
                courseOwnedRepository.save(courseOwned);

                map.put("success", true);
                map.put("message", "Świetnie!");
                return map;
            }
        }


        map.put("success", true);
        map.put("message", "Wystąpił nieoczekiwany błąd ;c");
        return map;
    }
}
