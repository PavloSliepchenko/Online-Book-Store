package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.userdtos.UserDto;
import com.example.onlinebookstore.dto.userdtos.UserRegistrationRequestDto;
import com.example.onlinebookstore.mapper.UserMapper;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.UserRepository;
import com.example.onlinebookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto save(UserRegistrationRequestDto registrationRequestDto) {
        User user = userMapper.toModel(registrationRequestDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
