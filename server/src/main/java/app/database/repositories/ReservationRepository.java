package app.database.repositories;

import app.database.entities.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation,String> {

    List<Reservation> getReservationsByStartDateBeforeAndIsPayed(LocalDate startDate, Boolean isPayed);

}
