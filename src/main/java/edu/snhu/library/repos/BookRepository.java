package edu.snhu.library.repos;

import edu.snhu.library.models.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface BookRepository extends MongoRepository<Book, Long> {
    @Query("{$and: [ " +
                "{$or: [ " +
                    "{'title': {$regex: ?0, $options: 'i'}}, " +
                    "{'author': {$regex: ?0, $options: 'i'}} " +
                "]}, " +
                "{'tags': {$regex: ?1, $options: 'i'}} " +
           "]}")
    List<Book> filterByTitleAuthorAndTags(String filter, String tags, Sort sort);

    @Query(value = "{}", fields = "{tags: 1, _id: 0}")
    Stream<Book> findDistinctTags();
}
