package app.hotel;

import app.database.entities.Guest;
import app.database.entities.Reservation;
import app.database.entities.Room;
import app.database.repositories.GuestRepository;
import app.database.repositories.ReservationRepository;
import app.database.repositories.RoomRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@ActiveProfiles(profiles = "test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DBConnectionTest{

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void connectionGuestRepository() {
        Guest guest = guestRepository
                .save(new Guest("123455678912","Test","Test",123456789, 2));
        List<Guest> foundEntity = guestRepository.findAll();

        assertNotNull(foundEntity);
        int last = foundEntity.size();
        assertEquals(guest.getIDcard(), foundEntity.get(last-1).getIDcard());
    }

    @Test
    public void connectionRoomRepository() {
        Room room = roomRepository
                .save(new Room("1",3,30.2F,"dostępny"));
        List<Room> foundEntity = roomRepository.findAll();

        assertNotNull(foundEntity);
        int last = foundEntity.size();
        assertEquals(room.getNumber(), foundEntity.get(last-1).getNumber());
    }

    @Test
    public void connectionReservationRepository() {
        Reservation reservation = reservationRepository
                .save(new Reservation("12","12345678910","2",
                        LocalDate.parse("2020-05-01"),LocalDate.parse("2020-05-11"),"1000.00 PLN",false));
        List<Reservation> foundEntity = reservationRepository.findAll();

        assertNotNull(foundEntity);
        int last = foundEntity.size();
        assertEquals(reservation.getId(), foundEntity.get(last-1).getId());
    }


}

