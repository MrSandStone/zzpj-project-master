package app.hotel;

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
public class Initializer implements ApplicationListener<Main.StageReadyEvent> {

    @Value("classpath:/basic.fxml")
    private Resource chartResource;

    private String title;
    private ApplicationContext applicationContext;

    public Initializer(@Value("${spring.javafx.title}") String title, ApplicationContext applicationContext) {
        this.title = title;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(Main.StageReadyEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(chartResource.getURL());
            fxmlLoader.setControllerFactory(AClass -> applicationContext.getBean(AClass));
            Parent parent = fxmlLoader.load();
            Stage stage = event.getStage();
            stage.setScene(new Scene(parent, 920, 720));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }
}
