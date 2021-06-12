package app.hotel.services.implementation;

import app.database.entities.Room;
import app.database.repositories.RoomRepository;
import app.hotel.services.RoomServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService implements RoomServiceInterface {
    private RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Optional<Room> find(String id) {
        Optional<Room> room = this.roomRepository.findById(id);
        return room;
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = this.roomRepository.findAll();
        return rooms;
    }

    @Override
    public Room insert( Room entity) {
        this.roomRepository.insert(entity);
        return entity;
    }

    @Override
    public void update( Room entity) {
        this.roomRepository.save(entity);
    }

    @Override
    public void delete( Room entity) {
        this.roomRepository.delete(entity);
    }
}
