package edu.snhu.library.controllers;

import edu.snhu.library.enums.ModalResultStatus;
import edu.snhu.library.models.Book;
import edu.snhu.library.models.ModalResult;
import edu.snhu.library.services.FxmlRootProvider;
import edu.snhu.library.viewmodels.AddBookViewModel;
import javafx.stage.Stage;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddBookControllerTest {

    @Mock private Stage modalDialog;
    @Mock private CompletableFuture<ModalResult<Book>> promise;

    @Mock private FxmlRootProvider fxmlRootProvider;
    @Mock private AddBookViewModel addBookViewModel;
    @InjectMocks private AddBookController controller;

    @BeforeEach
    void init() {
        controller.modalDialog = modalDialog;
        controller.promise = promise;
    }

    @Test
    void addBook() {
        final ArgumentCaptor<ModalResult<Book>> modalResultCaptor = ArgumentCaptor.forClass(ModalResult.class);

        // Invoke the AddBookController.addBook() method
        controller.addBook();

        // Verify the modal dialog was closed
        verify(modalDialog).close();

        // Verify the promise's complete() method was called
        // and capture the argument it was called with
        verify(promise).complete(modalResultCaptor.capture());

        // Verify the new book was added
        verify(addBookViewModel).save();

        // Confirm the promise's complete() method was called
        // with a ModalResultStatus of CREATED
        assertThat(modalResultCaptor.getValue())
                .isNotNull()
                .extracting("status", InstanceOfAssertFactories.type(ModalResultStatus.class))
                .isEqualTo(ModalResultStatus.CREATED);

    }

    @Test
    void cancel() {
        final ArgumentCaptor<ModalResult<Book>> modalResultCaptor = ArgumentCaptor.forClass(ModalResult.class);

        // Invoke the AddBookController.addBook() method
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