package com.example.fullrestapi.Service.Impl;

import com.example.fullrestapi.Repository.AuthorRepository;
import com.example.fullrestapi.Service.AuthorService;
import com.example.fullrestapi.domain.dto.AuthorDto;
import com.example.fullrestapi.domain.entities.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorEntity save(AuthorEntity author) {
        return authorRepository.save(author);
    }

    @Override
    public List<AuthorEntity> findAll() {
        return StreamSupport.stream(authorRepository.findAll().spliterator(),false).collect(Collectors.toList());
    }

    @Override
    public Page<AuthorEntity> findAll(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    @Override
    public Optional<AuthorEntity> findById(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return authorRepository.existsById(id);
    }

    @Override
    public AuthorEntity partialUpdate(Long id, AuthorEntity authorEntity) {
        authorEntity.setId(id);
        return authorRepository.findById(id).map(ExistingAuthor -> {
            Optional.ofNullable(authorEntity.getName()).ifPresent(ExistingAuthor::setName);
            Optional.ofNullable(authorEntity.getAge()).ifPresent(ExistingAuthor::setAge);
            return this.save(ExistingAuthor);
        }).orElseThrow(() -> new RuntimeException("Author not found"));
    }

    @Override
    public void delete(Long id) {
        authorRepository.deleteById(id);
    }
}
