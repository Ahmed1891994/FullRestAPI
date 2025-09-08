package com.example.fullrestapi.mappers.Impl;

import com.example.fullrestapi.domain.dto.BookDto;
import com.example.fullrestapi.domain.entities.BookEntity;
import com.example.fullrestapi.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class BookMapperImpl implements Mapper<BookEntity, BookDto> {

    private final ModelMapper modelMapper;
    private final AuthorMapperImpl authorMapper;

    public BookMapperImpl(ModelMapper modelMapper, AuthorMapperImpl authorMapper) {
        this.modelMapper = modelMapper;
        this.authorMapper = authorMapper;
    }

    @Override
    public BookDto mapTo(BookEntity bookEntity) {
        return modelMapper.map(bookEntity, BookDto.class);
    }

    @Override
    public BookEntity mapFrom(BookDto bookDto) {
        return modelMapper.map(bookDto, BookEntity.class);
    }

    // Partial mapping: only non-null fields
    public void mapPartial(BookDto source, BookEntity target) {
        if (source.getTitle() != null) target.setTitle(source.getTitle());
        if (source.getPublished() != null) target.setPublished(source.getPublished());
        if (source.getPages() != null) target.setPages(source.getPages());
        if (source.getPrice() != null) target.setPrice(source.getPrice());
        if (source.getAuthor() != null) {
            if (target.getAuthor() == null) target.setAuthor(authorMapper.mapFrom(source.getAuthor()));
            else authorMapper.mapPartial(source.getAuthor(), target.getAuthor());
        }
        if (source.getTags() != null) target.setTags(new ArrayList<>(source.getTags()));
        if (source.getRatings() != null) target.setRatings(new ArrayList<>(source.getRatings()));
    }
}
