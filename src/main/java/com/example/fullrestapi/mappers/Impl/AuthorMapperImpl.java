package com.example.fullrestapi.mappers.Impl;

import com.example.fullrestapi.domain.dto.AuthorDto;
import com.example.fullrestapi.domain.entities.AuthorEntity;
import com.example.fullrestapi.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AuthorMapperImpl implements Mapper<AuthorEntity, AuthorDto> {

    private final ModelMapper modelMapper;

    public AuthorMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AuthorDto mapTo(AuthorEntity authorEntity) {
        return modelMapper.map(authorEntity, AuthorDto.class);
    }

    @Override
    public AuthorEntity mapFrom(AuthorDto authorDto) {
        return modelMapper.map(authorDto, AuthorEntity.class);
    }

    // Partial mapping: only non-null fields
    public void mapPartial(AuthorDto source, AuthorEntity target) {
        if (source.getName() != null) target.setName(source.getName());
        if (source.getAge() != null) target.setAge(source.getAge());
        if (source.getActive() != null) target.setActive(source.getActive());
        if (source.getRating() != null) target.setRating(source.getRating());
        if (source.getTotalBooks() != null) target.setTotalBooks(source.getTotalBooks());
        if (source.getGenres() != null) target.setGenres(new ArrayList<>(source.getGenres()));
        if (source.getScores() != null) target.setScores(new ArrayList<>(source.getScores()));
        if (source.getWealth() != null) target.setWealth(source.getWealth());
        if (source.getFollowers() != null) target.setFollowers(source.getFollowers());
    }
}
