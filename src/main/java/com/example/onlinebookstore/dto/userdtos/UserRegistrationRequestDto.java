package com.example.onlinebookstore.dto.userdtos;

import com.example.onlinebookstore.validator.FieldMatch;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords do not match!"
        )
public class UserRegistrationRequestDto {
    @NotNull
    private String email;
    @NotNull
    @Length(min = 5, max = 15)
    private String password;
    @NotNull
    @Length(min = 5, max = 15)
    private String repeatPassword;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String shippingAddress;
}
