package com.example.fullrestapi.Controllers;

import com.example.fullrestapi.Service.AuthorService;
import com.example.fullrestapi.domain.entities.AuthorEntity;
import com.example.fullrestapi.domain.dto.AuthorDto;
import com.example.fullrestapi.mappers.Mapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final Mapper<AuthorEntity, AuthorDto> authorMapper;

    public AuthorController(AuthorService authorService, Mapper<AuthorEntity, AuthorDto> authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AuthorDto author) {
        try {
            AuthorEntity saved = authorService.save(authorMapper.mapFrom(author));
            return ResponseEntity.status(HttpStatus.CREATED).body(authorMapper.mapTo(saved));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public Page<AuthorDto> findAll(Pageable pageable) {
        return authorService.findAll(pageable).map(authorMapper::mapTo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return authorService.findById(id)
                .map(authorMapper::mapTo)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Author not found")));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {
        if (!authorService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Author not found"));
        }
        try {
            AuthorEntity entity = authorMapper.mapFrom(authorDto);
            entity.setId(id);
            AuthorEntity saved = authorService.save(entity);
            return ResponseEntity.ok(authorMapper.mapTo(saved));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> partialUpdate(@PathVariable Long id,
                                                @RequestBody AuthorDto authorDto) {

        if (!authorService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Author not found"));
        }

        // ✅ ADD MANUAL VALIDATION FOR PROVIDED FIELDS
        ResponseEntity<Object> validationError = validatePartialUpdate(authorDto);
        if (validationError != null) {
            return validationError;
        }

        // Check if name is provided and already exists for another author
        if (authorDto.getName() != null &&
                authorService.existsByNameAndIdNot(authorDto.getName(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Author with this name already exists"));
        }

        AuthorEntity updated = authorService.partialUpdate(id, authorMapper.mapFrom(authorDto));
        return ResponseEntity.ok(authorMapper.mapTo(updated));
    }

    // ✅ ADD VALIDATION METHOD FOR PARTIAL UPDATES (WITH ACTIVE VALIDATION)
    private ResponseEntity<Object> validatePartialUpdate(AuthorDto authorDto) {
        // Validate name if provided
        if (authorDto.getName() != null) {
            if (authorDto.getName().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name must not be empty"));
            }

            if (authorDto.getName().length() > 100) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name cannot exceed 100 characters"));
            }

            // ✅ ADD SPECIAL CHARACTER VALIDATION
            if (containsInvalidCharacters(authorDto.getName())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name must not contain special characters"));
            }
        }

        // ✅ VALIDATE ACTIVE FIELD IF PROVIDED (REQUIRED FIELD!)
        if (authorDto.getActive() != null) {
            // Active is a Boolean, so we just need to ensure it's not null
            // But since it's @NotNull in DTO, we should validate it's provided correctly
            if (authorDto.getActive() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Active is required"));
            }
        }

        // Validate age if provided
        if (authorDto.getAge() != null) {
            if (authorDto.getAge() < 1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Age must be at least 1"));
            }
            if (authorDto.getAge() > 125) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Age must not exceed 125"));
            }
        }

        // Validate rating if provided
        if (authorDto.getRating() != null) {
            if (authorDto.getRating() < 0.0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Rating cannot be negative"));
            }
            if (authorDto.getRating() > 5.0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Rating cannot exceed 5.0"));
            }
        }

        // Validate totalBooks if provided
        if (authorDto.getTotalBooks() != null) {
            if (authorDto.getTotalBooks() < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Total books cannot be negative"));
            }
        }

        // Validate wealth if provided
        if (authorDto.getWealth() != null) {
            if (authorDto.getWealth().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Wealth cannot be negative"));
            }
        }

        // Validate followers if provided
        if (authorDto.getFollowers() != null) {
            if (authorDto.getFollowers().compareTo(BigInteger.ZERO) < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Followers cannot be negative"));
            }
        }

        return null; // No validation errors
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        if (!authorService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Author not found"));
        }

        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

        // ✅ ADD HELPER METHOD FOR CHARACTER VALIDATION
        private boolean containsInvalidCharacters(String name) {
            // Allow letters, spaces, hyphens, apostrophes - common in names
            String validNamePattern = "^[a-zA-Z\\s\\-'.]+$";

            // Or more restrictive: only letters and spaces
            // String validNamePattern = "^[a-zA-Z\\s]+$";

            return !name.matches(validNamePattern);
        }
}
