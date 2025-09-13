package com.example.fullrestapi.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private Long id;

    @NotBlank(message = "name must not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "name must not contain special characters")
    private String name;

    @NotNull(message = "age is required")
    @Min(value = 1, message = "age must be at least 1")
    @Max(value = 125, message = "age must not exceed 125")
    private Integer age;

    @NotNull(message = "active is required")
    private Boolean active;

    private Double rating;
    private Integer totalBooks;
    private List<String> genres;
    private List<Integer> scores;
    private BigDecimal wealth;
    private BigInteger followers;
}
