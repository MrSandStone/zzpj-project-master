package app.hotel.controllers.reservationcontrollers;

import app.database.entities.Guest;
import app.database.entities.Reservation;
import app.database.entities.Room;
import app.database.exceptions.HotelException;
import app.database.exceptions.validations.Validator;
import app.hotel.controllers.AuxiliaryController;
import app.hotel.controllers.InitializeController;
import app.hotel.services.implementation.GuestService;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static app.hotel.controllers.AuxiliaryController.generateAlert;
import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Controller
public class ModifyReservationController implements Initializable, InitializeController {

    @FXML
    private TextField reservationId;


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

    @FXML
    private ChoiceBox reservationIdPayed;

    private ObservableList allRooms;
    private final ReservationService reservationService;
    private GuestService guestService;
    private double previousPrice;
    private Reservation selectedReservation;
    private final Validator<HashMap<String, String>> validator;

    @Autowired
    public ModifyReservationController(ReservationService reservationService,
                                       @Qualifier("reservation") Validator<HashMap<String, String>> validator) {
        this.reservationService = reservationService;
        this.validator = validator;
    }

    @Override
    public void initData(Object object) {
        ArrayList<Object> objectList = (ArrayList<Object>) object;
        selectedReservation = (Reservation) objectList.get(0);
        ObservableList<Guest> guestList = (ObservableList<Guest>) objectList.get(1);
        ObservableList<Room> roomList = (ObservableList<Room>) objectList.get(2);
        guestService = (GuestService) objectList.get(3);

        modifyReservationSetData(guestList, roomList);
        this.previousPrice = countTotalPricePLN();
        reservationTotalPrice.setText(previousPrice + " PLN");
        setDayCellFactories();
        allRooms = roomList;
    }

    private ArrayList<LocalDate> reservationSelectedDays(LocalDate item, boolean empty) {
        ArrayList<LocalDate> al = new ArrayList<>();
        LocalDate start = reservationStartDate.getValue();
        LocalDate end = reservationEndDate.getValue();
        while (!start.isAfter(end)) {
            if (empty || start.isEqual(item))
                al.add(item);
            start = start.plusDays(1);
        }
        return al;
    }

    private Set<LocalDate> occupiedDays() {
        if (!reservationStartDate.getValue().isBefore(reservationEndDate.getValue()))
            return null;
        try {
            Room room = (Room) choiceBoxRoomId.getSelectionModel().getSelectedItem();

            ArrayList<Reservation> list = (ArrayList<Reservation>) reservationService.findAll();
            list = (ArrayList<Reservation>) list.stream()
                    .filter(x -> x.getRoomId().equals(room.getNumber())) // rezerwacje tego samego pokoju
                    .filter(x -> !x.getId().equals(reservationId.getText())) // odrzucenie obecnej
                    .collect(Collectors.toList());

            return generateOccupiedDays(list);
        } catch (NullPointerException ignored) {

        }
        return null;
    }

    private Set<LocalDate> generateOccupiedDays(ArrayList<Reservation> list) {
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
        return dateList;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationStartDate.valueProperty().addListener((observable) -> refreshRoomsAfterDatePick());
        reservationEndDate.valueProperty().addListener((observable) -> refreshRoomsAfterDatePick());
        choiceBoxGuestId.valueProperty().addListener((observable) -> totalPriceOfReservation());
        choiceBoxRoomId.valueProperty().addListener((observable) -> totalPriceOfReservation());
    }

    public void switchMainWindow() {
        AuxiliaryController.switchMainWindow();
    }

    public void modifyReservation() throws ParseException {

        try {
            validator.validateUpdate(new HashMap<>() {{
                put("setStartDate", getReservationStartDate().getValue().toString());
                put("setEndDate", getReservationEndDate().getValue().toString());
            }});
        } catch (HotelException hotelException) {
            generateAlert("Rezerwacja nie została zaktualizowana!",
                    hotelException.displayErrors(),
                    Alert.AlertType.ERROR);
            return;
        }
        selectedReservation.setId(reservationId.getText());
        Room room = (Room) choiceBoxRoomId.getSelectionModel().getSelectedItem();
        selectedReservation.setRoomId(room.getNumber());
        Guest guest = (Guest) choiceBoxGuestId.getSelectionModel().getSelectedItem();
        selectedReservation.setGuestId(guest.getIDcard());
        checkIfOtherPrice();
        selectedReservation.setTotalPrice(reservationTotalPrice.getText());
        selectedReservation.setPayed(false);

        //data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateFormat originalFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (reservationStartDate.getValue() == null) {
            Date date = originalFormat.parse(reservationStartDate.getEditor().getText());
            String formattedDate = targetFormat.format(date);
            selectedReservation.setStartDate(LocalDate.parse(formattedDate));
        } else {
            selectedReservation.setStartDate(LocalDate.parse(formatter.format(reservationStartDate.getValue())));
        }

        if (reservationEndDate.getValue() == null) {
            Date date = originalFormat.parse(reservationEndDate.getEditor().getText());
            String formattedDate = targetFormat.format(date);
            selectedReservation.setEndDate(LocalDate.parse(formattedDate));
        } else {
            selectedReservation.setEndDate(LocalDate.parse(formatter.format(reservationEndDate.getValue())));
        }

        List<Reservation> reservations = reservationService.findAll();
        for (Reservation reservation : reservations) {
            if (ifConflict(reservation, (Room) room)) {
                generateAlert("", "Wybrany termin jest już zarezerwowany.", Alert.AlertType.ERROR);
                return;
            }
        }

        
        reservationService.update(selectedReservation);
        switchMainWindow();
    }

    private void checkIfOtherPrice() {
        double totalPrice = Double.parseDouble(reservationTotalPrice.getText().split(" ")[0]);
        if (previousPrice > 0 && selectedReservation.isPayed() && reservationTotalPrice.getText().length() > 0) {
            subtractDiscountFromGuest();
            double difference = previousPrice - totalPrice;
            double differenceABS = Math.abs(Math.round(difference * 100.0) / 100.0);
            if (difference > 0) {
                generateAlert("", "Należy oddać klientowi " + differenceABS + ".", Alert.AlertType.INFORMATION);
            } else if (difference < 0) {
                generateAlert("", "Klient musi dopłacić " + differenceABS + ".", Alert.AlertType.INFORMATION);
            }
        }
    }

    private void refreshRoomsAfterDatePick() {
        // jak obydwie daty są wybrane to go
        if (reservationStartDate.getValue() != null && reservationEndDate.getValue() != null) {
            try {
                // wybranie listy dostępnych pokoi do rezerwacji w wybranym przedziale
                List<Reservation> reservations = reservationService.findAll();
                ArrayList list = new ArrayList<>(allRooms);
                for (Object room : allRooms) {
                    for (Reservation reservation : reservations) {
                        if (ifConflict(reservation, (Room) room))
                            list.remove(room);
                    }
                }
                // ustawienie listy pokoi dostępnych do rezerwacji
                choiceBoxRoomId.setItems(FXCollections.observableArrayList(list));
                // ponowne obliczenie ceny
                totalPriceOfReservation();

            } catch (NullPointerException ignored) {
            }
        }
    }

    private boolean ifConflict(Reservation reservation, Room room) {

        if (reservation.getId().equals(reservationId.getText()))
            return false;

        if (reservation.getRoomId().equals(room.getNumber())) {
            LocalDate rStartDate = reservation.getStartDate();
            LocalDate rEndDate = reservation.getEndDate();
            return !((!rStartDate.isBefore(reservationEndDate.getValue())
                    && !rEndDate.isBefore(reservationEndDate.getValue()))
                    || (!rStartDate.isAfter(reservationStartDate.getValue())
                    && !rEndDate.isAfter(reservationStartDate.getValue())));
        }
        return false;
    }

    private void setDayCellFactories() {
        reservationStartDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // przeszle dni
                LocalDate today = LocalDate.now();
                if (empty || item.compareTo(today) < 0) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ff9ebe;");
                }
                // zaznaczone
                if (reservationSelectedDays(item, empty).contains(item))
                    setStyle("-fx-background-color: #8efffd;");
                // koliduje dzien
                Set<LocalDate> occupiedDays = occupiedDays();
                if (occupiedDays != null)
                    if (occupiedDays.contains(item))
                        setStyle("-fx-background-color: #ff9ebe;");
            }
        });

        reservationEndDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
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
                // koliduje dzien
                Set<LocalDate> occupiedDays = occupiedDays();
                if (occupiedDays != null)
                    if (occupiedDays.contains(item))
                        setStyle("-fx-background-color: #ff9ebe;");
            }
        });
    }

    private void subtractDiscountFromGuest() {
        Optional<Guest> guestOpt = guestService.find(selectedReservation.getGuestId());
        if (guestOpt.isEmpty())
            return;
        Guest guest = guestOpt.get();
        int discount = guest.getDiscount();
        if (discount > 0) {
            guest.setDiscount(discount - 1);
            guestService.update(guest);
        }
    }

    private int getChoiceBoxGuestIndex(ObservableList<Guest> guestList) {
        for (int i = 0; i < guestList.size(); i++) {
            if (guestList.get(i).getIDcard().equals(selectedReservation.getGuestId())) {
                return i;
            }
        }
        return -1;
    }

    private int getChoiceBoxRoomIndex(ObservableList<Room> roomList) {
        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).getNumber().equals(selectedReservation.getRoomId())) {
                return i;
            }
        }
        return -1;
    }

    private void modifyReservationSetData(ObservableList<Guest> guestList, ObservableList<Room> roomList) {
        reservationId.setText(selectedReservation.getId());
        reservationStartDate.setValue(selectedReservation.getStartDate());
        reservationEndDate.setValue(selectedReservation.getEndDate());
        reservationTotalPrice.setText(String.valueOf(selectedReservation.getTotalPrice()));
        reservationIdPayed.setItems(FXCollections.observableArrayList("opłacona", "nieopłacona"));
        reservationIdPayed.getSelectionModel().select(stateBooleanToString(selectedReservation.isPayed()));

        int guestIndex = getChoiceBoxGuestIndex(guestList);
        int roomIndex = getChoiceBoxRoomIndex(roomList);
        choiceBoxSetData(guestList, roomList);
        choiceBoxGuestId.getSelectionModel().select(guestIndex);
        choiceBoxRoomId.getSelectionModel().select(roomIndex);
    }

    private void choiceBoxSetData(ObservableList<Guest> guestList, ObservableList<Room> roomList) {
        choiceBoxGuestId.setItems(guestList);
        choiceBoxRoomId.setItems(roomList);
    }

    private void totalPriceOfReservation() {
        // sprawdzenie czy wszystkie pola wymagane do obliczenia ceny sa wypelnione
        if (reservationStartDate.getValue() != null && reservationEndDate.getValue() != null
                && choiceBoxRoomId.getSelectionModel().getSelectedIndex() > -1
                && choiceBoxGuestId.getSelectionModel().getSelectedIndex() > -1) {
            // obliczenie ceny całkowitej w PLN
            double totalPrice = countTotalPricePLN();
            reservationTotalPrice.setText(totalPrice + " PLN");
        }
    }

    private double countTotalPricePLN() {
        Room room = (Room) choiceBoxRoomId.getSelectionModel().getSelectedItem();
        LocalDate startDate = LocalDate.parse(reservationStartDate.getValue().toString());
        LocalDate endDate = LocalDate.parse(getReservationEndDate().getValue().toString());

        Long daysBetween = DAYS.between(startDate, endDate);
        Float roomPrice = room.getPrice();
        double totalPrice = daysBetween * roomPrice;
        double discount = totalPrice * 0.01 * ((Guest) getChoiceBoxGuestId().getSelectionModel().getSelectedItem()).getDiscount();
        totalPrice -= discount;
        return totalPrice;
    }

    private boolean stateStringToBoolean(String state) {
        return state.equals("opłacona");
    }

    private int stateBooleanToString(Boolean state) {
        if (state) {
            return 0;
        } else {
            return 1;
        }
    }


}
