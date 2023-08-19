package com.example.vertonowsky.user.service;

import com.example.vertonowsky.Collections;
import com.example.vertonowsky.avatar.Avatar;
import com.example.vertonowsky.avatar.AvatarService;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.role.Role;
import com.example.vertonowsky.role.RoleService;
import com.example.vertonowsky.role.RoleType;
import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.security.model.CustomOidcUser;
import com.example.vertonowsky.security.model.CustomUserDetails;
import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.token.repository.PasswordRecoveryTokenRepository;
import com.example.vertonowsky.token.service.VerificationTokenService;
import com.example.vertonowsky.topic.model.FinishedTopic;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserDto;
import com.example.vertonowsky.user.UserQueryType;
import com.example.vertonowsky.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;


@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    private final VerificationTokenService verificationTokenService;
    private final RoleService roleService;
    private final AvatarService avatarService;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordRecoveryTokenRepository passwordRecoveryTokenRepository, VerificationTokenService verificationTokenService, RoleService roleService, AvatarService avatarService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordRecoveryTokenRepository = passwordRecoveryTokenRepository;
        this.verificationTokenService = verificationTokenService;
        this.roleService = roleService;
        this.avatarService = avatarService;
        this.passwordEncoder = passwordEncoder;
    }

    public User get(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User get(Authentication authentication, UserQueryType userQueryType) {
        String email = getEmail(authentication);
        if (email == null) return null;

        if (userQueryType.equals(UserQueryType.BASE))
            return userRepository.findByEmail(email).orElse(null);

        if (userQueryType.equals(UserQueryType.COURSES_OWNED))
            return userRepository.findByEmailWithCoursesOwned(email).orElse(null);

        if (userQueryType.equals(UserQueryType.FINISHED_TOPICS))
            return userRepository.findByEmailWithFinishedTopics(email).orElse(null);

        if (userQueryType.equals(UserQueryType.FINISHED_TOPICS_AND_COURSES_OWNED))
            return userRepository.findByEmailWithFinishedTopicsAndCoursesOwned(email).orElse(null);

        if (userQueryType.equals(UserQueryType.FINISHED_TOPICS_AND_COURSES_OWNED_AND_PAYMENT_HISTORIES))
            return userRepository.findByEmailWithFinishedTopicsAndCoursesOwnedAndPaymentHistories(email).orElse(null);

        if (userQueryType.equals(UserQueryType.PAYMENT_HISTORIES))
            return userRepository.findByEmailWithPaymentHistories(email).orElse(null);

        return userRepository.findByEmailAllData(email).orElse(null);
    }

    public String getEmail(Authentication authentication) {
        if (authentication == null) return null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) return ((CustomUserDetails) authentication.getPrincipal()).getEmail();
        if (authentication.getPrincipal() instanceof CustomOidcUser)    return ((CustomOidcUser)    authentication.getPrincipal()).getEmail();

        return null;
    }

    public boolean isNull(User user) {
        return user == null;
    }

    public boolean isLoggedIn(User user) {
        return !isNull(user);
    }

    public boolean isLoggedIn(Authentication authentication) {
        String email = getEmail(authentication);
        User user = get(email);
        return isLoggedIn(user);
    }


    @Override
    public void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException, PasswordsNotEqualException, TermsNotAcceptedException, InvalidDataException {
        if (get(userDto.getEmail()) != null)
            throw new UserAlreadyExistsException(String.format("Konto o adresie %s już istnieje!", userDto.getEmail()));

        if (!UserDto.isEmailValid(userDto.getEmail()))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        if (userDto.getPassword() != null && userDto.getRegistrationMethod().equals(RegistrationMethod.DEFAULT)) {
            if (!UserDto.isPasswordValid(userDto.getPassword()))
                throw new InvalidPasswordFormatException("Hasło nie spełnia wymagań bezpieczeństwa.");

            if (!userDto.arePasswordsEqual())
                throw new PasswordsNotEqualException("Podane hasła nie są identyczne.");
        }

        if (!userDto.areTermsChecked())
            throw new TermsNotAcceptedException("Wymagana jest akceptacja regulaminu.");


        User user = new User();
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));  //encrypt password with BCrypt
            user.setVerified(false);
        } else {
            user.setPassword(null);
            user.setVerified(true);
        }

        Role role = roleService.createRoleIfNotExists(RoleType.ROLE_USER);
        user.setRole(role);

        Avatar avatar = avatarService.get(1);
        if (avatar == null) throw new InvalidDataException("Nie znaleziono żadnych dostępnych avatarów. Prosimy o kontakt z administracją.");
        user.setAvatar(avatar);

        user.setRegistrationDate(OffsetDateTime.now());
        user.setRegistrationMethod(userDto.getRegistrationMethod());
        userRepository.save(user);

        if (userDto.getRegistrationMethod().equals(RegistrationMethod.DEFAULT))
            // Create new verification token
            verificationTokenService.createTokenAndSendEmail(user);
    }


    public void changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public Optional<User> getUserByPasswordRecoveryToken(UUID tokenUuid) {
        Optional<PasswordRecoveryToken> token = passwordRecoveryTokenRepository.findByToken(tokenUuid);
        if (token.isEmpty()) return Optional.empty();
        return Optional.ofNullable(token.get().getUser());
    }


    /**
     * Check if user's course is valid
     *
     * @param user user object
     * @param course course object
     * @return boolean. True if course is valid.
     */
    public boolean isCourseValid(User user, Course course) {
        for (CourseOwned item : user.getCoursesOwned()) {
            if (!item.getCourse().getId().equals(course.getId())) continue;

            OffsetDateTime now = OffsetDateTime.now();
            if (now.isBefore(item.getExpiryDate())) return true;
        }
        return false;
    }

    public boolean isTopicFinished(Set<FinishedTopic> finishedTopics, Integer topicId) {
        if (Collections.isNullOrEmpty(finishedTopics)) return false;

        return finishedTopics.stream().filter(finishedTopic -> finishedTopic.getTopic().getId().equals(topicId)).count() == 1;
    }

    public List<CourseOwned> sortCoursesOwned(User user) {
        if (user.getCoursesOwned() == null) return null;
        return user.getCoursesOwned().stream().sorted(Comparator.comparing(CourseOwned::getExpiryDate).reversed()).toList();
    }

}

