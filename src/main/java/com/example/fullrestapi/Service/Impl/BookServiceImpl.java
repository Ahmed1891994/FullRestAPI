package com.example.fullrestapi.Service.Impl;

import com.example.fullrestapi.Repository.AuthorRepository;
import com.example.fullrestapi.Repository.BookRepository;
import com.example.fullrestapi.Service.BookService;
import com.example.fullrestapi.domain.dto.BookDto;
import com.example.fullrestapi.domain.entities.AuthorEntity;
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
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, BookMapperImpl bookMapper, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.authorRepository = authorRepository;
    }

    @Override
    public BookEntity save(String isbn, BookEntity book) {
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            AuthorEntity existingAuthor = authorRepository.findById(book.getAuthor().getId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));

            book.setAuthor(mergeAuthorFields(existingAuthor, book.getAuthor()));
        }
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

                    if (bookEntity.getAuthor() != null) {
                        if (bookEntity.getAuthor().getId() != null) {
                            AuthorEntity existingAuthor = authorRepository.findById(bookEntity.getAuthor().getId())
                                    .orElseThrow(() -> new RuntimeException("Author not found"));

                            existingAuthor = mergeAuthorFields(existingAuthor, bookEntity.getAuthor());
                            existing.setAuthor(existingAuthor);
                        } else {
                            existing.setAuthor(bookEntity.getAuthor());
                        }
                    }

                    return bookRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    private AuthorEntity mergeAuthorFields(AuthorEntity existingAuthor, AuthorEntity newAuthor) {
        if (existingAuthor.getName() == null && newAuthor.getName() != null)
            existingAuthor.setName(newAuthor.getName());

        if (existingAuthor.getAge() == null && newAuthor.getAge() != null)
            existingAuthor.setAge(newAuthor.getAge());

        if (existingAuthor.getActive() == null && newAuthor.getActive() != null)
            existingAuthor.setActive(newAuthor.getActive());

        if (existingAuthor.getRating() == null && newAuthor.getRating() != null)
            existingAuthor.setRating(newAuthor.getRating());

        if (existingAuthor.getTotalBooks() == null && newAuthor.getTotalBooks() != null)
            existingAuthor.setTotalBooks(newAuthor.getTotalBooks());

        if ((existingAuthor.getGenres() == null || existingAuthor.getGenres().isEmpty())
                && newAuthor.getGenres() != null && !newAuthor.getGenres().isEmpty())
            existingAuthor.setGenres(newAuthor.getGenres());

        if ((existingAuthor.getScores() == null || existingAuthor.getScores().isEmpty())
                && newAuthor.getScores() != null && !newAuthor.getScores().isEmpty())
            existingAuthor.setScores(newAuthor.getScores());

        if (existingAuthor.getWealth() == null && newAuthor.getWealth() != null)
            existingAuthor.setWealth(newAuthor.getWealth());

        if (existingAuthor.getFollowers() == null && newAuthor.getFollowers() != null)
            existingAuthor.setFollowers(newAuthor.getFollowers());

        return existingAuthor;
    }
}
