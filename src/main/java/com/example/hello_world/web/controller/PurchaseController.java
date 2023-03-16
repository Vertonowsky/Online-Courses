package com.example.hello_world.web.controller;

import com.example.hello_world.CourseStatus;
import com.example.hello_world.DiscountType;
import com.example.hello_world.persistence.model.*;
import com.example.hello_world.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String DISCOUNT_PATTERN = "[a-zA-Z0-9]*";

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

    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;



    @PostMapping("/purchase/useDiscountCode")
    public Map<String, Object> useDiscountCode(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName") String codeName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> map = verifyPaymentDetails(courseId, codeName, true);
        map.put("discount", null);
        map.put("newPrice", null);

        if (map.get("success").equals(false) || !(User.isLoggedIn(auth))) return map;


        Optional<Course> course = courseRepository.findById(courseId);
        Optional<DiscountCode> discountCode = discountCodeRepository.findByName(codeName);
        Optional<User> user = userRepository.findByEmail(User.getEmail(auth));

        if (course.isEmpty() || discountCode.isEmpty() || user.isEmpty()) {
            map.replace("success", false);
            map.replace("message", "Wystąpił nieoczekiwany błąd ;c");
            return map;
        }


        Map<String, Double> prices = calculateDiscountPrice(course.get(), discountCode.get());

        map.replace("success", true);
        map.replace("message", String.format("Sukces: Użyto kodu rabatowego %s!", codeName));
        if (prices.get("discount") != null) map.replace("discount", prices.get("discount"));
        if (prices.get("newPrice") != null) map.replace("newPrice", prices.get("newPrice"));
        map.put("title", discountCode.get().getTitle());
        return map;
    }











    @PostMapping("/purchase/verifyPayment")
    public Map<String, Object> verifyPayment(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName", defaultValue = "") String codeName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> map = verifyPaymentDetails(courseId, codeName, !codeName.isEmpty());
        map.put("discount", null);
        map.put("newPrice", null);

        if (map.get("success").equals(false) || !(User.isLoggedIn(auth))) return map;


        Optional<Course> course = courseRepository.findById(courseId);
        Optional<DiscountCode> discountCode = !codeName.isEmpty() ? discountCodeRepository.findByName(codeName) : Optional.empty();
        Optional<User> user = userRepository.findByEmail(User.getEmail(auth));

        if (course.isEmpty() || user.isEmpty()) {
            map.replace("success", false);
            map.replace("message", "Wystąpił nieoczekiwany błąd ;c");
            return map;
        }

        Map<String, Double> prices = calculateDiscountPrice(course.get(), discountCode.orElse(null));
        Optional<CourseOwned> courseOwned = courseOwnedRepository.findIfAlreadyBought(user.get(), course.get());


        Calendar calendar = new GregorianCalendar();
        Date now = new Date(System.currentTimeMillis());

        //user has bought this course before
        if (courseOwned.isPresent()) {
            Date expiryDate = courseOwned.get().getExpiryDate();

            //check if course is still active (yes, it is)
            if (now.compareTo(expiryDate) < 0) {

                calendar.setTime(expiryDate);
                calendar.add(Calendar.DATE, 90);

                courseOwnedRepository.updateExistingCourseExpiration(user.get(), course.get(), now, calendar.getTime());


            //Otherways, when course isn't active anymore
            } else {

                calendar.setTime(now);
                calendar.add(Calendar.DATE, 90);

                courseOwnedRepository.updateExistingCourseExpiration(user.get(), course.get(), now, calendar.getTime());

            }


        } else {

            calendar.setTime(now);
            calendar.add(Calendar.DATE, 90);
            Date expiryDate = calendar.getTime();

            CourseOwned courseOwn = new CourseOwned();
            courseOwn.setStatus(CourseStatus.NEW);
            courseOwn.setBuyDate(now);
            courseOwn.setExpiryDate(expiryDate);
            courseOwn.setCourse(course.get());
            courseOwn.setUser(user.get());

            courseOwnedRepository.save(courseOwn);
        }


        //if user used any discount code then save this to database -> validation was at the start of the function
        if (discountCode.isPresent()) {
            DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();
            discountCodeUsed.setDiscountCode(discountCode.get());
            discountCodeUsed.setUser(user.get());
            discountCodeUsed.setDate(now);
            discountCodeUsedRepository.save(discountCodeUsed);
        }


        //Save payment request in database
        PaymentHistory paymentHistory = new PaymentHistory(now, prices.get("newPrice"), "PLN", true);
        paymentHistory.setCourse(course.get());
        paymentHistory.setUser(user.get());
        paymentHistoryRepository.save(paymentHistory);



        map.replace("success", true);
        map.replace("message", String.format("Sukces: Zakupiono kurs %s!", course.get().getName()));
        if (prices.get("discount") != null) map.replace("discount", prices.get("discount"));
        if (prices.get("newPrice") != null) map.replace("newPrice", prices.get("newPrice"));
        return map;
    }














    private Map<String, Object> verifyPaymentDetails(Integer courseId, String codeName, boolean usingDiscount) {
        Date now = new Date(System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName));

        Optional<Course> course = courseRepository.findById(courseId);
        if (courseId < 1 || course.isEmpty()) {
            map.replace("message", "Błąd: Nie odnaleziono podanego kursu.");
            return map;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth)) {
            map.put("success", false);
            map.put("message", "Błąd: Zakup kursu możliwy jedynie dla zalogowanych użytkowników.");
            return map;
        }

        if (usingDiscount) {
            Pattern pattern = Pattern.compile(DISCOUNT_PATTERN);
            Matcher matcher = pattern.matcher(codeName);

            Optional<DiscountCode> discountCode = discountCodeRepository.findByName(codeName);

            if (codeName.isEmpty() || !matcher.matches() || discountCode.isEmpty()) return map;

            if (now.compareTo(discountCode.get().getExpiryDate()) > 0) {
                map.replace("message", "Błąd: Podany kod utracił ważność.");
                return map;
            }
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
