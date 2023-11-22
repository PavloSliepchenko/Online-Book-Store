package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.example.onlinebookstore.exception.RegistrationException;
import com.example.onlinebookstore.mapper.UserMapper;
import com.example.onlinebookstore.model.Role;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.RoleRepository;
import com.example.onlinebookstore.repository.UserRepository;
import com.example.onlinebookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role.RoleName DEFAULT_ROLE = Role.RoleName.USER;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private Set<Role> userRole;

    @Override
    public UserDto save(UserRegistrationRequestDto registrationRequestDto) {
        if (userRepository.findByEmail(registrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("The user with email "
                    + registrationRequestDto.getEmail()
                    + " already exists");
        }

        if (userRole == null) {
            userRole = initDefaultRole();
        }

        User user = userMapper.toModel(registrationRequestDto);
        user.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        user.setRoles(userRole);
        return userMapper.toDto(userRepository.save(user));
    }

    private Set<Role> initDefaultRole() {
        Role role = roleRepository.findByName(DEFAULT_ROLE);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        return roleSet;
    }
}
