package com.example.fullrestapi.Repository;

import com.example.fullrestapi.domain.entities.AuthorEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorEntity, Long>,
        PagingAndSortingRepository<AuthorEntity, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id); // For updates
}
