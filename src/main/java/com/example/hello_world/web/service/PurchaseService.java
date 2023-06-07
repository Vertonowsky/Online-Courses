package com.example.hello_world.web.service;

import com.example.hello_world.CourseStatus;
import com.example.hello_world.DiscountType;
import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.*;
import com.example.hello_world.persistence.repository.*;
import com.example.hello_world.validation.CourseNotFoundException;
import com.example.hello_world.validation.DiscountCodeExpiredException;
import com.example.hello_world.validation.DiscountCodeNotFoundException;
import com.example.hello_world.validation.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PurchaseService {


    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final CourseOwnedRepository courseOwnedRepository;
    private final DiscountCodeUsedRepository discountCodeUsedRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public PurchaseService(UserRepository userRepository, CourseRepository courseRepository, DiscountCodeRepository discountCodeRepository, CourseOwnedRepository courseOwnedRepository, DiscountCodeUsedRepository discountCodeUsedRepository, PaymentHistoryRepository paymentHistoryRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.courseOwnedRepository = courseOwnedRepository;
        this.discountCodeUsedRepository = discountCodeUsedRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }





    /**
     * Use discount code and calculate the discount
     *
     * @param email user's email
     * @param courseId id of the course
     * @param codeName name of the discount code
     * @param usingDiscount boolean. True if user provided a discount code
     * @return Map of objects: success, message, title, discount, newPrice
     * @throws CourseNotFoundException Thrown when couldn't find a course with specified courseId
     * @throws DiscountCodeNotFoundException Thrown when couldn't find a discount code with specified codeName
     * @throws DiscountCodeExpiredException Thrown when discount code has expired
     * @throws UserNotFoundException Thrown when couldn't find a user with specified email
     */
    public Map<String, Object> useDiscoundCode(String email, Integer courseId, String codeName, boolean usingDiscount) throws CourseNotFoundException, DiscountCodeNotFoundException, DiscountCodeExpiredException, UserNotFoundException {
        Map<String, Object> data = verifyData(email, courseId, codeName, usingDiscount);

        Course course = (Course) data.get("course");
        DiscountCode discountCode = (DiscountCode) data.get("discountCode");

        Map<String, Object> response = calculateDiscountPrice(course, discountCode);
        response.put("success", true);
        response.put("message", String.format("Sukces: Użyto kodu rabatowego %s!", codeName));
        response.put("title", discountCode.getTitle());

        return response;
    }





    /**
     *  Finalize the payment. Validate payment details
     *
     * @param email user's email
     * @param courseId id of the course
     * @param codeName name of the discount code
     * @param usingDiscount boolean. True if user provided a discount code
     * @return Map of objects: success, message, title, discount, newPrice
     * @throws CourseNotFoundException Thrown when couldn't find a course with specified courseId
     * @throws DiscountCodeNotFoundException Thrown when couldn't find a discount code with specified codeName
     * @throws DiscountCodeExpiredException Thrown when discount code has expired
     * @throws UserNotFoundException Thrown when couldn't find a user with specified email
     */
    public Map<String, Object> finalizePayment(String email, Integer courseId, String codeName, boolean usingDiscount) throws CourseNotFoundException, DiscountCodeNotFoundException, DiscountCodeExpiredException, UserNotFoundException {
        Map<String, Object> data = verifyData(email, courseId, codeName, usingDiscount);

        User user = (User) data.get("user");
        Course course = (Course) data.get("course");
        DiscountCode discountCode = (DiscountCode) data.get("discountCode");

        return buyCourse(user, course, discountCode);
    }





    /**
     * Verifies provided data
     *
     * @param email user's email
     * @param courseId id of the course
     * @param codeName name of the discount code
     * @param usingDiscount boolean. True if user provided a discount code
     * @return Map of objects: user, course, discountCode
     * @throws CourseNotFoundException Thrown when couldn't find a course with specified courseId
     * @throws DiscountCodeNotFoundException Thrown when couldn't find a discount code with specified codeName
     * @throws DiscountCodeExpiredException Thrown when discount code has expired
     * @throws UserNotFoundException Thrown when couldn't find a user with specified email
     */
    public Map<String, Object> verifyData(String email, Integer courseId, String codeName, boolean usingDiscount) throws CourseNotFoundException, DiscountCodeNotFoundException, DiscountCodeExpiredException, UserNotFoundException {
        Map<String, Object> response = new HashMap<>();
        Date now = new Date(System.currentTimeMillis());

        if (courseId < 1) throw new CourseNotFoundException("Błąd: Nie odnaleziono podanego kursu.");
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) throw new CourseNotFoundException("Błąd: Nie odnaleziono podanego kursu.");

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException("Błąd: Nie odnaleziono użytkownika");


        if (usingDiscount) {
            if (codeName.isEmpty() || !Regex.DISCOUNT_PATTERN.matches(codeName)) throw new DiscountCodeNotFoundException(String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName));
            Optional<DiscountCode> discountCode = discountCodeRepository.findByName(codeName);
            if (discountCode.isEmpty()) throw new DiscountCodeNotFoundException(String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName));

            if (now.compareTo(discountCode.get().getExpiryDate()) > 0) throw new DiscountCodeExpiredException("Błąd: Podany kod utracił ważność.");

            response.put("discountCode", discountCode.get());
        }

        response.put("course", course.get());
        response.put("user", user.get());
        return response;
    }





    /**
     * Calculate how big discount shold be, based on course price
     *
     * @param course course object which price we want to reduce
     * @param discountCode provided by user discount code
     * @return Map of objects: discount, newPrice
     */
    public Map<String, Object> calculateDiscountPrice(Course course, DiscountCode discountCode) {
        Double coursePrice = course.getPricePromotion() > 0 ? course.getPricePromotion() : course.getPrice();
        Map<String, Object> map = new HashMap<>();
        if (discountCode == null) {
            map.put("discount", 0.0);
            map.put("newPrice", coursePrice);
            return map;
        }

        Double discount = 0.0;
        double newPrice = 0.0;

        //Calculate discount
        if (discountCode.getType() == DiscountType.PERCENTAGE) {
            double value = discountCode.getValue();
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





    /**
     * Increase course duration for user by 3 months
     *
     * @param user user object
     * @param course course which is being bought
     * @param discountCode discount code used during the transcation
     * @return map of objects: success, message, discount, newPrice
     */
    public Map<String, Object> buyCourse(User user, Course course, DiscountCode discountCode) {
        Map<String, Object> response = new HashMap<>();
        Optional<CourseOwned> courseOwned = courseOwnedRepository.findIfAlreadyBought(user, course);

        Calendar calendar = new GregorianCalendar();
        Date now = new Date(System.currentTimeMillis());

        // User has bought this course before
        if (courseOwned.isPresent()) {
            Date expiryDate = courseOwned.get().getExpiryDate();

            // Check if course is still active (yes, it is)
            if (now.compareTo(expiryDate) < 0)
                calendar.setTime(expiryDate);

            // Otherways, when course isn't active anymore
            else
                calendar.setTime(now);

            calendar.add(Calendar.DATE, 90);
            courseOwnedRepository.updateExistingCourseExpiration(user, course, now, calendar.getTime());

        } else {

            calendar.setTime(now);
            calendar.add(Calendar.DATE, 90);
            Date expiryDate = calendar.getTime();

            CourseOwned courseOwn = new CourseOwned();
            courseOwn.setStatus(CourseStatus.NEW);
            courseOwn.setBuyDate(now);
            courseOwn.setExpiryDate(expiryDate);
            courseOwn.setCourse(course);
            courseOwn.setUser(user);

            courseOwnedRepository.save(courseOwn);
        }


        // If user used any discount code then save this to database -> validation was at the start of the function
        if (discountCode != null) {
            DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();
            discountCodeUsed.setDiscountCode(discountCode);
            discountCodeUsed.setUser(user);
            discountCodeUsed.setDate(now);
            discountCodeUsedRepository.save(discountCodeUsed);
        }

        Map<String, Object> prices = calculateDiscountPrice(course, discountCode);

        // Save payment request in database
        PaymentHistory paymentHistory = new PaymentHistory(now, (Double)(prices.get("newPrice")), "PLN", true);
        paymentHistory.setCourse(course);
        paymentHistory.setUser(user);
        paymentHistoryRepository.save(paymentHistory);

        // Generate response
        response.put("success", true);
        response.put("message", String.format("Sukces: Zakupiono kurs %s!", course.getName()));
        response.put("discount", prices.get("discount"));
        response.put("newPrice", prices.get("newPrice"));
        return response;
    }

}
