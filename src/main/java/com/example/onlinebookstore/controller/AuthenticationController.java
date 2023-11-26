package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.dto.user.UserLoginRequestDto;
import com.example.onlinebookstore.dto.user.UserLoginResponseDto;
import com.example.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.example.onlinebookstore.security.AuthenticationService;
import com.example.onlinebookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
@Tag(name = "Authentication", description = "End points for registration and login")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/registration")
    @Operation(summary = "Register", description = "Saves a new user to DB")
    public UserDto registerUser(@RequestBody @Valid UserRegistrationRequestDto request) {
        return userService.save(request);
    }

    @PostMapping(value = "/login")
    @Operation(summary = "Login",
            description = "Checks if there is such user in DB and returns a token")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
