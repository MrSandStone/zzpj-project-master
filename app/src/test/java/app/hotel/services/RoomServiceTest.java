package app.hotel.services;



import app.database.entities.Guest;
import app.database.entities.Room;
import app.database.repositories.GuestRepository;
import app.database.repositories.RoomRepository;
import app.hotel.App;
import app.hotel.services.implementation.GuestService;
import app.hotel.services.implementation.RoomService;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @MockBean
    private RoomRepository repository;

    @Test
    public void getGuestTest() {
        when(repository.findAll()).thenReturn(Stream
                .of(new Room("1",3,30.2F,"dostępny"))
                .collect(Collectors.toList()));
        assertEquals(1, roomService.findAll().size());
    }

    @Test
    public void getGuestByIDTest(){
        when(repository.findById("1"))
                .thenReturn(Optional.of(new Room("1",3,30.2F,"dostępny")));

        assertNotNull(roomService.find("1"));
    }

    @Test
    public void saveGuestTest() {
        Room room = new Room("1",3,30.2F,"dostępny");
        when(repository.save(room)).thenReturn(room);
        assertEquals(room, roomService.insert(room));
    }

    @Test
    public void deleteGuestTest() {
        Room room = new Room("1",3,30.2F,"dostępny");
        roomService.delete(room);
        verify(repository, times(1)).delete(room);
    }

    @Test
    public void modifyGuestTest() {
        Room room = new Room("1",3,30.2F,"dostępny");
        roomService.update(room);
        verify(repository, times(1)).save(room);
    }
}
