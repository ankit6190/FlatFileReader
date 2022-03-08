package com.example.batchprocessing.mapper;

import com.example.batchprocessing.dto.Book;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class BookMapper implements FieldSetMapper<Book> {

    @Override
    public Book mapFieldSet(FieldSet fieldSet) throws BindException {
        Book book = new Book();
        book.setAuthor(fieldSet.readString("Author"));
        book.setPrice(fieldSet.readDouble("Price"));
        book.setIsin(fieldSet.readString("ISIN"));
        book.setQuantity(fieldSet.readInt("Quantity"));
        return book;
    }
}
