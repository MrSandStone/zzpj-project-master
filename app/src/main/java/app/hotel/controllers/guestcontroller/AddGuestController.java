package app.hotel.controllers.guestcontroller;

import app.database.entities.Guest;
import app.database.exceptions.HotelException;
import app.database.exceptions.validations.Validator;
import app.hotel.controllers.AuxiliaryController;
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
public class AddGuestController {

    @FXML
    public TextField guestIDcard;

    @FXML
    private TextField guestName;

    @FXML
    private TextField guestSurname;

    @FXML
    private TextField guestPhonenumber;

    @FXML
    public TextField guestDiscount;

    private final GuestService guestService;
    private final Validator<HashMap<String, String>> validator;

    public AddGuestController(GuestService guestService,
                              @Qualifier("guest") Validator<HashMap<String, String>> validator) {
        this.guestService = guestService;
        this.validator = validator;
    }

    public void addGuest() {
        try {
            validator.validateInsert(new HashMap<>() {{
                put("guestIDcard", getGuestIDcard().getText());
                put("name", getGuestName().getText());
                put("surname", getGuestSurname().getText());
                put("phoneNumber", getGuestPhonenumber().getText());
                put("discount", getGuestDiscount().getText());
            }});
        } catch (HotelException hotelException) {
            generateAlert("Gość nie został dodany!",
                    hotelException.displayErrors(),
                    Alert.AlertType.ERROR);
            return;
        }

        Guest guest = new Guest();
        guest.setIDcard(getGuestIDcard().getText());
        guest.setName(getGuestName().getText());
        guest.setSurname(getGuestSurname().getText());
        guest.setPhoneNumber(Long.parseLong(getGuestPhonenumber().getText()));
        guest.setDiscount(Integer.parseInt(getGuestDiscount().getText()));

        guestService.insert(guest);
        switchMainWindow();
    }

    public void switchMainWindow() {
        AuxiliaryController.switchMainWindow();
    }
}
