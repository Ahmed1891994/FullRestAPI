package com.example.fullrestapi.Service.Impl;

import com.example.fullrestapi.Repository.BookRepository;
import com.example.fullrestapi.Service.BookService;
import com.example.fullrestapi.domain.dto.BookDto;
import com.example.fullrestapi.domain.entities.BookEntity;
import com.example.fullrestapi.mappers.Impl.BookMapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapperImpl bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapperImpl bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookEntity save(String isbn, BookEntity book) {
        book.setIsbn(isbn);
        return bookRepository.save(book);
    }

    @Override
    public List<BookEntity> findAll() {
        return StreamSupport
                .stream(bookRepository.findAll().spliterator(),false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookEntity> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Optional<BookEntity> findById(String isbn) {
        return bookRepository.findById(isbn);
    }

    @Override
    public boolean isExist(String isbn) {
        return bookRepository.existsById(isbn);
    }

    @Override
    public void delete(String isbn) {
        bookRepository.deleteById(isbn);
    }

    @Override
    public BookEntity partialUpdate(String isbn, BookEntity bookEntity) {
        return bookRepository.findById(isbn)
                .map(existing -> {
                    BookDto dto = bookMapper.mapTo(bookEntity);
                    bookMapper.mapPartial(dto, existing);
                    return bookRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
}
