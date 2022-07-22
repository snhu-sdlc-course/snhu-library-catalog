package edu.snhu.library.controllers;

import edu.snhu.library.enums.ModalResultStatus;
import edu.snhu.library.models.Book;
import edu.snhu.library.models.ModalResult;
import edu.snhu.library.services.FxmlRootProvider;
import edu.snhu.library.viewmodels.AddBookViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class AddBookController extends AbstractDialogController<Book> {

    private final AddBookViewModel addBookViewModel;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextArea txtAuthors;

    @FXML
    private TextArea txtTags;

    AddBookController(final FxmlRootProvider fxmlRootProvider, final AddBookViewModel addBookViewModel) {
        super("Add Book", "add_book.fxml", fxmlRootProvider);
        this.addBookViewModel = addBookViewModel;
    }

    @FXML
    public void initialize() {
        txtTitle.textProperty().bindBidirectional(addBookViewModel.getTitleProperty());
        txtAuthors.textProperty().bindBidirectional(addBookViewModel.getAuthorProperty());
        txtTags.textProperty().bindBidirectional(addBookViewModel.getTagsProperty());
    }

    @FXML
    public void addBook() {
        modalDialog.close();
        promise.complete(new ModalResult<>(ModalResultStatus.CREATED, getViewModel().save()));
    }

    @FXML
    public void cancel() {
        modalDialog.close();
        promise.complete(new ModalResult<>(ModalResultStatus.CANCELLED, null));
    }

    @FXML
    public AddBookViewModel getViewModel() {
        return addBookViewModel;
    }
}
