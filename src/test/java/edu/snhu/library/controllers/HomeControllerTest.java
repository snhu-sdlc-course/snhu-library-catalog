package edu.snhu.library.controllers;

import edu.snhu.library.enums.ModalResultStatus;
import edu.snhu.library.javafxext.JavaFxExtension;
import edu.snhu.library.javafxext.TestInJfxThread;
import edu.snhu.library.models.Book;
import edu.snhu.library.models.ModalResult;
import edu.snhu.library.services.FxmlRootProvider;
import edu.snhu.library.viewmodels.HomeViewModel;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(JavaFxExtension.class)
@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock private Application application;
    @Mock private AboutController aboutController;
    @Mock private AddBookController addBookController;
    @Mock private EditBookController editBookController;
    @Mock private FxmlRootProvider rootProvider;
    @Mock private HomeViewModel viewModel;
    private HomeController controller;

    @BeforeEach
    void init() {
        controller = new HomeController(application, aboutController, addBookController, editBookController, rootProvider, "appTitle", viewModel);
    }

    @Test
    void show(@Mock final Scene scene, @Mock final Stage stage) {
        // Mock out necessary components
        when(rootProvider.getRoot(any(), any())).thenReturn(Optional.of(scene));

        // Invoke the method under test
        controller.show(stage);

        // Confirm the window is configured and shown
        verify(stage).setScene(scene);
        verify(stage).setTitle("appTitle");
        verify(stage).show();
    }

    @TestInJfxThread
    @Test
    void initialize(
            @Mock final TableView<Book> booksTableView,
            @Mock final TextInputControl filterInput,
            @Mock final CheckComboBox<String> genreFilter,
            @Mock final IndexedCheckModel checkModel)
    {
        // Mock out necessary components
        when(viewModel.getFilterProperty()).thenReturn(new SimpleStringProperty());
        when(viewModel.getGenres()).thenReturn(FXCollections.emptyObservableList());
        when(filterInput.textProperty()).thenReturn(new SimpleStringProperty());
        when(genreFilter.getCheckModel()).thenReturn(checkModel);
        when(genreFilter.getItems()).thenReturn(FXCollections.emptyObservableList());
        when(checkModel.getCheckedItems()).thenReturn(FXCollections.emptyObservableList());

        controller.setBooksTableView(booksTableView);
        controller.setFilterInput(filterInput);
        controller.setGenreFilter(genreFilter);

        // Invoke the method under test
        controller.initialize();

        // Confirm the view model's data is loaded and the table view is set up
        verify(viewModel).loadBooks();
        verify(viewModel).getGenres();
        verify(booksTableView).setSortPolicy(any(Callback.class));
        verify(booksTableView).setRowFactory(any(Callback.class));
    }

    @Test
    void exitProgram() throws Exception {
        // Invoke the method under test
        controller.exitProgram(null);

        // Verify the JavaFX Application's stop() method was called
        verify(application).stop();
    }

    @Test
    void showAboutDialog(@Mock final MenuBar menuBar, @Mock final Scene scene, @Mock final Window window) {
        // Mock out necessary components
        when(menuBar.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(window);

        controller.setMenuBar(menuBar);

        // Invoke the method under test
        controller.showAboutDialog();

        // Verify the About dialog's show() method was called
        verify(aboutController).show(window);
    }

    @Test
    void addBook(@Mock final MenuBar menuBar, @Mock final Scene scene, @Mock final Window window, @Mock final CompletableFuture<ModalResult<Book>> completableFuture, @Mock TableView<Book> booksTableView) {
        final ArgumentCaptor<Consumer<ModalResult<Book>>> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);

        // Mock out necessary components
        when(menuBar.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(window);
        when(addBookController.show(window)).thenReturn(completableFuture);

        controller.setMenuBar(menuBar);
        controller.setBooksTableView(booksTableView);

        // Invoke the method under test
        controller.addBook();

        // Verify the promise was created and it's callback was executed
        verify(completableFuture).thenAccept(consumerCaptor.capture());
        verifyAddBookPromise(consumerCaptor.getValue(), booksTableView);
    }

    void verifyAddBookPromise(final Consumer<ModalResult<Book>> promise, final TableView<Book> booksTableView) {
        // The promise should only do something if ModalResultStatus is CREATED
        promise.accept(new ModalResult<>(ModalResultStatus.CREATED, null));
        promise.accept(new ModalResult<>(ModalResultStatus.OK, null));
        promise.accept(new ModalResult<>(ModalResultStatus.FAILED_TO_OPEN, null));
        promise.accept(new ModalResult<>(ModalResultStatus.CANCELLED, null));
        promise.accept(new ModalResult<>(ModalResultStatus.UPDATED, null));
        promise.accept(new ModalResult<>(ModalResultStatus.CLOSED, null));

        // While the promise has been invoked 6 times, it's if(){} condition should only have been satisfied once
        verify(viewModel).loadGenres();
        verify(viewModel).loadBooks();
        verify(booksTableView).sort();
    }

    @Test
    void removeBook() {
        // removeBook() should throw an exception until it's implemented
        assertThatThrownBy(() -> controller.removeBook()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void editSelectedBook(
            @Mock TableView<Book> booksTableView,
            @Mock final TableView.TableViewSelectionModel<Book> selectionModel,
            @Mock final ObservableList<Book> selectedItems,
            @Mock final MenuBar menuBar,
            @Mock final Scene scene,
            @Mock final Window window,
            @Mock final CompletableFuture<ModalResult<Book>> completableFuture)
    {
        final ArgumentCaptor<Consumer<ModalResult<Book>>> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        final Book book = new Book();

        // Mock out necessary components
        when(booksTableView.getSelectionModel()).thenReturn(selectionModel);
        when(selectionModel.getSelectedItems()).thenReturn(selectedItems);
        when(selectedItems.isEmpty()).thenReturn(false);
        when(selectionModel.getSelectedItem()).thenReturn(book);

        when(menuBar.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(window);
        when(editBookController.show(window, book)).thenReturn(completableFuture);

        controller.setBooksTableView(booksTableView);
        controller.setMenuBar(menuBar);

        // Invoke the method under test
        controller.editSelectedBook();

        // Verify the promise was created and it's callback was executed
        verify(completableFuture).thenAccept(consumerCaptor.capture());
        verifyEditBookPromise(consumerCaptor.getValue(), booksTableView);
    }

    void verifyEditBookPromise(final Consumer<ModalResult<Book>> promise, final TableView<Book> booksTableView) {
        // The promise should only do something if ModalResultStatus is UPDATED
        promise.accept(new ModalResult<>(ModalResultStatus.CREATED, null));
        promise.accept(new ModalResult<>(ModalResultStatus.OK, null));
        promise.accept(new ModalResult<>(ModalResultStatus.FAILED_TO_OPEN, null));
        promise.accept(new ModalResult<>(ModalResultStatus.CANCELLED, null));
        promise.accept(new ModalResult<>(ModalResultStatus.UPDATED, null));
        promise.accept(new ModalResult<>(ModalResultStatus.CLOSED, null));

        // While the promise has been invoked 6 times, it's if(){} condition should only have been satisfied once
        verify(viewModel).loadGenres();
        verify(viewModel).loadBooks();
        verify(booksTableView).sort();
    }

    @Test
    void editSelectedBook_NoSelection(
            @Mock TableView<Book> booksTableView,
            @Mock final TableView.TableViewSelectionModel<Book> selectionModel,
            @Mock final ObservableList<Book> selectedItems)
    {
        // Mock out necessary components
        when(booksTableView.getSelectionModel()).thenReturn(selectionModel);
        when(selectionModel.getSelectedItems()).thenReturn(selectedItems);
        when(selectedItems.isEmpty()).thenReturn(true);

        controller.setBooksTableView(booksTableView);

        // Invoke the method under test
        controller.editSelectedBook();

        // Confirm the selection model was never asked for the selected book
        verify(selectionModel, never()).getSelectedItem();
    }
}