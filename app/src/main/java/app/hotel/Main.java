package app.hotel;

import app.hotel.controllers.InitializeController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;


public class Main extends Application {

    private static ConfigurableApplicationContext applicationContext;

    public static Stage mainStage;


    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(App.class).run();
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        applicationContext.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage primaryStage) {
            super(primaryStage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }


    public static void setScene(URL location, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        fxmlLoader.setControllerFactory(AClass -> applicationContext.getBean(AClass));
        Parent parent = fxmlLoader.load();
        if (width == 0 || height == 0 || location.toString().contains("basic.fxml")) {
            mainStage.setScene(new Scene(parent, 920, 720));
        } else
            mainStage.setScene(new Scene(parent, width, height));
    }

    public static void setScene(URL location, int width, int height, Object object) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        fxmlLoader.setControllerFactory(AClass -> applicationContext.getBean(AClass));
        Parent parent = fxmlLoader.load();
        mainStage.setScene(new Scene(parent, width, height));

        InitializeController controller = fxmlLoader.getController();
        controller.initData(object);
    }

}
