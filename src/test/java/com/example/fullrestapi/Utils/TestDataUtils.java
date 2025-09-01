package com.example.fullrestapi.Utils;

import com.example.fullrestapi.domain.entities.AuthorEntity;
import com.example.fullrestapi.domain.entities.BookEntity;

public class TestDataUtils {

    static public AuthorEntity createAuthorA()
    {
        return AuthorEntity.builder()
                .name("Arther")
                .age(80)
                .build();
    }

    static public AuthorEntity createAuthorB()
    {
        return AuthorEntity.builder()
                .name("Arther2")
                .age(70)
                .build();
    }

    static public AuthorEntity createAuthorC()
    {
        return AuthorEntity.builder()
                .name("Arther3")
                .age(60)
                .build();
    }
    static public BookEntity createBookA(AuthorEntity author)
    {
        return BookEntity.builder()
                .isbn("965-982-0-110")
                .title("java learn")
                .author(author)
                .build();
    }

    static public BookEntity createBookB(AuthorEntity author)
    {
        return BookEntity.builder()
                .isbn("965-982-0-111")
                .title("java learn2")
                .author(author)
                .build();
    }

    static public BookEntity createBookC(AuthorEntity author)
    {
        return BookEntity.builder()
                .isbn("965-982-0-112")
                .title("java learn3")
                .author(author)
                .build();
    }
}
