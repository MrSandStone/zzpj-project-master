package app.hotel.services;
import app.database.entities.Guest;
import app.database.entities.Reservation;
import app.database.repositories.GuestRepository;
import app.database.repositories.ReservationRepository;
import app.hotel.App;
import app.hotel.services.implementation.GuestService;
import app.hotel.services.implementation.ReservationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ReservarionServiceTest {

    @Autowired
    private ReservationService reservationService;

    @MockBean
    private ReservationRepository repository;

    @Test
    public void getGuestTest() {
        when(repository.findAll()).thenReturn(Stream
                .of(new Reservation("12","12345678910","2",
                        LocalDate.parse("2020-05-01"),LocalDate.parse("2020-05-10"),"1000.00 PLN",false))
                .collect(Collectors.toList()));
        assertEquals(1, reservationService.findAll().size());
    }

    @Test
    public void getGuestByIDTest(){
        when(repository.findById("123455678912"))
                .thenReturn(Optional.of(new Reservation("12","12345678910","2",
                        LocalDate.parse("2020-05-01"),LocalDate.parse("2020-05-10"),"1000.00 PLN",false)));

        assertNotNull(reservationService.find("123455678912"));
    }

    @Test
    public void saveGuestTest() {
        Reservation guest = new Reservation("12","12345678910","2",
                LocalDate.parse("2020-05-01"),LocalDate.parse("2020-05-10"),"1000.00 PLN",false);
        when(repository.save(guest)).thenReturn(guest);
        assertEquals(guest, reservationService.insert(guest));
    }

    @Test
    public void deleteGuestTest() {
        Reservation guest = new Reservation("12","12345678910","2",
                LocalDate.parse("2020-05-01"),LocalDate.parse("2020-05-10"),"1000.00 PLN",false);
        reservationService.delete(guest);
        verify(repository, times(1)).delete(guest);
    }

    @Test
    public void modifyGuestTest() {
        Reservation guest = new Reservation("12","12345678910","2",
                LocalDate.parse("2020-05-01"),LocalDate.parse("2020-05-10"),"1000.00 PLN",false);
        reservationService.update(guest);
        verify(repository, times(1)).save(guest);
    }
}
