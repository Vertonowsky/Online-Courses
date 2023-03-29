package com.example.hello_world.web.service;

import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.UserDto;

public interface IUserService {

    void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException, PasswordsNotEqualException, TermsNotAcceptedException;

}
