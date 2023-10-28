package com.example.onlinebookstore.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    private String title;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
}
