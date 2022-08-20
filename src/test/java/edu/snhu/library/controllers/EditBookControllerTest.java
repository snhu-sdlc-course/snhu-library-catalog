package edu.snhu.library.controllers;

import edu.snhu.library.enums.ModalResultStatus;
import edu.snhu.library.models.Book;
import edu.snhu.library.models.ModalResult;
import edu.snhu.library.services.FxmlRootProvider;
import edu.snhu.library.viewmodels.EditBookViewModel;
import javafx.stage.Stage;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditBookControllerTest {
    @Mock private Stage modalDialog;
    @Mock private CompletableFuture<ModalResult<Book>> promise;

    @Mock private FxmlRootProvider fxmlRootProvider;
    @Mock private EditBookViewModel editBookViewModel;
    @InjectMocks private EditBookController controller;

    @BeforeEach
    void init() {
        controller.modalDialog = modalDialog;
        controller.promise = promise;
    }

    @Test
    void show() {
        // We do NOT want to actually create a Stage (JavaFX modal)
        when(fxmlRootProvider.getRoot(any(), any())).thenReturn(Optional.empty());

        // Create a book so we can confirm it's title is used in the modal
        final Book book = new Book(1L, "Java Book", "Author", "Fiction");

        // Invoke the show() method
        controller.show(null, book);

        // Confirm the scene title contains the book title
        assertThat(controller.getSceneTitle()).contains(book.getTitle());

        // Verify an attempt was made to load the book from the database
        verify(editBookViewModel).loadFrom(book);
    }

    @Test
    void show_MissingBookArgument() {
        // Confirm calling show without the Book throws an exception
        assertThatThrownBy(() -> controller.show(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Second argument to show() must be a Book");
    }

    @Test
    void show_NonBookArgument() {
        // Confirm calling show with something other than a Book throws an exception
        assertThatThrownBy(() -> controller.show(null, "A string"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Second argument to show() must be a Book");
    }

    @Test
    void saveBook() {
        final ArgumentCaptor<ModalResult<Book>> modalResultCaptor = ArgumentCaptor.forClass(ModalResult.class);

        // Invoke the EditBookController.saveBook() method
        controller.saveBook();

        // Verify the modal dialog was closed
        verify(modalDialog).close();

        // Verify the promise's complete() method was called
        // and capture the argument it was called with
        verify(promise).complete(modalResultCaptor.capture());

        // Verify the book was saved
        verify(editBookViewModel).save();

        // Confirm the promise's complete() method was called
        // with a ModalResultStatus of UPDATED
        assertThat(modalResultCaptor.getValue())
                .isNotNull()
                .extracting("status", InstanceOfAssertFactories.type(ModalResultStatus.class))
                .isEqualTo(ModalResultStatus.UPDATED);
    }

    @Test
    void cancel() {
        final ArgumentCaptor<ModalResult<Book>> modalResultCaptor = ArgumentCaptor.forClass(ModalResult.class);

        // Invoke the EditBookController.addBook() method
        controller.cancel();

        // Verify the modal dialog was closed
        verify(modalDialog).close();

        // Verify the promise's complete() method was called
        // and capture the argument it was called with
        verify(promise).complete(modalResultCaptor.capture());

        // Confirm the promise's complete() method was called
        // with a ModalResultStatus of CREATED
        assertThat(modalResultCaptor.getValue())
                .isNotNull()
                .extracting("status", InstanceOfAssertFactories.type(ModalResultStatus.class))
                .isEqualTo(ModalResultStatus.CANCELLED);
    }
}