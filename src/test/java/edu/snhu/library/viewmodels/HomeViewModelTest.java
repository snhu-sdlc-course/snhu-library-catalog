package edu.snhu.library.viewmodels;

import edu.snhu.library.models.Book;
import edu.snhu.library.repos.BookRepository;
import edu.snhu.library.services.BookService;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HomeViewModelTest {

    @Mock private BookService bookService;
    @Mock private BookRepository bookRepository;
    @InjectMocks private HomeViewModel viewModel;

    @Test
    void updateSort() {
        final Sort sort = Sort.by(Sort.Direction.ASC, "author");

        // Invoke the method under test
        viewModel.updateSort(sort);

        // Confirm that changing the sort causes the books to be reloaded
        verify(bookRepository).findAll(sort);
    }

    @Test
    void loadBooks_WithoutFilterOrGenre() {
        // Invoke the method under test
        viewModel.loadBooks();

        // Verify findAll() was called with Sort.unsorted()
        verify(bookRepository).findAll(eq(Sort.unsorted()));
    }

    @Test
    void loadBooks_WithFilter() {
        // If either filter or genre is specified, filterByTitleAuthorAndTags() should be called
        viewModel.getFilterProperty().set("some filter");

        // We don't have to invoke loadBooks() because changing the filter property triggers loadBooks()

        // Verify filterByTitleAuthorAndTags() was called
        verify(bookRepository).filterByTitleAuthorAndTags("some filter", "", Sort.unsorted());
    }

    @Test
    void loadBooks_WithGenre() {
        // If either filter or genre is specified, filterByTitleAuthorAndTags() should be called
        viewModel.getSelectedGenres().setAll("Science Fiction", "Western Fiction");

        // We don't have to invoke loadBooks() because changing the selectedGenres property triggers loadBooks()

        // Verify filterByTitleAuthorAndTags() was called
        verify(bookRepository).filterByTitleAuthorAndTags("", "Science Fiction|Western Fiction", Sort.unsorted());
    }

    @Test
    void loadGenres() {
        // Watch the value of the observable genres list
        final SimpleListProperty<String> listProperty = new SimpleListProperty<>(FXCollections.observableList(new LinkedList<>()));
        listProperty.bindContent(viewModel.getGenres());

        // Invoke the method under test
        viewModel.loadGenres();

        // The current implementation of loadGenres() has only 3 hard-coded values
        assertThat(listProperty).contains("Action", "Documentary", "War");
    }

    @Test
    void updateBooks() {
        // Add a book
        final Book book = new Book(1L, "Title", "Author", "Genre");
        viewModel.getBooks().add(book);

        // Invoke the method under test
        final Book book2 = new Book(2L, "Title2", "Author2", "Genre2");
        viewModel.updateBooks(List.of(book2));

        // Verify it clears and reloads the books and only has the new book2
        assertThat(viewModel.getBooks()).containsOnly(book2);
    }
}