package app.hotel.controllers.roomcontroller;

import app.database.entities.Room;
import app.database.exceptions.HotelException;
import app.database.exceptions.validations.Validator;
import app.hotel.controllers.AuxiliaryController;
import app.hotel.services.implementation.RoomService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

import static app.hotel.controllers.AuxiliaryController.generateAlert;

@Getter
@Controller
public class AddRoomController  {

    @FXML
    private TextField roomNumber;
    @FXML
    private TextField roomCapacity;
    @FXML
    private TextField roomPrice;

    private final RoomService roomService;
    private final Validator<HashMap<String, String>> validator;

    public AddRoomController(RoomService roomService,
                             @Qualifier("room") Validator<HashMap<String, String>> validator) {
        this.roomService = roomService;
        this.validator = validator;
    }

    public void addRoom() {
        try {
            validator.validateInsert(new HashMap<>() {{
                put("capacity", getRoomCapacity().getText());
                put("price", getRoomPrice().getText());
            }});
        }
        catch (HotelException hotelException){
            generateAlert("Pokoj nie został dodany!",
                    hotelException.displayErrors(),
                    Alert.AlertType.ERROR);
            return;
        }
        Room room = new Room();
        room.setNumber(getRoomNumber().getText());
        room.setCapacity(Integer.parseInt(getRoomCapacity().getText()));
        room.setPrice(Float.parseFloat(getRoomPrice().getText()));
        room.setState("dostępny");

        roomService.insert(room);
        switchMainWindow();
    }

    public void switchMainWindow() {
        AuxiliaryController.switchMainWindow();
    }

}
