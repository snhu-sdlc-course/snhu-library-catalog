package edu.snhu.library.repos;

import edu.snhu.library.models.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, Long> {
    @Query("{$or: [ {'title': {$regex: ?0, $options: 'i'}}, {'author': {$regex: ?0, $options: 'i'}} ]}")
    List<Book> filterByTitleAuthorAndTags(String filter, Sort sort);
}
