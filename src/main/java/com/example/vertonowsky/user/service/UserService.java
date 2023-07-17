package com.example.vertonowsky.user.service;

import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.role.Role;
import com.example.vertonowsky.role.RoleService;
import com.example.vertonowsky.role.RoleType;
import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.token.repository.PasswordRecoveryTokenRepository;
import com.example.vertonowsky.token.service.VerificationTokenService;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserDto;
import com.example.vertonowsky.user.UserRepository;
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


    @Override
    public void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException, PasswordsNotEqualException, TermsNotAcceptedException {
        if (emailExists(userDto.getEmail()))
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


    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}

