package com.example.fullrestapi.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authors")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ simpler & works best with PostgreSQL
    private Long id;

    private String name;
    private Integer age;
    private Boolean active;               // boolean field
    private Double rating;                // double rating
    private Integer totalBooks;           // total books

    @ElementCollection
    @CollectionTable(name = "author_genres", joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "genre")
    private List<String> genres;          // list of strings

    @ElementCollection
    @CollectionTable(name = "author_scores", joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "score")
    private List<Integer> scores;         // list of integers

    private BigDecimal wealth;            // big decimal for large numbers
    private BigInteger followers;         // big integer for very large numbers
}
