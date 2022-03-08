package com.example.batchprocessing.processor;

import com.example.batchprocessing.dto.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class BookItemProcessor implements ItemProcessor<Book, Book> {

    private static final Logger log = LoggerFactory.getLogger(BookItemProcessor.class);

    @Override
    public Book process(final Book book) throws Exception {
        final String author = book.getAuthor().toUpperCase();
        final String isin = book.getIsin().toUpperCase();

        final Book transformedBook = new Book(author, isin);

        log.info("Converting (" + book + ") into (" + transformedBook + ")");

        return transformedBook;
    }
}
