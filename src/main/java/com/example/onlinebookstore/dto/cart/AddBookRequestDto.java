package com.example.onlinebookstore.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBookRequestDto {
    @NotNull
    private Long bookId;
    private int quantity;
}
