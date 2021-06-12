package app.hotel.controllers.reservationcontrollers;

import app.hotel.controllers.AuxiliaryController;
import app.hotel.services.implementation.GuestService;
import app.hotel.services.implementation.ReservationService;
import app.hotel.services.implementation.RoomService;
import app.hotel.reportmakers.ReservationReport;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.time.LocalDate;

import static app.hotel.controllers.AuxiliaryController.generateAlert;

@Getter
@Controller
public class RaportReservationController {


    @FXML
    private DatePicker reservationStartDate;

    @FXML
    private DatePicker reservationEndDate;

    private final ReservationService reservationService;
    private RoomService roomService;
    private GuestService guestService;


    @Autowired
    public RaportReservationController(ReservationService reservationService, RoomService roomService,GuestService guestService) {
        this.reservationService = reservationService;
        this.guestService = guestService;
        this.roomService = roomService;
    }

    public void generateReportButtonFunction() throws ParseException {

        LocalDate dateFrom = getReservationStartDate().getValue();
        LocalDate dateTo = getReservationEndDate().getValue();

        if (dateFrom == null && dateTo == null) {
            generateAlert("", "Jedna z podanych dat nie może być pusta", Alert.AlertType.INFORMATION);
            return;
        }
        if (dateFrom == null)
            dateFrom = LocalDate.of(1900, 1, 1);
        if (dateTo == null)
            dateTo = LocalDate.of(2100, 1, 1);

        ReservationReport rr = new ReservationReport(dateFrom, dateTo, reservationService, roomService, guestService);

        rr.generateReport();
        switchMainWindow();

    }

    public void switchMainWindow() {
        AuxiliaryController.switchMainWindow();
    }
}
