package com.example.onlinebookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotEmpty
        @NotNull
        @Email
        String email,
        @NotNull
        @NotEmpty
        @Length(min = 5, max = 15)
        String password
) {
}
