package app.hotel.controllers;

import app.hotel.Main;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;

public class AuxiliaryController {
    public static void switchMainWindow() {
        URL mainWindowLocation = Main.class.getResource("/" + "basic.fxml");
        changeScene(mainWindowLocation, 0, 0);
    }

    public static void changeScene(URL url, int width, int height) {
        try {
            Main.setScene(url, width, height);
        } catch (IOException e) {
            System.err.println(url);
            e.printStackTrace();
        }
    }

    public static void changeScene(URL url, int width, int height, Object object) {
        try {
            Main.setScene(url, width, height, object);
        } catch (IOException e) {
            System.err.println(url);
            e.printStackTrace();
        }
    }

    public static void generateAlert(String headerText, String text, Alert.AlertType alertType) {
        String title;
        switch (alertType) {
            case ERROR:
                title = "Błąd";
                break;
            case WARNING:
                title = "Ostrzeżenie";
                break;
            case CONFIRMATION:
                title = "Potwierdzenie";
                break;
            case INFORMATION:
                title = "Informacja";
                break;
            default:
                title = "Alert";
        }
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(text);
        alert.showAndWait();
    }

}
