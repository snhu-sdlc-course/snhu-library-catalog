package edu.snhu.library.viewmodels;

import edu.snhu.library.models.Book;
import edu.snhu.library.repos.BookRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;

@Scope("prototype")
@Component
@Value
public class HomeViewModel {
    private final ObservableList<Book> books;
    private final BookRepository bookRepo;
    private final SimpleStringProperty filterProperty;
    private final SimpleObjectProperty<Sort> sort;

    public HomeViewModel(final BookRepository bookRepo) {
        this.bookRepo = bookRepo;
        books = FXCollections.observableList(new LinkedList<>());
        filterProperty = new SimpleStringProperty();
        filterProperty.addListener((observable, newValue, oldValue) -> loadBooks());
        sort = new SimpleObjectProperty<>(Sort.unsorted());
        sort.addListener(((observable, oldValue, newValue) -> loadBooks()));
    }

    public void updateSort(final Sort sort) {
        this.sort.set(sort);
    }

    public void loadBooks() {
        if(StringUtils.isBlank(filterProperty.get())) {
            updateBooks(bookRepo.findAll(sort.get()));
        } else {
            updateBooks(bookRepo.filterByTitleAuthorAndTags(filterProperty.get(), sort.get()));
        }
    }

    private void updateBooks(final Collection<Book> books) {
        this.books.clear();
        this.books.addAll(books);
    }
}
