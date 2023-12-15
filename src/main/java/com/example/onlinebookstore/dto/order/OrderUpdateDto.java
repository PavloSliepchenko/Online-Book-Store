package com.example.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderUpdateDto {
    @NotEmpty
    private String status;
}
