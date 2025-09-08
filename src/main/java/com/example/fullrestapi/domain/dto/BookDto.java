package com.example.fullrestapi.domain.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String isbn;
    private String title;
    private Boolean published;
    private Integer pages;
    private BigDecimal price;
    private AuthorDto author;       // Nested DTO
    private List<String> tags;
    private List<Double> ratings;
}
