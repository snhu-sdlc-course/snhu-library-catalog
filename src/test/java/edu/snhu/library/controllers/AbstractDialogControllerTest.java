package edu.snhu.library.controllers;

import edu.snhu.library.javafxext.JavaFxExtension;
import edu.snhu.library.javafxext.TestInJfxThread;
import edu.snhu.library.models.Book;
import edu.snhu.library.models.ModalResult;
import edu.snhu.library.services.FxmlRootProvider;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(JavaFxExtension.class)
@ExtendWith(MockitoExtension.class)
class AbstractDialogControllerTest {

    @Mock private FxmlRootProvider rootProvider;
    private AbstractDialogController<Book> abstractDialogController;

    @BeforeEach
    void init() {
        abstractDialogController = new AbstractDialogController<>("fxmlFileName.fxml", rootProvider) {};
    }

    @TestInJfxThread
    @Test
    void show(@Mock final Window parent, @Mock final Scene scene, @Mock final Parent root) {
        // Mock necessary components
        when(scene.getRoot()).thenReturn(root);
        when(rootProvider.getRoot(any(), any())).thenReturn(Optional.of(scene));

        // Invoke the method and capture the promise
        final CompletableFuture<ModalResult<Book>> promise = abstractDialogController.show(parent);

        // The promise should only complete when the user closes the modal somehow
        assertThat(promise).isNotCompleted();
    }

    @Test
    void show_NoScene(@Mock final Window parent) {
        // Mock necessary components
        when(rootProvider.getRoot(any(), any())).thenReturn(Optional.empty());

        // Invoke the method and capture the promise returned
        final CompletableFuture<ModalResult<Book>> promise = abstractDialogController.show(parent);

        // Confirm it completed with an exception -- it should because we mocked getRoot() to return an empty Optional
        assertThat(promise).isCompletedExceptionally();
    }
}