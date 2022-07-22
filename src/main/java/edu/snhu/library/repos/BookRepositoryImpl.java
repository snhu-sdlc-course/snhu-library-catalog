//package edu.snhu.library.repos;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import edu.snhu.library.models.Book;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//@Log4j2
//@Service
//public class BookRepositoryImpl implements BookRepository {
//    private List<Book> bookDb;
//
//    BookRepositoryImpl(@Value("classpath:/testdata.json") final Resource testData) throws IOException {
//        try {
//            bookDb = new ObjectMapper()
//                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                    .readerForListOf(Book.class)
//                    .readValue(testData.getURL());
//        } catch(final IOException e) {
//            log.error("Failed to load test data", e);
//            throw e;
//        }
//    }
//
//    @Override
//    public List<Book> findAll() {
//        return bookDb;
//    }
//
//    @Override
//    public Book save(Book book) {
//        if(book.getId() < 0) {
//            book.setId(findMaxId() + 1);
//            bookDb.add(book);
//            return book;
//        } else {
//            final Optional<Book> matchingBook = bookDb.stream().filter(b -> b.getId().equals(book.getId())).findFirst();
//            if(matchingBook.isPresent()) {
//                bookDb.set(bookDb.indexOf(matchingBook.get()), book);
//                return book;
//            }
//        }
//        return null;
//    }
//
//    private Long findMaxId() {
//        return bookDb.stream().map(Book::getId).max(Long::compare).orElse(-1L);
//    }
//}
