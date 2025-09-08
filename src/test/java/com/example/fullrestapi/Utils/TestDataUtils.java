package com.example.fullrestapi.Utils;

import com.example.fullrestapi.domain.entities.AuthorEntity;
import com.example.fullrestapi.domain.entities.BookEntity;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class TestDataUtils {

    public static AuthorEntity createAuthorFull() {
        return AuthorEntity.builder()
                .name("Arther")
                .age(80)
                .active(true)
                .rating(4.5)
                .totalBooks(10)
                .genres(List.of("Fantasy", "Sci-Fi"))
                .scores(List.of(95, 90, 85))
                .wealth(new BigDecimal("1000000.50"))
                .followers(new BigInteger("50000"))
                .build();
    }

    public static BookEntity createBookFull(AuthorEntity author) {
        return BookEntity.builder()
                .isbn("965-982-0-110")
                .title("Java Learn")
                .published(true)
                .pages(350)
                .price(new BigDecimal("49.99"))
                .author(author)
                .tags(List.of("Java", "Programming"))
                .ratings(List.of(5.0, 4.5, 5.0))
                .build();
    }
}
