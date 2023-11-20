package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.example.onlinebookstore.exception.RegistrationException;
import com.example.onlinebookstore.mapper.UserMapper;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.UserRepository;
import com.example.onlinebookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto save(UserRegistrationRequestDto registrationRequestDto) {
        if (userRepository.findByEmail(registrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("The user with email "
                    + registrationRequestDto.getEmail()
                    + " already exists");
        }

        User user = userMapper.toModel(registrationRequestDto);
        user.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }
}
