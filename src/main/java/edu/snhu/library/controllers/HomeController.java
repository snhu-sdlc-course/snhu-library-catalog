package edu.snhu.library.controllers;

import edu.snhu.library.enums.ModalResultStatus;
import edu.snhu.library.models.Book;
import edu.snhu.library.services.FxmlRootProvider;
import edu.snhu.library.viewmodels.HomeViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.control.CheckComboBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
public class HomeController {

    private final Application application;
    private final HomeViewModel viewModel;
    private final AboutController aboutController;
    private final AddBookController addBookController;
    private final EditBookController editBookController;
    private final FxmlRootProvider fxmlRootProvider;
    private final String appTitle;

    @Setter
    @FXML
    private MenuBar menuBar;

    @Setter
    @FXML
    private TableView<Book> booksTableView;

    @Setter
    @FXML
    private TextInputControl filterInput;

    @Setter
    @FXML
    private CheckComboBox<String> genreFilter;

    HomeController(
            final Application application,
            final AboutController aboutController,
            final AddBookController addBookController,
            final EditBookController editBookController,
            final FxmlRootProvider fxmlRootProvider,
            @Value("${edu.snhu.library.ui.title}") final String appTitle,
            final HomeViewModel viewModel
    ) {
        this.application = application;
        this.aboutController = aboutController;
        this.addBookController = addBookController;
        this.editBookController = editBookController;
        this.fxmlRootProvider = fxmlRootProvider;
        this.appTitle = appTitle;
        this.viewModel = viewModel;
    }

    public void show(final Stage stage) {
        final Optional<Scene> optRoot = fxmlRootProvider.getRoot(this, "home.fxml");
        optRoot.ifPresent(root -> {
            stage.setScene(root);
            stage.setTitle(appTitle);
            stage.show();
        });
    }

    public HomeViewModel getViewModel() {
        return viewModel;
    }

    @FXML
    void initialize() {
        // Set up filter property
        filterInput.textProperty().bindBidirectional(viewModel.getFilterProperty());

        // Set up genre filter
        genreFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super String>) c -> viewModel.getSelectedGenres().setAll(c.getList()));

        // Load the database to the list
        getViewModel().loadBooks();

        // Load genres
        getViewModel().loadGenres();

        // Update the genereFilter from viewcontroller
        genreFilter.getItems().addAll(viewModel.getGenres());
        viewModel.getGenres().addListener((ListChangeListener<String>) c -> genreFilter.getItems().setAll(c.getList()));

        // Offload the task of sorting to the database
        booksTableView.setSortPolicy(tv -> {
            if(tv.getSortOrder().size() < 1) {
                viewModel.updateSort(Sort.unsorted());
                return true;
            }

            final TableColumn<Book, ?> column = tv.getSortOrder().get(0);
            final Sort.Direction sortDir = column.getSortType() == TableColumn.SortType.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC;
            final String sortProp = ((PropertyValueFactory)column.cellValueFactoryProperty().get()).getProperty();
            viewModel.updateSort(Sort.by(sortDir, sortProp));

            return true;
        });

        // Detect row double clicks
        booksTableView.setRowFactory(tv -> {
            final TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getClickCount() == 2) {
                    editBook(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    void exitProgram(final ActionEvent evt) throws Exception {
        application.stop();
    }

    @FXML
    void showAboutDialog() {
        aboutController.show(menuBar.getScene().getWindow());
    }

    @FXML
    public void addBook() {
        addBookController.show(menuBar.getScene().getWindow()).thenAccept(results -> {
            if(results.status() == ModalResultStatus.CREATED) {
                viewModel.loadGenres();
                viewModel.loadBooks();
                booksTableView.sort();

                // For some reason, we have to defer this. Otherwise, it just scrolls to the top
                Platform.runLater(() -> {
                    booksTableView.getSelectionModel().select(results.payload());
                    booksTableView.requestFocus();
                    booksTableView.scrollTo(results.payload());
                });
            }
        });
    }

    @FXML
    public void removeBook() {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @FXML
    public void editSelectedBook() {
        if(booksTableView.getSelectionModel().getSelectedItems().isEmpty()) return;
        editBook(booksTableView.getSelectionModel().getSelectedItem());
    }

    public void editBook(final Book book) {
        editBookController.show(menuBar.getScene().getWindow(), book).thenAccept(results -> {
            if(results.status() == ModalResultStatus.UPDATED) {
                viewModel.loadGenres();
                viewModel.loadBooks();
                booksTableView.sort();

                Platform.runLater(() -> {
                    booksTableView.getSelectionModel().select(results.payload());
                    booksTableView.requestFocus();
                });
            }
        });
    }
}
