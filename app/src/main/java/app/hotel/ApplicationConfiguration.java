package app.hotel;

import app.database.ServerConfiguration;
import app.hotel.services.implementation.CurrencyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(ServerConfiguration.class)
public class ApplicationConfiguration {

    ServerConfiguration serverConfiguration;

    public ApplicationConfiguration(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }





}
