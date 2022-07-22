package edu.snhu.library.javafx;

import edu.snhu.library.models.ModalResult;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public interface DialogController<P> {
    CompletableFuture<ModalResult<P>> show(final Window parent, final Object ...args);
}
