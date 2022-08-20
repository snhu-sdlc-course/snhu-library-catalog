package edu.snhu.library.viewmodels;

import edu.snhu.library.models.Book;
import edu.snhu.library.repos.BookRepository;
import edu.snhu.library.services.BookService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
    private final BookService bookService;
    private final BookRepository bookRepo;
    private final SimpleStringProperty filterProperty;
    private final SimpleObjectProperty<Sort> sort;
    private final ObservableList<String> genres;
    private final ObservableList<String> selectedGenres;

    public HomeViewModel(final BookService bookService, final BookRepository bookRepo) {
        this.bookService = bookService;
        this.bookRepo = bookRepo;

        books = FXCollections.observableList(new LinkedList<>());

        filterProperty = new SimpleStringProperty();
        filterProperty.addListener((observable, newValue, oldValue) -> loadBooks());

        sort = new SimpleObjectProperty<>(Sort.unsorted());
        sort.addListener(((observable, oldValue, newValue) -> loadBooks()));

        genres = FXCollections.observableList(new LinkedList<>());
        selectedGenres = FXCollections.observableList(new LinkedList<>());
        selectedGenres.addListener((ListChangeListener<? super String>) c -> loadBooks());
    }

    public void updateSort(final Sort sort) {
        this.sort.set(sort);
    }

    public void loadBooks() {
        if(StringUtils.isBlank(filterProperty.get()) && selectedGenres.isEmpty()) {
            updateBooks(bookRepo.findAll(sort.get()));
        } else {
            updateBooks(bookRepo.filterByTitleAuthorAndTags(filterProperty.getValueSafe(), StringUtils.join(selectedGenres, "|"), sort.get()));
        }
    }

    public void loadGenres() {
        // Print all unique tags/genres to terminal
        System.out.println(StringUtils.join(bookService.getUniqueTags(), ","));

        // TODO: Update the list of available genres to
        //       be the unique genres found in `books`.
        //       Note -- bookService.getUniqueTags() will return all unique tags/genres
        genres.setAll("Action", "Documentary", "War");
    }

    private void updateBooks(final Collection<Book> books) {
        this.books.clear();
        this.books.addAll(books);
    }
}
