### Spring整合ElasticSearch例子



#### 1.引入依赖

```xml
	<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>


    <dependencies>

        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-elasticsearch</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

```
#### 2.添加配置

```

server.port=8085

spring.data.elasticsearch.cluster-nodes = bei1:9300
elasticsearch.cluster.name=coffe-elasticsearch

```

#### 3.创建实体类和数据访问类

实体类

```java

@Document(indexName = "book",type = "book")
public class Book {
    @Id
    private String id;
    private String name;
    private Long price;
    @Version
    private Long version;

    public Map<Integer, Collection<String>> getBuckets() {
        return buckets;
    }

    public void setBuckets(Map<Integer, Collection<String>> buckets) {
        this.buckets = buckets;
    }

    @Field(type = FieldType.Nested)
    private Map<Integer, Collection<String>> buckets = new HashMap();

    public Book(){}

    public Book(String id, String name,Long version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}

```

数据访问类

```java

@Component
public interface BookRepository extends ElasticsearchRepository<Book,String> {

    Page<Book> findByNameAndPrice(String name, Long price, Pageable pageable);
    Page<Book> findByNameOrPrice(String name, Long price, Pageable pageable);
    Page<Book> findByName(String name, Pageable pageable);

}

```


#### 3.使用单元测试测试使用结果

创建测试类

```java

@SpringBootTest
@RunWith(SpringRunner.class)
public class BookRepositoryTest {

    @Autowired
    private BookRepository repository;
    @Autowired
    private ElasticsearchTemplate esTemplate;
    
}

```

**（1）添加文档**

```java


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



```

**(2)查询所有文档**

```java
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

```

![运行结果](https://img2018.cnblogs.com/blog/789766/201901/789766-20190124053406676-1516736086.png)


**(3)使用查询条件**

```java

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


```

![运行结果](https://img2018.cnblogs.com/blog/789766/201901/789766-20190124054753404-359864478.png)


**(4)使用原生方式查询**

```java

    /**
     * 原生方式查询字段
     */
    @Test
    public void queryByName() {

        QueryBuilder queryBuilder = new QueryStringQueryBuilder("修养").field("name");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        Page<Book> bookPage = esTemplate.queryForPage(searchQuery, Book.class);
        bookPage.getContent().forEach(book -> {
            System.out.println(book.getName());
        });

    }


```

![运行结果](https://img2018.cnblogs.com/blog/789766/201901/789766-20190124053715014-1499615926.png)




