package com.example.fullrestapi.Controllers;

import com.example.fullrestapi.Service.BookService;
import com.example.fullrestapi.domain.dto.BookDto;
import com.example.fullrestapi.domain.entities.BookEntity;
import com.example.fullrestapi.mappers.Impl.BookMapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BookController {
    private final BookService bookService;
    private final BookMapperImpl bookMapper;

    public BookController(BookService bookService,  BookMapperImpl bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @PutMapping(path ="/books/{isbn}")
    public ResponseEntity<BookDto> createUpdate(@PathVariable("isbn") String isbn, @RequestBody BookDto bookDto) {
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        boolean isExists = bookService.isExist(isbn);
        BookEntity savedBookEntity = bookService.save(isbn, bookEntity);
        BookDto savedBookDto = bookMapper.mapTo(savedBookEntity);

        if(isExists){
            return new ResponseEntity<>(savedBookDto,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(savedBookDto,HttpStatus.CREATED);
        }
    }

    @GetMapping(path ="/books")
    public Page<BookDto> findAll(Pageable pageable) {
        Page<BookEntity> books = bookService.findAll(pageable);
        return books.map(bookMapper::mapTo);
    }

    @GetMapping(path ="/books/{isbn}")
    public ResponseEntity<BookDto> findById(@PathVariable("isbn") String isbn)
    {
        return bookService.findById(isbn)
                .map(bookMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> partialUpdate(@PathVariable("isbn") String isbn, @RequestBody BookDto bookDto) {
        if(!bookService.isExist(isbn))
            return ResponseEntity.notFound().build();

        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        BookEntity updatedBookEntity = bookService.partialUpdate(isbn,bookEntity);
        return ResponseEntity.ok(bookMapper.mapTo(updatedBookEntity));
    }

    @DeleteMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> delete(@PathVariable("isbn") String isbn) {
        bookService.delete(isbn);
        return ResponseEntity.noContent().build();
    }
}
