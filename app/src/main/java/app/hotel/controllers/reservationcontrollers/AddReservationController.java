package app.hotel.controllers.reservationcontrollers;

import app.database.entities.Guest;
import app.database.entities.Reservation;
import app.database.entities.Room;
import app.database.exceptions.HotelException;
import app.database.exceptions.validations.Validator;
import app.hotel.controllers.AuxiliaryController;
import app.hotel.controllers.InitializeController;
import app.hotel.services.implementation.ReservationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static app.hotel.controllers.AuxiliaryController.generateAlert;
import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Controller
public class AddReservationController implements Initializable, InitializeController {

    @FXML
    private ChoiceBox choiceBoxGuestId;

    @FXML
    private ChoiceBox choiceBoxRoomId;


    @FXML
    private DatePicker reservationStartDate;


    @FXML
    private DatePicker reservationEndDate;

    @FXML
    private TextField reservationTotalPrice;

    private ObservableList allRooms;
    private final ReservationService reservationService;
    private final Validator<HashMap<String, String>> validator;

    public void switchMainWindow() {
        AuxiliaryController.switchMainWindow();
    }

    @Autowired
    public AddReservationController(ReservationService reservationService,
                                    @Qualifier("reservation") Validator<HashMap<String, String>> validator) {
        this.reservationService = reservationService;
        this.validator = validator;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationStartDate.valueProperty().addListener((observable) -> checkRooms());
        reservationEndDate.valueProperty().addListener((observable) -> checkRooms());
        choiceBoxRoomId.valueProperty().addListener((observable) -> refreshDatesAfterRoomPick());
        choiceBoxGuestId.valueProperty().addListener((observable) -> totalPriceOfReservation());
    }

    @Override
    public void initData(Object object) {

        ArrayList<Object> objectList = (ArrayList<Object>) object;
        ObservableList<Guest> guestList = (ObservableList<Guest>) objectList.get(0);
        ObservableList<Room> roomList = ((ObservableList<Room>) objectList.get(1))
                .filtered(x -> !x.getState().equals("niedostępny")); //filtrowanie niedostÄ™pnych pokojĂłw
        choiceBoxSetData(guestList, roomList);
        setInitReservationDatePickersFactories();
    }

    public void addReservation() {
        try {
            validator.validateInsert(new HashMap<>() {{
                put("setStartDate", getReservationStartDate().getValue().toString());
                put("setEndDate", getReservationEndDate().getValue().toString());
            }});
        } catch (HotelException hotelException) {
            generateAlert("Rezerwacja nie została dodana!",
                    hotelException.displayErrors(),
                    Alert.AlertType.ERROR);
            return;
        }

        Reservation reservation = new Reservation();
        Guest g = (Guest) getChoiceBoxGuestId().getSelectionModel().getSelectedItem();
        Room r = (Room) getChoiceBoxRoomId().getSelectionModel().getSelectedItem();
        reservation.setGuestId(g.getIDcard());
        reservation.setRoomId(r.getNumber());
        reservation.setStartDate(LocalDate.parse(getReservationStartDate().getValue().toString()));
        reservation.setEndDate(LocalDate.parse(getReservationEndDate().getValue().toString()));
        double cost = Double.parseDouble(getReservationTotalPrice().getText());
        reservation.setTotalPrice(cost + " PLN");
        reservation.setPayed(false);
        reservationService.insert(reservation);
        switchMainWindow();
    }

    private void totalPriceOfReservation() {
        // dziura między wyborem daty a wpisaniem jej do edytora
        // wywołanie gdy zostanie wybrany gość
        if (reservationStartDate.getValue() != null && reservationEndDate.getValue() != null
                && choiceBoxRoomId.getSelectionModel().getSelectedIndex() > -1
                && choiceBoxGuestId.getSelectionModel().getSelectedIndex() > -1) {
            Room room = (Room) getChoiceBoxRoomId().getSelectionModel().getSelectedItem();
            LocalDate startDate = LocalDate.parse(reservationStartDate.getValue().toString());
            LocalDate endDate = LocalDate.parse(getReservationEndDate().getValue().toString());


            Long daysBetween = DAYS.between(startDate, endDate);
            Float roomPrice = room.getPrice();
            double totalPrice = daysBetween * roomPrice;
            double discount = totalPrice * 0.01 * ((Guest) getChoiceBoxGuestId().getSelectionModel().getSelectedItem()).getDiscount();
            totalPrice -= discount;
            totalPrice = Math.round(totalPrice * 100.0) / 100.0;
            reservationTotalPrice.setText(String.valueOf(totalPrice));
        }
    }

    public void refreshDatesAfterRoomPick() {
        try {
            Room room = (Room) choiceBoxRoomId.getSelectionModel().getSelectedItem();
            ArrayList<Reservation> list = (ArrayList<Reservation>) reservationService.findAll();
            list = (ArrayList<Reservation>) list.stream().filter(x -> x.getRoomId().equals(room.getNumber())).collect(Collectors.toList());
            Set<LocalDate> dateList = new HashSet<>();
            for (Reservation r : list) {
                LocalDate start = r.getStartDate().plusDays(1);
                LocalDate end = r.getEndDate().minusDays(1);
                if (start.isAfter(end))
                    start = start.minusDays(1);
                while (!start.isAfter(end)) {
                    dateList.add(start);
                    start = start.plusDays(1);
                }
            }
            refreshDayCellFactories(dateList);
        } catch (NullPointerException ignored) {

        }
    }

    private void refreshDayCellFactories(Set<LocalDate> dateList) {
        reservationStartDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // koliduje dzien
                if (empty || dateList.contains(item)) {
                    setStyle("-fx-background-color: #ff9ebe;");
                }
                // przeszle dni
                LocalDate today = LocalDate.now();
                if (empty || item.compareTo(today) < 0) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ff9ebe;");
                }
                // zaznaczone
                if (reservationSelectedDays(item, empty).contains(item))
                    setStyle("-fx-background-color: #8efffd;");

            }
        });

        reservationEndDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // koliduje dzien
                if (empty || dateList.contains(item))
                    setStyle("-fx-background-color: #ff9ebe;");

                // dni przed start date
                if (reservationStartDate.getValue() != null) {
                    if (item.isBefore(reservationStartDate.getValue().plusDays(1))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ff9ebe;");
                    }
                }
                // zaznaczone
                if (reservationSelectedDays(item, empty).contains(item))
                    setStyle("-fx-background-color: #8efffd;");
            }
        });
    }

    private ArrayList<LocalDate> reservationSelectedDays(LocalDate item, boolean empty) {
        ArrayList<LocalDate> al = new ArrayList<>();
        LocalDate start = reservationStartDate.getValue();
        LocalDate end = reservationEndDate.getValue();
        if (start == null || end == null)
            return new ArrayList<>(Collections.singleton(LocalDate.of(3010, 10, 10)));
        while (!start.isAfter(end)) {
            if (empty || start.isEqual(item))
                al.add(item);
            start = start.plusDays(1);
        }
        return al;
    }

    private void checkRooms() {
        if (reservationStartDate.getValue() != null && reservationEndDate.getValue() != null) {
            List<Reservation> reservations = reservationService.findAll();
            ArrayList list = new ArrayList<>(allRooms);
            for (Object room : allRooms) {
                for (int j = 0; j < reservations.size(); j++) {
                    if (ifConflict(reservations, j, (Room) room))
                        list.remove(room);
                }
            }
            choiceBoxRoomId.setItems(FXCollections.observableArrayList(list));
        }

    }

    private boolean ifConflict(List<Reservation> reservations, int j, Room room) {
        if (reservations.get(j).getRoomId().equals(room.getNumber())) {
            LocalDate rStartDate = reservations.get(j).getStartDate();
            LocalDate rEndDate = reservations.get(j).getEndDate();
            return !((!rStartDate.isBefore(reservationEndDate.getValue())
                    && !rEndDate.isBefore(reservationEndDate.getValue()))
                    || (!rStartDate.isAfter(reservationStartDate.getValue())
                    && !rEndDate.isAfter(reservationStartDate.getValue())));
        }
        return false;
    }


    private void choiceBoxSetData(ObservableList<Guest> guestList, ObservableList<Room> roomList) {
        choiceBoxGuestId.setItems(guestList);
        choiceBoxRoomId.setItems(roomList);
        allRooms = roomList;
    }

    private void setInitReservationDatePickersFactories() {
        // uniemożliwienie rezerwacji przed dniem dzisiejszym
        reservationStartDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // przeszłe dni
                LocalDate today = LocalDate.now();
                if (empty || item.compareTo(today) < 0) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ff9ebe;");
                }

            }
        });
        // uniemożliwienie rezerwacji przed dniem startu rezerwacji
        reservationEndDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (reservationStartDate.getValue() != null)
                    if (item.isBefore(reservationStartDate.getValue().plusDays(1))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ff9ebe;");
                    }
            }
        });
    }
}
