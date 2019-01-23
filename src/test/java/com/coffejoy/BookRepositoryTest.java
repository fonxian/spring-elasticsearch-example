package com.coffejoy;

import com.coffejoy.entity.Book;
import com.coffejoy.repository.BookRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fonxian on 2019/1/24.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BookRepositoryTest {

    @Resource
    private BookRepository repository;
    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 插入文档
     */
    @Test
    public void indexBook() {

        Book book = new Book();
        book.setId("123456");
        book.setName("瓦尔登湖");
        book.setPrice(20L);
        book.setVersion(1L);
        repository.save(book);

        Book book2 = new Book();
        book2.setId("234567");
        book2.setName("Java编程思想");
        book2.setPrice(88L);
        book2.setVersion(1L);
        repository.save(book2);

        Book book3 = new Book();
        book3.setId("8910");
        book3.setName("程序员的自我修养");
        book3.setPrice(56L);
        book3.setVersion(1L);
        repository.save(book3);

    }

    /**
     * 通过ID获取单条记录
     */
    @Test
    public void getOne() {
        if (repository.findById("123456").isPresent()) {
            Book book = repository.findById("123456").get();
            System.out.println(book.getName());
        }
    }

    /**
     * 使用查询条件
     */
    @Test
    public void queryByNameOrPrice() {
        Page<Book> books = repository.findByNameOrPrice("瓦尔登湖", 56L, Pageable.unpaged());
        books.forEach(book -> {
            System.out.println(book.getName());
            System.out.println(book.getPrice());
        });
    }

    /**
     * 获取所有文档
     */
    @Test
    public void getAll() {
        repository.findAll().forEach(book -> {
            System.out.println(book.getName());
            System.out.println(book.getPrice());
        });
    }

    /**
     * 原生方式查询字段
     */
    @Test
    public void nativeQueryByName() {

        QueryBuilder queryBuilder = new QueryStringQueryBuilder("修养").field("name");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        Page<Book> bookPage = esTemplate.queryForPage(searchQuery, Book.class);
        bookPage.getContent().forEach(book -> {
            System.out.println(book.getName());
        });

    }

}
