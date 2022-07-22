package edu.snhu.library.controllers;

import edu.snhu.library.enums.ModalResultStatus;
import edu.snhu.library.models.Book;
import edu.snhu.library.models.ModalResult;
import edu.snhu.library.services.FxmlRootProvider;
import edu.snhu.library.viewmodels.EditBookViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Scope("prototype")
@Component
public class EditBookController extends AbstractDialogController<Book> {

    private final EditBookViewModel editBookViewModel;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextArea txtAuthors;

    @FXML
    private TextArea txtTags;

    EditBookController(final FxmlRootProvider fxmlRootProvider, final EditBookViewModel editBookViewModel) {
        super("edit_book.fxml", fxmlRootProvider);
        this.editBookViewModel = editBookViewModel;
    }

    public CompletableFuture<ModalResult<Book>> show(final Window parent, final Object ...args) {
        if(args.length < 1 || !Book.class.isAssignableFrom(args[0].getClass())) {
            throw new IllegalArgumentException("Second argument to show() must be a Book");
        }

        final Book book = Book.class.cast(args[0]);
        setSceneTitle("Edit Book [" + book.getTitle() + "]");
        editBookViewModel.loadFrom(book);
        return super.show(parent, args);
    }

    @FXML
    public void initialize() {
        txtTitle.textProperty().bindBidirectional(editBookViewModel.getTitleProperty());
        txtAuthors.textProperty().bindBidirectional(editBookViewModel.getAuthorProperty());
        txtTags.textProperty().bindBidirectional(editBookViewModel.getTagsProperty());
    }

    @FXML
    public void saveBook() {
        modalDialog.close();
        promise.complete(new ModalResult<>(ModalResultStatus.UPDATED, getViewModel().save()));
    }

    public void cancel() {
        modalDialog.close();
        promise.complete(new ModalResult<>(ModalResultStatus.CANCELLED, null));
    }

    @FXML
    public EditBookViewModel getViewModel() {
        return editBookViewModel;
    }
}
