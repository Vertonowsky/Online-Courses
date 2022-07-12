package com.example.hello_world.web.service;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.validation.UserAlreadyExistsException;
import com.example.hello_world.web.dto.UserDto;

public interface IUserService {

    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException;

}
