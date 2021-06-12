package app.hotel;


import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"app.database.repositories"})
@EntityScan(basePackages = {"app.database.entities"})
public class App {
    public static void main(String[] args) {
        Application.launch(Main.class, args);

    }
}
