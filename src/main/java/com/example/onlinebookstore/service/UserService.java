package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.dto.user.UserRegistrationRequestDto;

public interface UserService {
    UserDto save(UserRegistrationRequestDto registrationRequestDto);
}
