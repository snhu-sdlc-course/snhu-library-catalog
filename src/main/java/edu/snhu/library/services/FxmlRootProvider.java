package edu.snhu.library.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class FxmlRootProvider {

    private final ApplicationContext ctx;

    FxmlRootProvider(final ApplicationContext ctx){
        this.ctx = ctx;
    }

    public Optional<Scene> getRoot(final Object controller, final String relativePath) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(FxmlRootProvider.class.getClassLoader().getResource(Path.of("fxml/", relativePath).toString()));
            fxmlLoader.setControllerFactory(clazz -> {
                if(clazz.isAssignableFrom(controller.getClass())) {
                    return controller;
                } else {
                    return ctx.getBean(clazz);
                }
            });
            return Optional.of(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
