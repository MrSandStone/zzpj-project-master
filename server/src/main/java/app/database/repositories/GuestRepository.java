package app.database.repositories;

import app.database.entities.Guest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends MongoRepository<Guest, String> {
    List<Guest> findByName(String name);
}
