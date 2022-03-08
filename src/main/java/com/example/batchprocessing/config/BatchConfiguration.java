package com.example.batchprocessing.config;

import javax.sql.DataSource;

import com.example.batchprocessing.listner.JobCompletionNotificationListener;
import com.example.batchprocessing.dto.Book;
import com.example.batchprocessing.mapper.BookMapper;
import com.example.batchprocessing.mapper.ManagineMapper;
import com.example.batchprocessing.processor.BookItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	public PatternMatchingCompositeLineMapper orderFileLineMapper() {
		PatternMatchingCompositeLineMapper lineMapper =
				new PatternMatchingCompositeLineMapper();
		Map<String, LineTokenizer> tokenizers = new HashMap<>(3);
		tokenizers.put("IS*", bookTokenizer());
		tokenizers.put("PS*", magazineTokenizer());
		lineMapper.setTokenizers(tokenizers);
		Map<String, FieldSetMapper> mappers = new HashMap<>(2);
		mappers.put("IS*", new BookMapper());
		mappers.put("PS*", new ManagineMapper());
		lineMapper.setFieldSetMappers(mappers);
		return lineMapper;
	}

	public FixedLengthTokenizer bookTokenizer() {
		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();

		tokenizer.setNames("ISIN", "Quantity", "Price", "Author");
		tokenizer.setColumns(new Range(1, 12),
				new Range(13, 15),
				new Range(16, 20),
				new Range(21, 29));

		return tokenizer;
	}

	public FixedLengthTokenizer magazineTokenizer() {
		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();

		tokenizer.setNames("ISIN", "Quantity", "Price", "Author");
		tokenizer.setColumns(new Range(1, 12),
				new Range(13, 15),
				new Range(16, 26),
				new Range(27, 35));
		return tokenizer;
	}

	@Bean
	public FlatFileItemReader<Book> bookReader() {
		FlatFileItemReader<Book> itemReader = new FlatFileItemReader<Book>();
		itemReader.setResource(new ClassPathResource("book.txt"));
		itemReader.setLineMapper(orderFileLineMapper());
		return itemReader;
	}

	@Bean
	public BookItemProcessor bookItemProcessor() {
		return new BookItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Book> bookWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Book>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO book (author, isin) VALUES (:author, :isin)")
				.dataSource(dataSource)
				.build();
	}

	@Bean
	public Job importBookJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importBookJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Book> writer) {
		return stepBuilderFactory.get("step1")
				.<Book, Book> chunk(10)
				.reader(bookReader())
				.processor(bookItemProcessor())
				.writer(writer)
				.build();
	}
}
