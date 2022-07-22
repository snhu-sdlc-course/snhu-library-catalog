package edu.snhu.library;

import edu.snhu.library.controllers.HomeController;
import edu.snhu.library.events.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageReadyListener implements ApplicationListener<StageReadyEvent> {
    private final HomeController homeController;

    StageReadyListener(
            final HomeController homeController
    ) {
        this.homeController = homeController;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        homeController.show(event.getStage());
    }
}
