package edu.snhu.library.viewmodels;

import edu.snhu.library.models.Book;
import edu.snhu.library.repos.BookRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
@Log4j2
@Value
public class AddBookViewModel {
    private final StringProperty titleProperty = new SimpleStringProperty();
    private final StringProperty authorProperty = new SimpleStringProperty();
    private final StringProperty tagsProperty = new SimpleStringProperty();

    private final BookRepository bookRepo;

    public AddBookViewModel(final BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public Book save() {
        return bookRepo.save(new Book(-1L, titleProperty.get(), authorProperty.get(), tagsProperty.get()));
    }
}
