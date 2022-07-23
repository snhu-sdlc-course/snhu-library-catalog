package edu.snhu.library;

import edu.snhu.library.controllers.HomeController;
import edu.snhu.library.events.StageReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
