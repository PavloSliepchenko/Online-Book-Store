package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.userdtos.UserDto;
import com.example.onlinebookstore.dto.userdtos.UserRegistrationRequestDto;

public interface UserService {
    UserDto save(UserRegistrationRequestDto registrationRequestDto);
}
