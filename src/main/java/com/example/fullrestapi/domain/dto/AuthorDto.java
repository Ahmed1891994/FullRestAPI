package com.example.fullrestapi.domain.dto;

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
    private String name;
    private Integer age;
    private Boolean active;
    private Double rating;
    private Integer totalBooks;
    private List<String> genres;
    private List<Integer> scores;
    private BigDecimal wealth;
    private BigInteger followers;
}
