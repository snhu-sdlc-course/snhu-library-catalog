package edu.snhu.library.exceptions;

public class FxmlFileNotLoadedException extends RuntimeException {
    public FxmlFileNotLoadedException(final String fileName) {
        super("FXML file [" + fileName + "] could not be loaded...");
    }
}
