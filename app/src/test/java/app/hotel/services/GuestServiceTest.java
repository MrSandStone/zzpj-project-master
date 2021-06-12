package app.hotel.services;

import app.database.entities.Guest;
import app.database.repositories.GuestRepository;
import app.hotel.App;
import app.hotel.services.implementation.GuestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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
public class GuestServiceTest {

    @Autowired
    private GuestService guestService;

    @MockBean
    private GuestRepository repository;

    @Test
    public void getGuestTest() {
        MockitoAnnotations.initMocks(this);
        when(repository.findAll()).thenReturn(Stream
                .of(new Guest("123455678912","Test","Test",123456789, 2))
                .collect(Collectors.toList()));
        assertEquals(1, guestService.findAll().size());
    }

    @Test
    public void getGuestByIDTest(){
        MockitoAnnotations.initMocks(this);
        when(repository.findById("123455678912"))
                .thenReturn(Optional.of(new Guest("123455678912", "Test", "Test", 123456789, 2)));

        assertNotNull(guestService.find("123455678912"));
    }

    @Test
    public void saveGuestTest() {
        MockitoAnnotations.initMocks(this);
        Guest guest = new Guest("123455678912", "Test", "Test", 123456789, 2);
        when(repository.save(guest)).thenReturn(guest);
        assertEquals(guest, guestService.insert(guest));
    }

    @Test
    public void deleteGuestTest() {
        MockitoAnnotations.initMocks(this);
        Guest guest = new Guest("123455678912", "Test", "Test", 123456789, 2);
        guestService.delete(guest);
        verify(repository, times(1)).delete(guest);
    }

    @Test
    public void modifyGuestTest() {
        MockitoAnnotations.initMocks(this);
        Guest guest = new Guest("123455678912", "Test", "Test2", 123456789, 2);
        guestService.update(guest);
        verify(repository, times(1)).save(guest);
    }
}
