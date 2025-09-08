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

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final BookMapperImpl bookMapper;

    public BookController(BookService bookService, BookMapperImpl bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    // Create a new book
    @PostMapping("/{isbn}")
    public ResponseEntity<BookDto> createBook(@PathVariable String isbn, @RequestBody BookDto bookDto) {
        if (bookService.isExist(isbn)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 if ISBN exists
        }
        BookEntity savedBook = bookService.save(isbn, bookMapper.mapFrom(bookDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.mapTo(savedBook));
    }

    // Partial update (also handles full updates)
    @PatchMapping("/{isbn}")
    public ResponseEntity<BookDto> updateBook(@PathVariable String isbn, @RequestBody BookDto bookDto) {
        if (!bookService.isExist(isbn)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 if ISBN doesn't exist
        }
        BookEntity updatedBook = bookService.partialUpdate(isbn, bookMapper.mapFrom(bookDto));
        return ResponseEntity.ok(bookMapper.mapTo(updatedBook));
    }

    // Get all books
    @GetMapping
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookService.findAll(pageable).map(bookMapper::mapTo);
    }

    // Get book by ISBN
    @GetMapping("/{isbn}")
    public ResponseEntity<BookDto> getBookByIsbn(@PathVariable String isbn) {
        return bookService.findById(isbn)
                .map(bookMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a book
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.delete(isbn);
        return ResponseEntity.noContent().build();
    }
}
