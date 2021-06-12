package app.hotel.controllers.roomcontroller;

import app.database.entities.Room;
import app.database.exceptions.HotelException;
import app.database.exceptions.validations.Validator;
import app.hotel.controllers.AuxiliaryController;
import app.hotel.controllers.InitializeController;
import app.hotel.services.implementation.RoomService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

import static app.hotel.controllers.AuxiliaryController.generateAlert;

@Getter
@Controller
public class ModifyRoomController implements InitializeController {

    @FXML
    private TextField roomNumber;
    @FXML
    private TextField roomCapacity;
    @FXML
    private TextField roomPrice;
    @FXML
    private ChoiceBox roomStateChoiceBox;

    private Room selectedRoom;

    private final RoomService roomService;
    private final Validator<HashMap<String, String>> validator;

    public ModifyRoomController(RoomService roomService,
                             @Qualifier("room") Validator<HashMap<String, String>> validator) {
        this.roomService = roomService;
        this.validator = validator;
    }
    public void modifyRoom() {
        try {
            validator.validateUpdate(new HashMap<>() {{
                put("capacity", getRoomCapacity().getText());
                put("price", getRoomPrice().getText());
            }});
        }
        catch (HotelException hotelException){
            generateAlert("Pokoj nie został zaktualizowany!",
                    hotelException.displayErrors(),
                    Alert.AlertType.ERROR);
            return;
        }
        selectedRoom.setNumber(roomNumber.getText());
        selectedRoom.setCapacity(Integer.parseInt(roomCapacity.getText()));
        selectedRoom.setPrice(Float.parseFloat(roomPrice.getText()));
        selectedRoom.setState((String) roomStateChoiceBox.getSelectionModel().getSelectedItem());

        roomService.update(selectedRoom);
        switchMainWindow();

    }

    public void switchMainWindow() {
        AuxiliaryController.switchMainWindow();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initData(Object object) {
        selectedRoom = (Room) object;
        roomNumber.setText(selectedRoom.getNumber());
        roomCapacity.setText(String.valueOf(selectedRoom.getCapacity()));
        roomPrice.setText(String.valueOf(selectedRoom.getPrice()));
        roomStateChoiceBox.setItems(FXCollections.observableArrayList("dostępny", "niedostępny", "zajęty"));
        roomStateChoiceBox.getSelectionModel().select(selectedRoom.getState());

    }
}
