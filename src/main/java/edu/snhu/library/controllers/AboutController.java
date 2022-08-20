package edu.snhu.library.controllers;

import edu.snhu.library.services.FxmlRootProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Scope("prototype")
@Component
public class AboutController extends AbstractDialogController<Void> {

    private final String version;
    private final String buildDate;
    private final String javaVersion;

    @Setter
    @FXML
    private Label lblVersion;

    @Setter
    @FXML
    private Label lblBuildDate;

    @Setter
    @FXML
    private Label lblJavaVersion;

    AboutController(
            @Value("${edu.snhu.library.version}") final String version,
            @Value("${edu.snhu.library.buildDate}") final String buildDate,
            @Value("${edu.snhu.library.javaVersion}") final String javaVersion,
            final FxmlRootProvider fxmlRootProvider
    ) {
        super("About SNHU Software Technical Catalog", "about.fxml", fxmlRootProvider);
        this.version = version;
        this.buildDate = buildDate;
        this.javaVersion = javaVersion;
    }

    /**
     * Called by JavaFX after @FXML annotations have been injected.
     */
    @FXML
    public void initialize() {
        lblVersion.setText(version);
        lblBuildDate.setText(buildDate);
        lblJavaVersion.setText(javaVersion);
    }
}
