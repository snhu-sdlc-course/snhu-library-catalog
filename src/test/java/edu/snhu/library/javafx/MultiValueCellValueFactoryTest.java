package edu.snhu.library.javafx;

import edu.snhu.library.javafxext.JavaFxExtension;
import edu.snhu.library.javafxext.TestInJfxThread;
import edu.snhu.library.models.Book;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JavaFxExtension.class)
@ExtendWith(MockitoExtension.class)
class MultiValueCellValueFactoryTest {

    @TestInJfxThread
    @Test
    void call(@Mock final TableView<Book> tableView, @Mock final TableColumn<Book, String> tableColumn) {
        final Book book = new Book(1L, "My Book", "The Author", "Fiction");
        final MultiValueCellValueFactory<Book> factory = new MultiValueCellValueFactory<>("author", ";");
        final TableColumn.CellDataFeatures<Book, String> cellDataFeatures = new TableColumn.CellDataFeatures<>(tableView, tableColumn, book);

        // Confirm the initial value is correct
        assertThat(factory.call(cellDataFeatures).getValue()).isEqualTo("The Author");

        // Update the data
        book.setAuthor("Another Author");

        // Confirm the new value is returned
        assertThat(factory.call(cellDataFeatures).getValue()).isEqualTo("Another Author");
    }

    @MethodSource("formatToSingleArgumentsProvider")
    @ParameterizedTest
    void formatToSingleLine(final String separator, final String input, final String expectedOutput) {
        final MultiValueCellValueFactory<Book> factory = new MultiValueCellValueFactory<>(null, separator);
        assertThat(factory.formatToSingleLine(input)).isEqualTo(expectedOutput);
    }

    private static Stream<Arguments> formatToSingleArgumentsProvider() {
        return Stream.of(
                Arguments.of("; ", "Author 1\nAuthor 2 and Author 3\nAuthor 4", "Author 1; Author 2 and Author 3; Author 4"),
                Arguments.of("|", "Author 1\n\nAuthor 2\nAuthor 3\n\nAuthor 4", "Author 1|Author 2|Author 3|Author 4")
        );
    }
}