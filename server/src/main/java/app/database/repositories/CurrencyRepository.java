package app.database.repositories;

import app.database.entities.RateModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CurrencyRepository extends MongoRepository<RateModel, String> {

    RateModel findTopByOrderByIdDesc();

}
