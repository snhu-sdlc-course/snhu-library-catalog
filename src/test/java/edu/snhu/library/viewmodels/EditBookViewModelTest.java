package edu.snhu.library.viewmodels;

import edu.snhu.library.models.Book;
import edu.snhu.library.repos.BookRepository;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EditBookViewModelTest {

    @Mock private BookRepository bookRepository;
    @InjectMocks private EditBookViewModel viewModel;

    @Test
    void loadFrom() {
        final LongProperty id = new SimpleLongProperty();
        id.bind(viewModel.getIdProperty());

        final StringProperty title = new SimpleStringProperty();
        title.bind(viewModel.getTitleProperty());

        final StringProperty author = new SimpleStringProperty();
        author.bind(viewModel.getAuthorProperty());

        final StringProperty tags = new SimpleStringProperty();
        tags.bind(viewModel.getTagsProperty());

        final Book book = new Book(15L, "My Title", "My Author", "My Generes");

        // Invoke the method under test
        viewModel.loadFrom(book);

        // Verify the properties were updated from the book and propagated to our bound properties
        assertThat(id.get()).isEqualTo(book.getId());
        assertThat(title.get()).isEqualTo(book.getTitle());
        assertThat(author.get()).isEqualTo(book.getAuthor());
        assertThat(tags.get()).isEqualTo(book.getTags());
    }

    @Test
    void save() {
        // Set the data
        final Book book = new Book(15L, "My Title", "My Author", "My Genres");
        viewModel.loadFrom(book);

        // Simulate changes made to the form
        viewModel.getTitleProperty().set("A New Title");

        // Invoke the method under test
        viewModel.save();

        // Capture the data saved to the database
        final ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        final Book savedBook = bookCaptor.getValue();

        // Verify the unchanged values and new title were saved
        assertThat(savedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 15L)
                .hasFieldOrPropertyWithValue("title", "A New Title")
                .hasFieldOrPropertyWithValue("author", "My Author")
                .hasFieldOrPropertyWithValue("tags", "My Genres");
    }
}