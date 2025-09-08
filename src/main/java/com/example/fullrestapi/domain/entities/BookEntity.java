package com.example.fullrestapi.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    private String isbn;

    private String title;
    private Boolean published;            // boolean
    private Integer pages;                // integer
    private BigDecimal price;             // big decimal price

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private AuthorEntity author;          // nested author

    @ElementCollection
    private List<String> tags;            // list of strings

    @ElementCollection
    private List<Double> ratings;         // list of doubles
}
