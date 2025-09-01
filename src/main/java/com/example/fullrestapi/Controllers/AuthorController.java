package com.example.fullrestapi.Controllers;

import com.example.fullrestapi.Service.AuthorService;
import com.example.fullrestapi.domain.entities.AuthorEntity;
import com.example.fullrestapi.domain.dto.AuthorDto;
import com.example.fullrestapi.mappers.Impl.BookMapperImpl;
import com.example.fullrestapi.mappers.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthorController {

    private final AuthorService authorService;
    private final Mapper<AuthorEntity, AuthorDto> authorMapper;
    private final BookMapperImpl bookMapperImpl;

    public AuthorController(AuthorService authorService, Mapper<AuthorEntity, AuthorDto> authorMapper, BookMapperImpl bookMapperImpl) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.bookMapperImpl = bookMapperImpl;
    }


    @PostMapping(path = "/authors")
    public ResponseEntity<AuthorDto> create(@RequestBody AuthorDto author) {
        AuthorEntity authorEntity = authorMapper.mapFrom(author);
        AuthorEntity authorSavedEntity= authorService.save(authorEntity);
        return new ResponseEntity<>(authorMapper.mapTo(authorSavedEntity),HttpStatus.CREATED);
    }

    @GetMapping(path = "/authors")
    public Page<AuthorDto> findAll(Pageable pageable) {
        Page<AuthorEntity> authors = authorService.findAll(pageable);
        return authors.map(authorMapper::mapTo);
    }

    @GetMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> findById(@PathVariable("id") Long id) {
        return authorService.findById(id)
                .map(authorMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> update(@PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {
        if(!authorService.isExists(id))
            return ResponseEntity.notFound().build();

        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        authorEntity.setId(id);
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);
        return new ResponseEntity<>(authorMapper.mapTo(savedAuthorEntity),HttpStatus.OK);
    }

    @PatchMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> patialUpdate(@PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {
        if(!authorService.isExists(id))
            return ResponseEntity.notFound().build();

        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        AuthorEntity updatedAuthorEntity = authorService.partialUpdate(id,authorEntity);
        return new ResponseEntity<>(authorMapper.mapTo(updatedAuthorEntity),HttpStatus.OK);
    }

    @DeleteMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> delete(@PathVariable("id") Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
