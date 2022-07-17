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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> map = verifyPaymentDetails(courseId, codeName, true);
        map.put("discount", null);
        map.put("newPrice", null);

        if (map.get("success").equals(false)) {
            return map;
        }

        Course course = courseRepository.findById(courseId).get();
        DiscountCode discountCode = discountCodeRepository.findByName(codeName).get();
        MyUserDetails currentUserName = (MyUserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByEmail(currentUserName.getUsername());

        if (user.isPresent()) {
            Map<String, Double> prices = calculateDiscountPrice(course, discountCode);

            /*DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();
            discountCodeUsed.setDiscountCode(discountCode);
            discountCodeUsed.setUser(user.get());
            discountCodeUsed.setDate(now);
            discountCodeUsedRepository.save(discountCodeUsed);*/

            map.replace("success", true);
            map.replace("message", String.format("Sukces: Użyto kodu rabatowego %s!", codeName));
            if (prices.get("discount") != null) map.replace("discount", prices.get("discount"));
            if (prices.get("newPrice") != null) map.replace("newPrice", prices.get("newPrice"));
            map.put("title", discountCode.getTitle());
            return map;
        }


        map.replace("success", false);
        map.replace("message", "Wystąpił nieoczekiwany błąd ;c");
        return map;
    }



    @PostMapping("/purchase/verifyPayment")
    public Map<String, Object> verifyPayment(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName", defaultValue = "") String codeName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> map = verifyPaymentDetails(courseId, codeName, !codeName.isEmpty());
        map.put("discount", null);
        map.put("newPrice", null);

        if (map.get("success").equals(false)) {
            return map;
        }

        Course course = courseRepository.findById(courseId).get();
        DiscountCode discountCode = !codeName.isEmpty() ? discountCodeRepository.findByName(codeName).get() : null;
        MyUserDetails currentUserName = (MyUserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByEmail(currentUserName.getUsername());

        if (user.isPresent()) {
            Map<String, Double> prices = calculateDiscountPrice(course, discountCode);
            Optional<CourseOwned> courseOwned = courseOwnedRepository.findIfAlreadyBought(user.get(), course);


            Calendar calendar = new GregorianCalendar();
            Date now = new Date(System.currentTimeMillis());

            //user has bought this course before
            if (courseOwned.isPresent()) {
                Date expiryDate = courseOwned.get().getExpiryDate();

                //check if course is still active (yes, it is)
                if (now.compareTo(expiryDate) < 0) {

                    calendar.setTime(expiryDate);
                    calendar.add(Calendar.DATE, 90);

                    courseOwnedRepository.updateExistingCourseExpiration(user.get(), course, now, calendar.getTime());


                //Otherways, when course isn't active anymore
                } else {

                    calendar.setTime(now);
                    calendar.add(Calendar.DATE, 90);

                    courseOwnedRepository.updateExistingCourseExpiration(user.get(), course, now, calendar.getTime());

                }


            } else {

                calendar.setTime(now);
                calendar.add(Calendar.DATE, 90);
                Date expiryDate = calendar.getTime();

                CourseOwned courseOwn = new CourseOwned();
                courseOwn.setStatus(CourseStatus.NEW);
                courseOwn.setBuyDate(now);
                courseOwn.setExpiryDate(expiryDate);
                courseOwn.setCourse(course);
                courseOwn.setUser(user.get());

                courseOwnedRepository.save(courseOwn);
            }


            //if user used any discount code then save this to database -> validation was at the start of the function
            if (!codeName.isEmpty()) {

                DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();
                discountCodeUsed.setDiscountCode(discountCode);
                discountCodeUsed.setUser(user.get());
                discountCodeUsed.setDate(now);
                discountCodeUsedRepository.save(discountCodeUsed);

            }


            map.replace("success", true);
            map.replace("message", String.format("Sukces: Zakupiono kurs %s!", course.getName()));
            if (prices.get("discount") != null) map.replace("discount", prices.get("discount"));
            if (prices.get("newPrice") != null) map.replace("newPrice", prices.get("newPrice"));
            return map;
        }




        map.put("success", true);
        map.put("message", "Wystąpił nieoczekiwany błąd ;c");
        return map;
    }





    private Map<String, Object> verifyPaymentDetails(Integer courseId, String codeName, boolean usingDiscount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Date now = new Date(System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName));


        Optional<Course> course = courseRepository.findById(courseId);
        if (courseId < 1 || course.isEmpty()) {
            map.replace("message", "Błąd: Nie odnaleziono podanego kursu.");
            return map;
        }

        if (usingDiscount) {
            Pattern pattern = Pattern.compile(DISCOUNT_PATTERN);
            Matcher matcher = pattern.matcher(codeName);

            Optional<DiscountCode> discountCode = discountCodeRepository.findByName(codeName);

            if (codeName == null || codeName.isEmpty()) return map;
            if (!matcher.matches()) return map;
            if (discountCode.isEmpty()) return map;

            if (now.compareTo(discountCode.get().getExpiryDate()) > 0) {
                map.replace("message", "Błąd: Podany kod utracił ważność.");
                return map;
            }
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            map.put("success", false);
            map.put("message", "Błąd: Zakup kursu możliwy jedynie dla zalogowanych użytkowników.");
            return map;
        }


        map.replace("success", true);
        map.replace("message", "Sukces");
        return map;
    }




    private Map<String, Double> calculateDiscountPrice(Course course, DiscountCode discountCode) {
        Double coursePrice = course.getPricePromotion() > 0 ? course.getPricePromotion() : course.getPrice();
        Map<String, Double> map = new HashMap<>();
        if (discountCode == null) {
            map.put("discount", 0.0);
            map.put("newPrice", coursePrice);
            return map;
        }

        Double discount = 0.0;
        Double newPrice = 0.0;

        //Calculate discount
        if (discountCode.getType() == DiscountType.PERCENTAGE) {
            Double value = discountCode.getValue();
            discount = (value/100) * coursePrice;
            newPrice = coursePrice - discount;
        }

        if (discountCode.getType() == DiscountType.VALUE) {
            discount = discountCode.getValue();
            newPrice = coursePrice - discount;
        }

        map.put("discount", discount);
        map.put("newPrice", newPrice);
        return map;
    }








}
