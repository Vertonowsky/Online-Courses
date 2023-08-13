package com.example.vertonowsky.user.service;

import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.role.Role;
import com.example.vertonowsky.role.RoleService;
import com.example.vertonowsky.role.RoleType;
import com.example.vertonowsky.security.model.CustomOidcUser;
import com.example.vertonowsky.security.model.CustomUserDetails;
import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.token.repository.PasswordRecoveryTokenRepository;
import com.example.vertonowsky.token.service.VerificationTokenService;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserDto;
import com.example.vertonowsky.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    private final VerificationTokenService verificationTokenService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, VerificationTokenService verificationTokenService, PasswordEncoder passwordEncoder, PasswordRecoveryTokenRepository passwordRecoveryTokenRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.verificationTokenService = verificationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.passwordRecoveryTokenRepository = passwordRecoveryTokenRepository;
        this.roleService = roleService;
    }


    public User get(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User get(Authentication authentication) {
        String email = getEmail(authentication);
        if (email == null) return null;

        return userRepository.findByEmail(getEmail(authentication)).orElse(null);
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


    @Override
    public void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException, PasswordsNotEqualException, TermsNotAcceptedException {
        if (get(userDto.getEmail()) != null)
            throw new UserAlreadyExistsException(String.format("Konto o adresie %s już istnieje!", userDto.getEmail()));

        if (!UserDto.isEmailValid(userDto.getEmail()))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        if (!UserDto.isPasswordValid(userDto.getPassword()))
            throw new InvalidPasswordFormatException("Hasło nie spełnia wymagań bezpieczeństwa.");

        if (!userDto.arePasswordsEqual())
            throw new PasswordsNotEqualException("Podane hasła nie są identyczne.");

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

        user.setRegistrationDate(new Date(System.currentTimeMillis()));
        user.setRegistrationMethod(userDto.getRegistrationMethod());
        userRepository.save(user);

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

            Date now = new Date(System.currentTimeMillis());
            if (now.compareTo(item.getExpiryDate()) < 0) return true;
        }
        return false;
    }

}

