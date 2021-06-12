package app.hotel.reportmakers;

import app.database.entities.Room;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static app.hotel.controllers.AuxiliaryController.generateAlert;

public class RoomReport {
    private final ArrayList<Room> rooms;
    private final Font polishFont;

    public RoomReport(ObservableList<Room> rooms) {

        this.rooms = new ArrayList<>(rooms);
        Font font;
        try {
            font = new Font(BaseFont
                    .createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED), 16);

        } catch (Exception e) {
            font = new Font();
        }
        this.polishFont = font;
    }

    public void generateReport() {
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(getFileName()));
            document.open();
            fillDocument(document);
            document.close();
            writer.close();
            generateAlert("", "Pomyślnie utworzono raport o nazwie " + getFileName(), Alert.AlertType.CONFIRMATION);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            generateAlert("", "Błąd poczas tworzenia raportu rezerwacji! ", Alert.AlertType.ERROR);
        }
    }

    private void fillDocument(Document d) throws DocumentException {
        addHeading(d);
        addAvailableRooms(d);
        addUnavailableRooms(d);
        addOccupiedRooms(d);
    }

    private void addHeading(Document d) {
        try {
            String s = "Raport dotyczący pokoi (stan na dzień " + LocalDate.now().toString() + ")";
            d.add(new Chunk(s, polishFont));
            d.add(new Paragraph("")); //new line
        } catch (DocumentException ignored) {

        }
    }

    private void addAvailableRooms(Document d) throws DocumentException {
        ArrayList<Room> availableRooms = rooms.stream()
                .filter(x -> x.getState().equals("dostępny")).collect(Collectors.toCollection(ArrayList::new));

        String paragraph = "Wolne pokoje - " + availableRooms.size();
        if (availableRooms.size() != 0)
            paragraph += ": ";
        d.add(new Paragraph(paragraph, polishFont));

        addListedRooms(d, availableRooms);
    }

    private void addUnavailableRooms(Document d) throws DocumentException {
        ArrayList<Room> unavailableRooms = rooms.stream()
                .filter(x -> x.getState().equals("niedostępny")).collect(Collectors.toCollection(ArrayList::new));

        String paragraph = "Niedostępne pokoje - " + unavailableRooms.size();
        if (unavailableRooms.size() != 0)
            paragraph += ": ";
        d.add(new Paragraph(paragraph, polishFont));

        addListedRooms(d, unavailableRooms);
    }

    private void addOccupiedRooms(Document d) throws DocumentException {
        ArrayList<Room> occupiedRooms = rooms.stream()
                .filter(x -> x.getState().equals("zajęty")).collect(Collectors.toCollection(ArrayList::new));

        String paragraph = "Zajęte pokoje - " + occupiedRooms.size();
        if (occupiedRooms.size() != 0)
            paragraph += ": ";
        d.add(new Paragraph(paragraph, polishFont));

        addListedRooms(d, occupiedRooms);

    }

    public void addListedRooms(Document d, ArrayList<Room> rooms) throws DocumentException {
        List orderedList = new List(List.UNORDERED);
        for (Room r : rooms)
            orderedList.add(new ListItem(r.getNumber()));
        if (rooms.size() != 0)
            d.add(orderedList);
    }

    private String getFileName() {
        int h = LocalDateTime.now().getHour();
        int m = LocalDateTime.now().getMinute();
        String dateAndClock = LocalDate.now().toString() + "_" + h + "-";
        if (m < 10)
            dateAndClock += "0";
        dateAndClock += m;
        String prefix = "room_report_";
        String suffix = ".pdf";
        return prefix + dateAndClock + suffix;
    }
}
