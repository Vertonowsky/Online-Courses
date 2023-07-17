package com.example.vertonowsky.user.service;

import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.user.UserDto;

public interface IUserService {

    void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException, PasswordsNotEqualException, TermsNotAcceptedException;

}
