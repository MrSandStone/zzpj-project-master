package app.database;


import app.database.api.StrategyContext;
import app.database.api.strategy.*;
import app.database.exceptions.validations.GuestValidator;
import app.database.exceptions.validations.ReservationValidator;
import app.database.exceptions.validations.RoomValidator;
import app.database.exceptions.validations.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class ServerConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StrategyContext strategyFactory() {
        Set<Exchange> exchanges = new HashSet<>();
        exchanges.add(new USDExchange());
        exchanges.add(new PLNExchange());
        exchanges.add(new AUDExchange());
        exchanges.add(new MXNExchange());
        exchanges.add(new EURExchange());
        exchanges.add(new CADExchange());

        return new StrategyContext(exchanges);
    }

    @Bean
    @Qualifier("guest")
    public Validator<HashMap<String, String>> guestValidator() {
        return new GuestValidator();
    }

    @Bean
    @Qualifier("room")
    public Validator<HashMap<String, String>> roomValidator() {
        return new RoomValidator();
    }

    @Bean
    @Qualifier("reservation")
    public Validator<HashMap<String, String>> reservationValidator() {
        return new ReservationValidator();
    }

}
