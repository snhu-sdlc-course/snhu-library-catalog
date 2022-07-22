package edu.snhu.library.viewmodels;

import edu.snhu.library.models.Book;
import edu.snhu.library.repos.BookRepository;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
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
public class EditBookViewModel {
    private final LongProperty idProperty = new SimpleLongProperty(-1L);
    private final StringProperty titleProperty = new SimpleStringProperty();
    private final StringProperty authorProperty = new SimpleStringProperty();
    private final StringProperty tagsProperty = new SimpleStringProperty();

    private final BookRepository bookRepo;

    public EditBookViewModel(final BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public void loadFrom(final Book book) {
        if(book.getId() < 0) {
            throw new IllegalStateException("Book.id cannot be less than 0");
        }

        idProperty.set(book.getId());
        titleProperty.set(book.getTitle());
        authorProperty.set(book.getAuthor());
        tagsProperty.set(book.getTags());
    }

    public Book save() {
        if(idProperty.get() < 0) {
            throw new IllegalStateException("Book.id cannot be less than 0");
        }
        return bookRepo.save(new Book(idProperty.get(), titleProperty.get(), authorProperty.get(), tagsProperty.get()));
    }
}
