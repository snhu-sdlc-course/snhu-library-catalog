package edu.snhu.library.services;

import edu.snhu.library.models.Book;
import edu.snhu.library.repos.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Returns a set of all unique tags found in the books db.
     * @return
     */
    public Set<String> getUniqueTags() {
        return bookRepository.findDistinctTags()
                .map(Book::getTags)
                .flatMap(t -> Stream.of(t.split("[;|\n\r]")))
                .collect(Collectors.toSet());
    }
}
