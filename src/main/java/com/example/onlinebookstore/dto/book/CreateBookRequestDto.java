package com.example.onlinebookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String isbn;
    @NotNull
    @Min(value = 0)
    private BigDecimal price;
    private String description;
    private String coverImage;
    private List<Long> categoryIds;
}
