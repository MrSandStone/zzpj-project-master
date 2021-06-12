package app.hotel.controllers.guestcontroller;

import app.database.entities.Guest;
import app.database.exceptions.HotelException;
import app.database.exceptions.validations.Validator;
import app.hotel.controllers.AuxiliaryController;
import app.hotel.controllers.InitializeController;
import app.hotel.services.implementation.GuestService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

import static app.hotel.controllers.AuxiliaryController.generateAlert;

@Controller
@Getter
public class ModifyGuestController implements InitializeController {

    @FXML
    public TextField guestIDCard;

    @FXML
    private TextField guestName;

    @FXML
    private TextField guestSurname;

    @FXML
    private TextField guestPhonenumber;

    @FXML
    public TextField guestDiscount;


    private Guest selectedGuest;
    private final GuestService guestService;
    private final Validator<HashMap<String, String>> validator;

    public ModifyGuestController(GuestService guestService,
                                 @Qualifier("guest") Validator<HashMap<String, String>> validator) {
        this.guestService = guestService;
        this.validator = validator;
    }


    public void initData(Object object) {
        selectedGuest = (Guest) object;
        guestName.setText(selectedGuest.getName());
        guestSurname.setText(selectedGuest.getSurname());
        guestPhonenumber.setText(String.valueOf(selectedGuest.getPhoneNumber()));
        guestDiscount.setText(String.valueOf(selectedGuest.getDiscount()));
    }

    public void modifyGuest() {
        try {
            validator.validateUpdate(new HashMap<>() {{
                put("phoneNumber", guestPhonenumber.getText());
                put("discount", guestDiscount.getText());
            }});
        } catch (HotelException hotelException) {
            generateAlert("Gość nie został zaktualizowany!",
                    hotelException.displayErrors(),
                    Alert.AlertType.ERROR);
            return;
        }

        selectedGuest.setName(guestName.getText());
        selectedGuest.setSurname(guestSurname.getText());
        selectedGuest.setPhoneNumber(Integer.parseInt(guestPhonenumber.getText()));
        selectedGuest.setDiscount(Integer.parseInt(guestDiscount.getText()));
        guestService.update(selectedGuest);
        switchMainWindow();
    }

    public void switchMainWindow() {
        AuxiliaryController.switchMainWindow();
    }
}
