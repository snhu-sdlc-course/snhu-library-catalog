package edu.snhu.library.controllers;

import edu.snhu.library.javafxext.JavaFxExtension;
import edu.snhu.library.javafxext.TestInJfxThread;
import edu.snhu.library.services.FxmlRootProvider;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(JavaFxExtension.class) // Allow running test methods in a JavaFX thread
@ExtendWith(MockitoExtension.class)
class AboutControllerTest {

    @Mock private Label lblVersion;
    @Mock private Label lblBuildDate;
    @Mock private Label lblJavaVersion;
    @Mock private FxmlRootProvider rootProvider;

    private final String expectedVersion = "1.0.0";
    private final String expectedBuildDate = "January 1, 2022";
    private final String expectedJavaVersion = "17.0.1";

    private AboutController controller;

    @BeforeEach
    void init() {
        controller = new AboutController(expectedVersion, expectedBuildDate, expectedJavaVersion, rootProvider);
    }

    @TestInJfxThread // Because we mock a JavaFX component, we must run in a JavaFX thread
    @Test
    void testInitialize() {
        // Inject our mocks of Label controls
        controller.setLblVersion(lblVersion);
        controller.setLblBuildDate(lblBuildDate);
        controller.setLblJavaVersion(lblJavaVersion);

        // Execute the initialize() method we are testing
        controller.initialize();

        // Verify our mocks were called as expected
        verify(lblVersion).setText(expectedVersion);
        verify(lblBuildDate).setText(expectedBuildDate);
        verify(lblJavaVersion).setText(expectedJavaVersion);
    }

}