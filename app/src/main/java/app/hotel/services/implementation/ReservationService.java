package app.hotel.services.implementation;

import app.database.entities.Reservation;
import app.database.repositories.ReservationRepository;
import app.hotel.services.ReservationServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService implements ReservationServiceInterface {

    private ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Optional<Reservation> find(String id) {
        Optional<Reservation> reservation = this.reservationRepository.findById(id);
        return reservation;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = this.reservationRepository.findAll();
        return reservations;
    }

    @Override
    public Reservation insert( Reservation entity) {
        this.reservationRepository.insert(entity);
        return entity;
    }

    @Override
    public void update( Reservation entity) {
        this.reservationRepository.save(entity);
    }

    @Override
    public void delete( Reservation entity) {
        this.reservationRepository.delete(entity);
    }

    public List<Reservation> getOutdatedNoPaidReservation(){
        return this.reservationRepository.
                getReservationsByStartDateBeforeAndIsPayed(LocalDate.now().plusDays(2), false);
    }
}
