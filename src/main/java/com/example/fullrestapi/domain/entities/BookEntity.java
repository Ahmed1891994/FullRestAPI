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
    private String isbn;  // ✅ natural key

    private String title;
    private Boolean published;            // boolean
    private Integer pages;                // integer
    private BigDecimal price;             // big decimal price

    // ✅ Many books can have the same Author
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @ElementCollection
    @CollectionTable(name = "book_tags", joinColumns = @JoinColumn(name = "book_isbn"))
    @Column(name = "tag")
    private List<String> tags;            // list of strings

    @ElementCollection
    @CollectionTable(name = "book_ratings", joinColumns = @JoinColumn(name = "book_isbn"))
    @Column(name = "rating")
    private List<Double> ratings;         // list of doubles
}
