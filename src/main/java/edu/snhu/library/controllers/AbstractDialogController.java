package edu.snhu.library.controllers;

import edu.snhu.library.enums.ModalResultStatus;
import edu.snhu.library.exceptions.FxmlFileNotLoadedException;
import edu.snhu.library.javafx.DialogController;
import edu.snhu.library.models.ModalResult;
import edu.snhu.library.services.FxmlRootProvider;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDialogController<T> implements DialogController<T> {

    protected Stage modalDialog;
    protected CompletableFuture<ModalResult<T>> promise;

    @Getter
    @Setter
    private String sceneTitle;

    private final String fxmlFileName;
    private final FxmlRootProvider fxmlRootProvider;

    AbstractDialogController(final String fxmlFileName, final FxmlRootProvider fxmlRootProvider) {
        this("", fxmlFileName, fxmlRootProvider);
    }

    AbstractDialogController(final String sceneTitle, final String fxmlFileName, final FxmlRootProvider fxmlRootProvider) {
        this.sceneTitle = sceneTitle;
        this.fxmlFileName = fxmlFileName;
        this.fxmlRootProvider = fxmlRootProvider;
    }

    @Override
    public CompletableFuture<ModalResult<T>> show(Window parent, Object... args) {
        final Optional<Scene> optAddBook = fxmlRootProvider.getRoot(this, fxmlFileName);

        promise = new CompletableFuture<>();

        optAddBook.ifPresent(scene -> {
            modalDialog = new Stage();
            modalDialog.setTitle(getSceneTitle());
            modalDialog.setScene(scene);
            modalDialog.initModality(Modality.APPLICATION_MODAL);
            modalDialog.initOwner(parent);
            modalDialog.show();
            modalDialog.setOnCloseRequest(e -> {
                modalDialog.close();
                promise.complete(new ModalResult(ModalResultStatus.CLOSED, null));
            });
        });
        if(optAddBook.isEmpty()) return CompletableFuture.failedFuture(new FxmlFileNotLoadedException(fxmlFileName));
        return promise;
    }
}
