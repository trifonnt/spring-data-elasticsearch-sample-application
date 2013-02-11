package org.springframework.data.elasticsearch.repositories;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.entities.Book;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/springContext-test.xml")
public class SampleBookRepositoryTest {

    @Resource
    private SampleBookRepository repository;

    @Before
    public void emptyData(){
        repository.deleteAll();
    }

    @Test
    public void shouldIndexSingleBookEntity(){

        Book book = new Book();
        book.setId("123455");
        book.setName("Spring Data Elasticsearch");
        //Indexing using repository
        repository.save(book);
        //lets try to search same record in elasticsearch
        Book indexedBook = repository.findOne(book.getId());
        assertThat(indexedBook,is(notNullValue()));
        assertThat(indexedBook.getId(),is(book.getId()));
    }

    @Test
    public void shouldBulkIndexMultipleBookEntities(){

        Book book1 = new Book(RandomStringUtils.random(5),"Spring Data");
        Book book2 = new Book(RandomStringUtils.random(5),"Spring Data Elasticsearch");
        //Bulk Index using repository
        repository.save(asList(book1, book2));
        //lets try to search same records in elasticsearch
        Book indexedBook1 = repository.findOne(book1.getId());
        assertThat(indexedBook1.getId(),is(book1.getId()));
        Book indexedBook2 = repository.findOne(book2.getId());
        assertThat(indexedBook2.getId(),is(book2.getId()));
    }

    @Test
    @Ignore("not to run as just for showing usage of repository ! might throw java.lang.OutOfMemoryError :-) ")
    public void crudRepositoryTest(){

        Book book1 = new Book(RandomStringUtils.random(5),"Spring Data");
        Book book2 = new Book(RandomStringUtils.random(5),"Spring Data Elasticsearch");
        List<Book> books = Arrays.asList(book1,book2);

        //indexing single document
        repository.save(book1);
        //bulk indexing multiple documents
        repository.save(books);
        //searching single document based on documentId
        Book book = repository.findOne(book1.getId());
        //to get all records as iteratable collection
        Iterable<Book> bookList = repository.findAll();
        //page request which will give first 10 document
        Page<Book> bookPage = repository.findAll(new PageRequest(0,10));
        // to get all records as ASC on name field
        Iterable<Book> bookIterable = repository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC,"name")));
        //to get total number of docoments in an index
        Long count = repository.count();
        //to check wheather document exists or not
        boolean exists = repository.exists(book1.getId());
        //delete a document by entity
        repository.delete(book1);
        //delete multiple document using collection
        repository.delete(books);
        //delete a document using documentId
        repository.delete(book1.getId());
        //delete all document
        repository.deleteAll();
    }

    @Test
    public void shouldCountAllElementsInIndex(){

        List<Book> books = new ArrayList<Book>();
        for(int i=1; i<=10 ; i++){
           books.add(new Book(RandomStringUtils.random(5),"Spring Data Rocks !"));
        }
        //Bulk Index using repository
        repository.save(books);
        //count all elements
        long count = repository.count();
        assertThat(count,is(equalTo(10L)));
    }

    @Test
    public void shouldExecuteCustomSearchQueries(){

        Book book1 = new Book(RandomStringUtils.random(5),"Custom Query");
        Book book2 = new Book(RandomStringUtils.random(5),null);
        //indexing a book
        repository.save(Arrays.asList(book1,book2));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setElasticsearchQuery(matchAllQuery());
        searchQuery.setElasticsearchFilter(boolFilter().must(existsFilter("name")));
        searchQuery.setPageable(new PageRequest(0,10));

        Page<Book> books = repository.search(searchQuery);
        assertThat(books.getNumberOfElements(), is(equalTo(1)));
    }

    @Test
    public void shoulExecuteCustomSearchQueries(){
        Book book1 = new Book(RandomStringUtils.random(5),"Custom Query");
        Book book2 = new Book(RandomStringUtils.random(5),"Elasticsearch QueryBuilder");
        //bulk indexing two documents
        repository.save(Arrays.asList(book1,book2));
        QueryBuilder queryBuilder = QueryBuilders.fieldQuery("name",book1.getName());
        //searching in elasticsearch using repository Page<E> search(QueryBuilder q, PageRequest p ) method.
        Page<Book> books =  repository.search(queryBuilder,new PageRequest(0,20));
        assertThat(books.getNumberOfElements(),is(equalTo(1)));
    }



}