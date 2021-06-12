package app.hotel.reportmakers;

import app.database.entities.Guest;
import app.database.entities.Reservation;
import app.database.entities.Room;
import app.hotel.services.implementation.GuestService;
import app.hotel.services.implementation.ReservationService;
import app.hotel.services.implementation.RoomService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static app.hotel.controllers.AuxiliaryController.generateAlert;

public class ReservationReport {

    private final LocalDate reportDateFrom;
    private final LocalDate reportDateTo;
    private final ArrayList<Reservation> reservations;
    private final Font polishFont12;
    private final Font polishFont16;
    private final RoomService rooms;
    private final GuestService guests;

    public ReservationReport(LocalDate reportDateFrom, LocalDate reportDateTo, ReservationService reservations, RoomService rooms, GuestService guests) {
        this.reportDateFrom = reportDateFrom;
        this.reportDateTo = reportDateTo;
        this.rooms = rooms;
        this.guests = guests;
        this.reservations = reservations.findAll().stream()
                .filter(x -> !reportDateFrom.isAfter(x.getStartDate()))
                .filter(x -> !reportDateTo.isBefore(x.getEndDate()))
                .collect(Collectors.toCollection(ArrayList::new));
        Font font;
        try {
            font = new Font(BaseFont
                    .createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED), 16);

        } catch (Exception e) {
            font = new Font();
        }
        this.polishFont16 = new Font(font);
        this.polishFont12 = new Font(font);
        polishFont12.setSize(12);
    }

    public void generateReport() {
        if (reportDateFrom.isAfter(reportDateTo)) {
            generateAlert("Błąd", "Data początkowa musi być wcześniej niż data zakończenia", Alert.AlertType.ERROR);
            return;
        }

        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(getFileName()));
            document.open();
            fillDocument(document);
            document.close();
            writer.close();
            generateAlert("", "Pomyślnie utworzono raport o nazwie " + getFileName(), Alert.AlertType.INFORMATION);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            generateAlert("", "Błąd poczas tworzenia raportu rezerwacji! ", Alert.AlertType.ERROR);
        }
    }

    private void fillDocument(Document d) throws DocumentException {
        addHeading(d);
        addUnfinishedReservations(d);
        addFinishedReservations(d);
        addNotPaidReservations(d);
        addPaidReservations(d);
    }

    private void addHeading(Document d) {
        try {
            String s = "Raport dotyczący rezerwacji z dn. " + LocalDate.now().toString();
            Chunk chunk = new Chunk(s, polishFont16);
            d.add(chunk);

            d.add(new Paragraph("")); //new line

            s = "\nPrzedział czasowy: " + reportDateFrom + " - " + reportDateTo;
            chunk = new Chunk(s, polishFont16);
            d.add(chunk);

            d.add(new Paragraph("")); //new line
        } catch (DocumentException ignored) {

        }
    }

    private void addUnfinishedReservations(Document d) throws DocumentException {
        ArrayList<Reservation> notFinished = reservations.stream()
                .filter(x -> x.getEndDate().isAfter(LocalDate.now()))
                .collect(Collectors.toCollection(ArrayList::new));


        String paragraph = "Rezerwacje niezakończone - " + notFinished.size();
        if (notFinished.size() != 0)
            paragraph += ": ";
        d.add(new Paragraph(paragraph, polishFont16));

        addReservationList(d, notFinished);
    }

    private void addFinishedReservations(Document d) throws DocumentException {
        ArrayList<Reservation> finished = reservations.stream()
                .filter(x -> !x.getEndDate().isAfter(LocalDate.now()))
                .collect(Collectors.toCollection(ArrayList::new));

        String paragraph = "Rezerwacje zakończone - " + finished.size();
        if (finished.size() != 0)
            paragraph += ": ";
        d.add(new Paragraph(paragraph, polishFont16));

        addReservationList(d, finished);
    }

    private void addNotPaidReservations(Document d) throws DocumentException {
        ArrayList<Reservation> notPaid = reservations.stream()
                .filter(x -> !x.isPayed())
                .collect(Collectors.toCollection(ArrayList::new));

        String paragraph = "Rezerwacje nieopłacone - " + notPaid.size();
        if (notPaid.size() != 0)
            paragraph += ": ";
        d.add(new Paragraph(paragraph, polishFont16));

        addReservationList(d, notPaid);
    }

    private void addPaidReservations(Document d) throws DocumentException {
        ArrayList<Reservation> paid = reservations.stream()
                .filter(Reservation::isPayed)
                .collect(Collectors.toCollection(ArrayList::new));

        String paragraph = "Rezerwacje opłacone - " + paid.size();
        if (paid.size() != 0)
            paragraph += ": ";
        d.add(new Paragraph(paragraph, polishFont16));

        addReservationList(d, paid);
    }

    private void addReservationList(Document d, ArrayList<Reservation> reservations) throws DocumentException {
        List orderedList = new List(List.UNORDERED);
        for (Reservation r : reservations)
            orderedList.add(new ListItem(generateReservationEntry(r), polishFont12));
        d.add(orderedList);
    }

    private String generateReservationEntry(Reservation reservation) {
        Guest guest = new Guest();
        for (Guest tmpGuest : guests.findAll()) {
            if (tmpGuest.getIDcard().equals(reservation.getGuestId()))
                guest = tmpGuest;
        }
        Room room = new Room();
        for (Room tmpRoom : rooms.findAll()) {
            if (tmpRoom.getNumber().equals(reservation.getRoomId()))
                room = tmpRoom;
        }
        return reservation.getId() +
                " (" +
                reservation.getStartDate() +
                " - " +
                reservation.getEndDate() +
                ") - Gość: " +
                guest.getName() +
                " " +
                guest.getSurname() +
                ", Pokój: " +
                room.getNumber();
    }

    private String getFileName() {
        int h = LocalDateTime.now().getHour();
        int m = LocalDateTime.now().getMinute();
        String dateAndClock = LocalDate.now().toString() + "_" + h + "-";
        if (m < 10)
            dateAndClock += "0";
        dateAndClock += m;
        String prefix = "reservation_report_";
        String suffix = ".pdf";
        return prefix + dateAndClock + suffix;
    }

}
