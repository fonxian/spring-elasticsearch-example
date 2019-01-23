package com.coffejoy.repository;
import com.coffejoy.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Created by fangzhijie on 2019/1/24.
 */
@Component
public interface BookRepository extends ElasticsearchRepository<Book,String> {

    Page<Book> findByNameAndPrice(String name, Long price, Pageable pageable);
    Page<Book> findByNameOrPrice(String name, Long price, Pageable pageable);
    Page<Book> findByName(String name, Pageable pageable);

}
