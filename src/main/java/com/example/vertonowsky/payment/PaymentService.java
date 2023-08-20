package com.example.vertonowsky.payment;

import com.example.vertonowsky.Regex;
import com.example.vertonowsky.course.CourseDto;
import com.example.vertonowsky.course.CourseSerializer;
import com.example.vertonowsky.course.CourseService;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.course.repository.CourseOwnedRepository;
import com.example.vertonowsky.discount.DiscountCodeDto;
import com.example.vertonowsky.discount.DiscountCodeSerializer;
import com.example.vertonowsky.discount.DiscountCodeService;
import com.example.vertonowsky.discount.DiscountType;
import com.example.vertonowsky.discount.model.DiscountCode;
import com.example.vertonowsky.exception.CourseNotFoundException;
import com.example.vertonowsky.exception.DiscountCodeExpiredException;
import com.example.vertonowsky.exception.DiscountCodeNotFoundException;
import com.example.vertonowsky.exception.UserNotFoundException;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserSerializer;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

import static com.example.vertonowsky.course.CourseSerializer.Task.BASE;
import static com.example.vertonowsky.course.CourseSerializer.Task.PRICE;


@Service
public class PaymentService {

    private final CourseOwnedRepository courseOwnedRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CourseService courseService;
    private final DiscountCodeService discountCodeService;
    private final UserService userService;

    public PaymentService(CourseOwnedRepository courseOwnedRepository, PaymentHistoryRepository paymentHistoryRepository, CourseService courseService, DiscountCodeService discountCodeService, UserService userService) {
        this.courseOwnedRepository = courseOwnedRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.courseService = courseService;
        this.discountCodeService = discountCodeService;
        this.userService = userService;
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
    public PaymentDto useDiscountCode(String email, Integer courseId, String codeName, boolean usingDiscount) throws CourseNotFoundException, DiscountCodeNotFoundException, DiscountCodeExpiredException, UserNotFoundException {
        PaymentDto paymentDto = verifyData(email, courseId, codeName, usingDiscount);
        calculateDiscountPrice(paymentDto);
        return paymentDto;
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
    public PaymentDto finalizePayment(String email, Integer courseId, String codeName, boolean usingDiscount) throws CourseNotFoundException, DiscountCodeNotFoundException, DiscountCodeExpiredException, UserNotFoundException {
        PaymentDto paymentDto = verifyData(email, courseId, codeName, usingDiscount);
        calculateDiscountPrice(paymentDto);

        User user = userService.get(email);
        Course course = courseService.get(courseId);
        DiscountCode discountCode = discountCodeService.get(codeName);

        buyCourse(user, course, discountCode, paymentDto);
        return paymentDto;
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
    public PaymentDto verifyData(String email, Integer courseId, String codeName, boolean usingDiscount) throws CourseNotFoundException, DiscountCodeNotFoundException, DiscountCodeExpiredException, UserNotFoundException {
        PaymentDto paymentDto = new PaymentDto();
        OffsetDateTime now = OffsetDateTime.now();

        if (courseId < 1) throw new CourseNotFoundException("Błąd: Nie odnaleziono podanego kursu.");
        Course course = courseService.get(courseId);
        if (course == null) throw new CourseNotFoundException("Błąd: Nie odnaleziono podanego kursu.");

        User user = userService.get(email);
        if (user == null) throw new UserNotFoundException("Błąd: Nie odnaleziono użytkownika");


        if (usingDiscount) {
            if (codeName.isEmpty() || !Regex.DISCOUNT_PATTERN.matches(codeName)) throw new DiscountCodeNotFoundException(String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName));
            DiscountCode discountCode = discountCodeService.get(codeName);
            if (discountCode == null) throw new DiscountCodeNotFoundException(String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName));

            if (now.isAfter(discountCode.getExpiryDate())) throw new DiscountCodeExpiredException("Błąd: Podany kod utracił ważność.");

            paymentDto.setDiscountCode(DiscountCodeSerializer.serialize(discountCode));
        }

        paymentDto.setCourse(CourseSerializer.serialize(course, BASE, PRICE));
        paymentDto.setUser(UserSerializer.serialize(user, UserSerializer.Task.BASE));

        return paymentDto;
    }





    /**
     * Increase course duration for user by 3 months
     *
     * @param user user object
     * @param course course which is being bought
     * @param discountCode discount code used during the transcation
     * @return map of objects: success, message, discount, newPrice
     */
    public void buyCourse(User user, Course course, DiscountCode discountCode, PaymentDto paymentDto) {
        CourseOwned courseOwned = courseOwnedRepository.findIfAlreadyBought(user, course).orElse(null);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime till = now;


        // User haven't bought this course before
        if (courseOwned == null)
            courseService.newCourseOwned(user, course, now, now.plusDays(90));


        // User has bought this course before
        else {
            OffsetDateTime expiryDate = courseOwned.getExpiryDate();

            // Check if course is still active (yes, it is)
            if (expiryDate.isAfter(now))
                till = expiryDate;

            courseService.updateExistingCourseExpiration(user, course, now, till.plusDays(90));
        }

        // If user used any discount code, then save it to database -> validation was completed in previous steps
        if (discountCode != null)
            discountCodeService.newDiscountCodeUsed(user, discountCode);


        // Save payment request in database
        newPaymentHistory(user, course, paymentDto.getNewPrice(), "PLN", true);

    }

    /**
     * Calculate how big discount shold be, based on course price
     *
     * @param paymentDto paymentDto object containg information about course and used discount code
     */
    public void calculateDiscountPrice(PaymentDto paymentDto) {
        CourseDto courseDto = paymentDto.getCourse();
        Double coursePrice = courseDto.getPricePromotion() > 0 ? courseDto.getPricePromotion() : courseDto.getPrice();

        DiscountCodeDto discountCodeDto = paymentDto.getDiscountCode();
        if (discountCodeDto == null) {
            paymentDto.setNewPrice(coursePrice);
            return;
        }

        Double discount = 0.0;
        Double newPrice = 0.0;

        //Calculate discount
        if (discountCodeDto.getType() == DiscountType.PERCENTAGE) {
            Double value = discountCodeDto.getValue();
            discount = (value/100) * coursePrice;
            newPrice = coursePrice - discount;
        }

        if (discountCodeDto.getType() == DiscountType.VALUE) {
            discount = discountCodeDto.getValue();
            newPrice = coursePrice - discount;
        }

        paymentDto.setDiscount(discount);
        paymentDto.setNewPrice(newPrice);
    }


    public void newPaymentHistory(User user, Course course, Double price, String currency, boolean success) {
        PaymentHistory paymentHistory = new PaymentHistory(OffsetDateTime.now(), price, currency, success);
        paymentHistory.setCourse(course);
        paymentHistory.setUser(user);

        paymentHistoryRepository.save(paymentHistory);
    }

}
