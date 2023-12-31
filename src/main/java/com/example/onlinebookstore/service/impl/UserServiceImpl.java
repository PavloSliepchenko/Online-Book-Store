package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.example.onlinebookstore.exception.RegistrationException;
import com.example.onlinebookstore.mapper.UserMapper;
import com.example.onlinebookstore.model.Role;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.RoleRepository;
import com.example.onlinebookstore.repository.ShoppingCartRepository;
import com.example.onlinebookstore.repository.UserRepository;
import com.example.onlinebookstore.service.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role.RoleName DEFAULT_ROLE = Role.RoleName.USER;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ShoppingCartRepository cartRepository;
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
        User userFromDb = userRepository.save(user);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(userFromDb);
        cartRepository.save(shoppingCart);

        return userMapper.toDto(userFromDb);
    }

    private Set<Role> initDefaultRole() {
        Role role = roleRepository.findByName(DEFAULT_ROLE);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        return roleSet;
    }
}
